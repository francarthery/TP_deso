'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { responsablePagoService } from '@/services/api';
import { PersonaJuridicaDTO } from '@/types';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import AlertModal from '@/components/ui/AlertModal';

export default function NuevoResponsablePagoPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const [formData, setFormData] = useState<PersonaJuridicaDTO>({
    razonSocial: '',
    cuit: '',
    telefono: '',
    direccion: {
      calle: '',
      numero: '',
      departamento: '',
      piso: '',
      codigoPostal: '',
      localidad: '',
      provincia: '',
      pais: '',
    },
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    let newValue = value.toUpperCase();

    if (name === 'razonSocial') {
       if (!/^[A-Z0-9ÑÁÉÍÓÚÜ.,&\- ]*$/.test(newValue)) return;
    }

    if (name === 'telefono') {
      if (!/^[0-9]*$/.test(newValue)) return;
      if (newValue.length > 20) return;
    }

    if (name === 'cuit') {
      const numbers = newValue.replace(/\D/g, '');
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

    setFormData((prev) => ({ ...prev, [name]: newValue }));
  };

  const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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

    setFormData((prev) => ({
      ...prev,
      direccion: { ...prev.direccion, [name]: newValue },
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.cuit) {
      const cuitRegex = /^[0-9]{2}-?[0-9]{8}-?[0-9]$/;
      if (!cuitRegex.test(formData.cuit)) {
        setError('El CUIT debe tener 11 números y respetar el formato XX-XXXXXXXX-X.');
        setShowErrorModal(true);
        return;
      }
    }

    setLoading(true);
    setError('');

    try {
      await responsablePagoService.darAltaPersonaJuridica(formData);
      setShowSuccessModal(true);
    } catch (err: any) {
      if (err.response && err.response.status === 409) {
        setError('El CUIT ingresado ya existe en el sistema.');
      } else {
        setError(err.response?.data || 'Error al crear el responsable de pago');
      }
      setShowErrorModal(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto py-10">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Nuevo Responsable de Pago (Persona Jurídica)</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                <Label htmlFor="razonSocial">Razón Social</Label>
                <Input
                    id="razonSocial"
                    name="razonSocial"
                    value={formData.razonSocial}
                    onChange={handleChange}
                    required
                    maxLength={100}
                />
                </div>
                <div className="space-y-2">
                <Label htmlFor="cuit">CUIT</Label>
                <Input
                    id="cuit"
                    name="cuit"
                    value={formData.cuit}
                    onChange={handleChange}
                    required
                    placeholder="XX-XXXXXXXX-X"
                />
                </div>
                <div className="space-y-2">
                <Label htmlFor="telefono">Teléfono</Label>
                <Input
                    id="telefono"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleChange}
                    required
                />
                </div>
            </div>

            <div className="space-y-2">
              <h3 className="font-medium">Dirección</h3>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="calle">Calle</Label>
                  <Input id="calle" name="calle" value={formData.direccion.calle} onChange={handleAddressChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="numero">Número</Label>
                  <Input id="numero" name="numero" value={formData.direccion.numero} onChange={handleAddressChange} required maxLength={10} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="departamento">Departamento</Label>
                  <Input id="departamento" name="departamento" value={formData.direccion.departamento} onChange={handleAddressChange} maxLength={5} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="piso">Piso</Label>
                  <Input id="piso" name="piso" value={formData.direccion.piso} onChange={handleAddressChange} maxLength={5} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="codigoPostal">Código Postal</Label>
                  <Input id="codigoPostal" name="codigoPostal" value={formData.direccion.codigoPostal} onChange={handleAddressChange} required maxLength={10} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="localidad">Localidad</Label>
                  <Input id="localidad" name="localidad" value={formData.direccion.localidad} onChange={handleAddressChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="provincia">Provincia</Label>
                  <Input id="provincia" name="provincia" value={formData.direccion.provincia} onChange={handleAddressChange} required maxLength={50} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="pais">País</Label>
                  <Input id="pais" name="pais" value={formData.direccion.pais} onChange={handleAddressChange} required maxLength={50} />
                </div>
              </div>
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Guardando...' : 'Guardar'}
            </Button>
          </form>
        </CardContent>
      </Card>

      <AlertModal
        isOpen={showErrorModal}
        onClose={() => setShowErrorModal(false)}
        title="Error"
        message={error}
      />
      
      <AlertModal
        isOpen={showSuccessModal}
        onClose={() => {
            setShowSuccessModal(false);
            
        }}
        title="Éxito"
        message="Responsable de pago creado exitosamente."
      />
    </div>
  );
}
