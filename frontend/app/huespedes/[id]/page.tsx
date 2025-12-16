'use client';

import { useEffect, useState, useRef } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { huespedService } from '@/services/api';
import { Huesped, TipoDocumento, IVA } from '@/types';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '@/components/ui/Card';
import AlertModal from '@/components/ui/AlertModal';
import Modal from '@/components/ui/Modal';

export default function EditarHuespedPage() {
  const router = useRouter();
  const params = useParams();
  const id = Number(params.id);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const [showCancelModal, setShowCancelModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  
  const tipoDocumentoRef = useRef<HTMLSelectElement>(null);

  const [formData, setFormData] = useState<Huesped | null>(null);

  useEffect(() => {
    const fetchHuesped = async () => {
      if (!id || isNaN(id)) return;
      
      try {
        const data = await huespedService.getById(id);
        setFormData(data);
      } catch (err: any) {
        console.error('Error fetching guest:', err);
        const errorMessage = err.response?.data?.message || err.message || 'Error al cargar el huésped';
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };
    fetchHuesped();
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    if (!formData) return;
    const { name, value } = e.target;
    let newValue = value;

    if (name !== 'email' && (e.target.type === 'text' || e.target.tagName === 'SELECT')) {
      newValue = value.toUpperCase();
    }

    if (['nombres', 'apellido', 'nacionalidad', 'ocupacion'].includes(name)) {
      if (!/^[A-ZÁÉÍÓÚÑ\s]*$/.test(newValue)) return;
    }
    
    if (name === 'telefono') {
      if (!/^[0-9]*$/.test(newValue)) return;
    }

    if (name === 'numeroDocumento') {
      if (!/^[A-Z0-9]*$/.test(newValue)) return;
    }

    if (name === 'cuit') {
      const numbers = newValue.replace(/\D/g, '');
      //Limite de cuit 11 digitos (2-8-1 es el formato)
      const limitedNumbers = numbers.slice(0, 11);
      
      let formatted = '';
      if (limitedNumbers.length > 0) {
        formatted = limitedNumbers.slice(0, 2);
      }
      if (limitedNumbers.length > 2) {
        formatted += '-' + limitedNumbers.slice(2, 10);
      }
      if (limitedNumbers.length > 10) {
        formatted += '-' + limitedNumbers.slice(10);
      }
      newValue = formatted;
    }

    if (name === 'fechaDeNacimiento') {
      const selectedDate = new Date(newValue);
      const today = new Date();
      //Resetear hora para comparar solo fechas
      today.setHours(0, 0, 0, 0);
      if (selectedDate >= today) {
        return;
      }
    }

    setFormData((prev) => prev ? ({ ...prev, [name]: newValue }) : null);
  };

  const handleDireccionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!formData) return;
    const { name, value } = e.target;
    let newValue = value.toUpperCase();

    if (['calle', 'localidad'].includes(name)) {
      if (!/^[A-ZÁÉÍÓÚÑ0-9\s]*$/.test(newValue)) return;
    }
    if (['provincia', 'pais'].includes(name)) {
      if (!/^[A-ZÁÉÍÓÚÑ\s]*$/.test(newValue)) return;
    }
    if (['numero', 'codigoPostal', 'piso'].includes(name)) {
      if (!/^[0-9]*$/.test(newValue)) return;
    }
    if (name === 'departamento') {
       if (!/^[A-Z0-9]*$/.test(newValue)) return;
    }

    setFormData((prev) => prev ? ({
      ...prev,
      direccion: { ...prev.direccion, [name]: newValue },
    }) : null);
  };

  const sanitizeData = (data: Huesped): Huesped => {
    const sanitized = { ...data };
    
    //Convertir strings vacíos a null
    (Object.keys(sanitized) as Array<keyof Huesped>).forEach(key => {
      if (typeof sanitized[key] === 'string' && (sanitized[key] as string).trim() === '') {
        (sanitized as any)[key] = null;
      }
    });

    
    if (sanitized.direccion) {
      const sanitizedDireccion = { ...sanitized.direccion };
      (Object.keys(sanitizedDireccion) as Array<keyof typeof sanitizedDireccion>).forEach(key => {
        if (typeof sanitizedDireccion[key] === 'string' && (sanitizedDireccion[key] as string).trim() === '') {
          (sanitizedDireccion as any)[key] = null;
        }
      });
      sanitized.direccion = sanitizedDireccion;
    }

    return sanitized;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData) return;

    //Validaciones adicionales
    if (formData.posicionFrenteAlIVA === IVA.RESPONSABLE_INSCRIPTO && !formData.cuit) {
      setErrorMessage('El CUIT es obligatorio para Responsable Inscripto.');
      setShowErrorModal(true);
      return;
    }

    if (formData.cuit) {
      const cuitRegex = /^[0-9]{2}-?[0-9]{8}-?[0-9]$/;
      if (!cuitRegex.test(formData.cuit)) {
        setErrorMessage('El CUIT debe tener 11 números y respetar el formato XX-XXXXXXXX-X.');
        setShowErrorModal(true);
        return;
      }
    }

    if (formData.tipoDocumento === TipoDocumento.DNI && formData.cuit && formData.numeroDocumento) {
      const cuitNumbers = formData.cuit.replace(/\D/g, '');
      const dniNumbers = formData.numeroDocumento.replace(/\D/g, '');
      //El DNI debe estar contenido en el CUIT (posiciones 2 a 10 aprox, o simplemente incluido)
      //Formato CUIT: XX-DNI-X. 
      //Verificamos si el CUIT contiene el DNI.
      if (!cuitNumbers.includes(dniNumbers)) {
        setErrorMessage('El CUIT no coincide con el DNI ingresado.');
        setShowErrorModal(true);
        return;
      }
    }

    setSaving(true);
    setError('');
    try {
      const dataToSend = sanitizeData(formData);
      const result = await huespedService.update(id, dataToSend);
      
      if (result.success) {
        setShowSuccessModal(true);
      } else if (result.status === 409) {
         const errorData = result.message;
         const errorMessage = typeof errorData === 'object' && errorData !== null 
            ? (errorData.message || JSON.stringify(errorData))
            : (errorData || 'El DNI ya existe en el sistema.');
         setErrorMessage(errorMessage);
         setShowErrorModal(true);
      }
    } catch (err: any) {
      console.error(err);
      const errorData = err.response?.data;
      const errorMessage = typeof errorData === 'object' && errorData !== null 
        ? (errorData.message || JSON.stringify(errorData))
        : (errorData || err.message || 'Error al actualizar el huésped');
      setError(errorMessage);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setShowCancelModal(true);
  };

  const confirmCancel = () => {
    setShowCancelModal(false);
    router.push('/huespedes');
  };

  if (loading) return <div className="container mx-auto py-10 text-center">Cargando...</div>;
  if (error) return <div className="container mx-auto py-10 text-center text-red-500">Error: {error}</div>;
  if (!formData) return <div className="container mx-auto py-10 text-center">Huésped no encontrado</div>;

  return (
    <div className="container mx-auto py-10">
      <Card className="max-w-4xl mx-auto">
        <CardHeader>
          <CardTitle>Modificar huesped</CardTitle>
        </CardHeader>
        <form onSubmit={handleSubmit}>
          <CardContent className="grid gap-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="nombres">Nombres</Label>
                <Input id="nombres" name="nombres" value={formData.nombres || ''} onChange={handleChange} required maxLength={50} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="apellido">Apellido</Label>
                <Input id="apellido" name="apellido" value={formData.apellido || ''} onChange={handleChange} required maxLength={50} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="tipoDocumento">Tipo Documento</Label>
                <select
                  id="tipoDocumento"
                  name="tipoDocumento"
                  ref={tipoDocumentoRef}
                  className="flex h-10 w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-sm ring-offset-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-950 focus-visible:ring-offset-2"
                  value={formData.tipoDocumento}
                  onChange={handleChange}
                >
                  {Object.values(TipoDocumento).map((tipo) => (
                    <option key={tipo} value={tipo}>{tipo}</option>
                  ))}
                </select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="numeroDocumento">Número Documento</Label>
                <Input id="numeroDocumento" name="numeroDocumento" value={formData.numeroDocumento || ''} onChange={handleChange} required maxLength={10} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="cuit">CUIT</Label>
                <Input id="cuit" name="cuit" value={formData.cuit || ''} onChange={handleChange} maxLength={20} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="posicionFrenteAlIVA">Posición frente al IVA</Label>
                <select
                  id="posicionFrenteAlIVA"
                  name="posicionFrenteAlIVA"
                  className="flex h-10 w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-sm ring-offset-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-950 focus-visible:ring-offset-2"
                  value={formData.posicionFrenteAlIVA}
                  onChange={handleChange}
                >
                  {Object.values(IVA).map((iva) => (
                    <option key={iva} value={iva}>{iva.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="fechaDeNacimiento">Fecha de Nacimiento</Label>
                <Input type="date" id="fechaDeNacimiento" name="fechaDeNacimiento" value={formData.fechaDeNacimiento || ''} onChange={handleChange} required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="telefono">Teléfono</Label>
                <Input id="telefono" name="telefono" value={formData.telefono || ''} onChange={handleChange} required maxLength={20} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input type="email" id="email" name="email" value={formData.email || ''} onChange={handleChange} maxLength={100} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="ocupacion">Ocupación</Label>
                <Input id="ocupacion" name="ocupacion" value={formData.ocupacion || ''} onChange={handleChange} maxLength={50} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="nacionalidad">Nacionalidad</Label>
                <Input id="nacionalidad" name="nacionalidad" value={formData.nacionalidad || ''} onChange={handleChange} required maxLength={85} />
              </div>
            </div>

            <div className="border-t pt-4">
              <h3 className="text-lg font-medium mb-4">Dirección</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="calle">Calle</Label>
                  <Input id="calle" name="calle" value={formData.direccion?.calle || ''} onChange={handleDireccionChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="numero">Número</Label>
                  <Input id="numero" name="numero" value={formData.direccion?.numero || ''} onChange={handleDireccionChange} required maxLength={10} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="piso">Piso</Label>
                  <Input id="piso" name="piso" value={formData.direccion?.piso || ''} onChange={handleDireccionChange} maxLength={5} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="departamento">Departamento</Label>
                  <Input id="departamento" name="departamento" value={formData.direccion?.departamento || ''} onChange={handleDireccionChange} maxLength={5} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="codigoPostal">Código Postal</Label>
                  <Input id="codigoPostal" name="codigoPostal" value={formData.direccion?.codigoPostal || ''} onChange={handleDireccionChange} required maxLength={10} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="localidad">Localidad</Label>
                  <Input id="localidad" name="localidad" value={formData.direccion?.localidad || ''} onChange={handleDireccionChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="provincia">Provincia</Label>
                  <Input id="provincia" name="provincia" value={formData.direccion?.provincia || ''} onChange={handleDireccionChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="pais">País</Label>
                  <Input id="pais" name="pais" value={formData.direccion?.pais || ''} onChange={handleDireccionChange} required maxLength={50} />
                </div>
              </div>
            </div>

            {error && <p className="text-sm text-red-500">{error}</p>}
          </CardContent>
          <CardFooter className="flex justify-end gap-4">
            <Button type="button" variant="outline" onClick={handleCancel}>Cancelar</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</Button>
          </CardFooter>
        </form>
      </Card>

      <AlertModal
        isOpen={showErrorModal}
        onClose={() => setShowErrorModal(false)}
        title="Error de Validación"
        message={errorMessage}
        variant="danger"
      />

      <Modal
        isOpen={showCancelModal}
        onClose={() => setShowCancelModal(false)}
        title="Confirmar Cancelación"
      >
        <div className="p-4 space-y-4">
          <p className="text-slate-600">¿Desea cancelar la modificación del huésped?</p>
          <div className="flex justify-end gap-4">
            <Button variant="outline" onClick={() => setShowCancelModal(false)}>
              NO
            </Button>
            <Button variant="destructive" onClick={confirmCancel}>
              SI
            </Button>
          </div>
        </div>
      </Modal>

      <AlertModal
        isOpen={showSuccessModal}
        onClose={() => {
          setShowSuccessModal(false);
          router.push('/huespedes');
        }}
        title="Éxito"
        message="La operación ha culminado con éxito"
        variant="primary"
      />
    </div>
  );
}
