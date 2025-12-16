'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams, useSearchParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Label } from '@/components/ui/Label';
import { pagoService } from '@/services/api';
import { TipoPagoDTO, MetodoPago, TipoMoneda } from '@/types';
import AlertModal from '@/components/ui/AlertModal';

export default function DetallePagoPage() {
  const params = useParams();
  const searchParams = useSearchParams();
  const router = useRouter();
  
  const idFactura = Number(params.id);
  const totalFactura = Number(searchParams.get('total') || 0);
  const fechaFactura = searchParams.get('fecha');
  const responsable = searchParams.get('responsable');
  const numeroFactura = searchParams.get('numero');

  const [pagos, setPagos] = useState<TipoPagoDTO[]>([]);
  const [nuevoPago, setNuevoPago] = useState<Partial<TipoPagoDTO>>({
    metodoPago: MetodoPago.MONEDA,
    importe: 0,
    tipoMoneda: TipoMoneda.ARS
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [alertModal, setAlertModal] = useState({ isOpen: false, title: '', message: '', variant: 'default' as 'default' | 'danger' | 'primary' });
  const [cotizacionLoading, setCotizacionLoading] = useState(false);

  const totalPagado = pagos.reduce((acc, pago) => {
    const monto = Number(pago.importe) || 0;
    const cotiz = pago.tipoMoneda !== TipoMoneda.ARS ? (Number(pago.cotizacion) || 1) : 1;
    return acc + (monto * cotiz);
  }, 0);
  const saldoRestante = totalFactura - totalPagado;

  useEffect(() => {
    const cotiz = Number(nuevoPago.cotizacion) || 1;
    const saldo = saldoRestante > 0 ? saldoRestante : 0;

    const nuevoImporte = nuevoPago.tipoMoneda === TipoMoneda.ARS ? saldo : (Math.ceil((saldo / cotiz) * 100) / 100);
    setNuevoPago(prev => ({ ...prev, importe: nuevoImporte }));
  }, [saldoRestante, nuevoPago.tipoMoneda, nuevoPago.cotizacion]);

  const fetchCotizacion = async (moneda: TipoMoneda) => {
    if (moneda === TipoMoneda.ARS) return 1;
    setCotizacionLoading(true);
    try {
      let url = '';
      switch (moneda) {
        case TipoMoneda.USD: url = 'https://dolarapi.com/v1/dolares/oficial'; break;
        case TipoMoneda.EUR: url = 'https://dolarapi.com/v1/cotizaciones/eur'; break;
        case TipoMoneda.BRL: url = 'https://dolarapi.com/v1/cotizaciones/brl'; break;
        case TipoMoneda.UYU: url = 'https://dolarapi.com/v1/cotizaciones/uyu'; break;
        default: return 1;
      }
      const res = await fetch(url);
      const data = await res.json();
      return data.compra || 1;
    } catch (e) {
      console.error(e);
      return 1;
    } finally {
      setCotizacionLoading(false);
    }
  };

  const handleInputChange = async (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'tipoMoneda') {
        const moneda = value as TipoMoneda;
        let cotiz = 1;
        if (moneda !== TipoMoneda.ARS) {
            cotiz = await fetchCotizacion(moneda);
        }
        setNuevoPago(prev => ({
            ...prev,
            tipoMoneda: moneda,
            cotizacion: cotiz
        }));
    } else if (name === 'metodoPago') {
      
      setNuevoPago(prev => ({
        metodoPago: value as MetodoPago,
        importe: prev.importe,
        tipoMoneda: prev.tipoMoneda,
        
        numeroTarjeta: undefined,
        titular: undefined,
        fechaVencimiento: undefined,
        codigoSeguridad: undefined,
        numeroCheque: undefined,
        bancoEmisor: undefined,
        plaza: undefined,
        fechaCobro: undefined
      }));
      setFormErrors({});
    } else {
      let newValue = value;

      
      if (name === 'numeroTarjeta' || name === 'numeroCheque') {
        if (!/^[0-9]*$/.test(newValue)) return;
        if (newValue.length > 20) return;
      }

      if (name === 'titular') {
        newValue = newValue.toUpperCase();
        
        if (!/^[A-Z\s]*$/.test(newValue)) return;
        if (newValue.length > 50) return;
      }

      if (name === 'codigoSeguridad') {
        if (!/^[0-9]*$/.test(newValue)) return;
        if (newValue.length > 4) return;
      }

      if (name === 'bancoEmisor') {
        newValue = newValue.toUpperCase();
        if (newValue.length > 50) return;
      }

      setNuevoPago(prev => ({
        ...prev,
        [name]: newValue
      }));
      
      if (formErrors[name]) {
        setFormErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors[name];
          return newErrors;
        });
      }
    }
  };

  const showAlert = (title: string, message: string, variant: 'default' | 'danger' | 'primary' = 'default') => {
    setAlertModal({ isOpen: true, title, message, variant });
  };

  const agregarPago = () => {
    const errors: Record<string, string> = {};

    if (!nuevoPago.importe || Number(nuevoPago.importe) <= 0) {
      errors.importe = 'El importe debe ser mayor a 0';
    }
    
   
    if (nuevoPago.metodoPago === MetodoPago.TARJETA_CREDITO || nuevoPago.metodoPago === MetodoPago.TARJETA_DEBITO) {
      if (!nuevoPago.numeroTarjeta) errors.numeroTarjeta = 'Requerido';
      if (!nuevoPago.titular) errors.titular = 'Requerido';
      if (!nuevoPago.codigoSeguridad) errors.codigoSeguridad = 'Requerido';
      if (!nuevoPago.fechaVencimiento) {
        errors.fechaVencimiento = 'Requerido';
      } else {
        const [year, month, day] = nuevoPago.fechaVencimiento.split('-').map(Number);
        const fechaVencimientoDate = new Date(year, month - 1, day);
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        if (fechaVencimientoDate <= hoy) {
          errors.fechaVencimiento = 'La fecha de vencimiento debe ser posterior a hoy';
        }
      }
    }
    
    if (nuevoPago.metodoPago === MetodoPago.CHEQUE) {
      if (!nuevoPago.numeroCheque) errors.numeroCheque = 'Requerido';
      if (!nuevoPago.bancoEmisor) errors.bancoEmisor = 'Requerido';
      if (!nuevoPago.fechaCobro) {
        errors.fechaCobro = 'Requerido';
      } else {
        const fechaCobro = new Date(nuevoPago.fechaCobro);
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        const [year, month, day] = nuevoPago.fechaCobro.split('-').map(Number);
        const fechaCobroDate = new Date(year, month - 1, day);
        
        if (fechaCobroDate < hoy) {
          errors.fechaCobro = 'La fecha de cobro debe ser igual o posterior a hoy';
        }
      }
    }

    if (nuevoPago.metodoPago !== MetodoPago.MONEDA) {
      const cotiz = nuevoPago.tipoMoneda !== TipoMoneda.ARS ? (Number(nuevoPago.cotizacion) || 1) : 1;
      const importeEnArs = Number(nuevoPago.importe) * cotiz;
      
      if (importeEnArs > saldoRestante + 0.01) {
        errors.importe = 'Solo el pago en efectivo puede exceder el saldo restante';
      }
    }

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    setPagos([...pagos, nuevoPago as TipoPagoDTO]);
    
    
    setNuevoPago({
      metodoPago: MetodoPago.MONEDA,
      importe: 0, 
      tipoMoneda: TipoMoneda.ARS,
      cotizacion: 1
    });
    setFormErrors({});
  };

  const eliminarPago = (index: number) => {
    const nuevosPagos = [...pagos];
    nuevosPagos.splice(index, 1);
    setPagos(nuevosPagos);
  };

  const finalizarPago = async () => {
    if (saldoRestante > 0) {
      showAlert('Saldo Pendiente', 'Debe cubrir el total de la factura', 'danger');
      return;
    }

    
    let pagosAEnviar = pagos.map(p => ({ ...p }));
    const totalPagadoCalculado = pagosAEnviar.reduce((acc, p) => {
        const cotiz = p.tipoMoneda !== TipoMoneda.ARS ? (Number(p.cotizacion) || 1) : 1;
        return acc + (Number(p.importe) * cotiz);
    }, 0);
    
    if (totalPagadoCalculado > totalFactura) {
      const excedente = totalPagadoCalculado - totalFactura;
      
      const cashPaymentIndex = pagosAEnviar.findIndex(p => p.metodoPago === MetodoPago.MONEDA);
      
      if (cashPaymentIndex !== -1) {
       
        const pago = pagosAEnviar[cashPaymentIndex];
        const cotiz = pago.tipoMoneda !== TipoMoneda.ARS ? (Number(pago.cotizacion) || 1) : 1;
        const excedenteEnMoneda = excedente / cotiz;
        
        pagosAEnviar[cashPaymentIndex].importe = Number(pagosAEnviar[cashPaymentIndex].importe) - excedenteEnMoneda;
      }
    }

    
    pagosAEnviar = pagosAEnviar.map(p => {
      const cotiz = p.tipoMoneda !== TipoMoneda.ARS ? (Number(p.cotizacion) || 1) : 1;
      return {
        ...p,
        importe: Number((Number(p.importe) * cotiz).toFixed(2))
      };
    });

    setLoading(true);
    setError('');
    try {
      await pagoService.pagar(idFactura, pagosAEnviar);
      showAlert('Éxito', 'Pago registrado exitosamente', 'default');
      
    } catch (err) {
      console.error(err);
      showAlert('Error', 'Error al procesar el pago', 'danger');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
      <AlertModal 
        isOpen={alertModal.isOpen} 
        onClose={() => {
          setAlertModal(prev => ({ ...prev, isOpen: false }));
          if (alertModal.title === 'Éxito') {
            router.push('/pago');
          }
        }}
        title={alertModal.title}
        message={alertModal.message}
        variant={alertModal.variant}
      />
      <h1 className="text-3xl font-bold mb-6">Registrar Pago</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {}
        <Card>
          <CardHeader>
            <CardTitle>Detalle de Factura #{numeroFactura}</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <p><strong>Fecha:</strong> {fechaFactura ? new Date(fechaFactura).toLocaleDateString() : '-'}</p>
              <p><strong>Responsable:</strong> {responsable}</p>
              <div className="border-t pt-2 mt-4">
                <p className="text-lg"><strong>Total Factura:</strong> ${totalFactura.toFixed(2)}</p>
                <p className="text-lg text-green-600"><strong>Total Pagado:</strong> ${totalPagado.toFixed(2)}</p>
                <p className={`text-lg ${saldoRestante > 0 ? 'text-red-600' : 'text-blue-600'}`}>
                  <strong>{saldoRestante > 0 ? 'Saldo Restante:' : 'Vuelto:'}</strong> ${Math.abs(saldoRestante).toFixed(2)}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {}
        <Card>
          <CardHeader>
            <CardTitle>Agregar Medio de Pago</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="metodoPago">Método de Pago</Label>
              <select
                id="metodoPago"
                name="metodoPago"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={nuevoPago.metodoPago}
                onChange={handleInputChange}
              >
                {Object.values(MetodoPago).map((metodo) => (
                  <option key={metodo} value={metodo}>{metodo.replace('_', ' ')}</option>
                ))}
              </select>
            </div>

            <div>
              <Label htmlFor="importe">Importe</Label>
              <Input
                id="importe"
                name="importe"
                type="number"
                value={nuevoPago.importe}
                onChange={handleInputChange}
                className={formErrors.importe ? 'border-red-500' : ''}
              />
              {formErrors.importe && <p className="text-red-500 text-sm mt-1">{formErrors.importe}</p>}
            </div>

            <div>
              <Label htmlFor="tipoMoneda">Moneda</Label>
              <select
                id="tipoMoneda"
                name="tipoMoneda"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={nuevoPago.tipoMoneda}
                onChange={handleInputChange}
              >
                {Object.values(TipoMoneda).map((moneda) => (
                  <option key={moneda} value={moneda}>{moneda}</option>
                ))}
              </select>
            </div>

            {nuevoPago.tipoMoneda !== TipoMoneda.ARS && (
              <div>
                <Label htmlFor="cotizacion">Cotización (ARS)</Label>
                <div className="relative">
                  <Input
                    id="cotizacion"
                    name="cotizacion"
                    type="number"
                    value={nuevoPago.cotizacion || ''}
                    onChange={handleInputChange}
                    disabled={cotizacionLoading}
                  />
                  {cotizacionLoading && (
                    <span className="absolute right-3 top-2 text-xs text-gray-500">Cargando...</span>
                  )}
                </div>
              </div>
            )}

            {(nuevoPago.metodoPago === MetodoPago.TARJETA_CREDITO || nuevoPago.metodoPago === MetodoPago.TARJETA_DEBITO) && (
              <>
                <div>
                  <Label htmlFor="numeroTarjeta">Número de Tarjeta</Label>
                  <Input
                    id="numeroTarjeta"
                    name="numeroTarjeta"
                    value={nuevoPago.numeroTarjeta || ''}
                    onChange={handleInputChange}
                    className={formErrors.numeroTarjeta ? 'border-red-500' : ''}
                  />
                  {formErrors.numeroTarjeta && <p className="text-red-500 text-sm mt-1">{formErrors.numeroTarjeta}</p>}
                </div>
                <div>
                  <Label htmlFor="titular">Titular</Label>
                  <Input
                    id="titular"
                    name="titular"
                    value={nuevoPago.titular || ''}
                    onChange={handleInputChange}
                    className={formErrors.titular ? 'border-red-500' : ''}
                  />
                  {formErrors.titular && <p className="text-red-500 text-sm mt-1">{formErrors.titular}</p>}
                </div>
                <div>
                  <Label htmlFor="fechaVencimiento">Fecha de Vencimiento</Label>
                  <Input
                    id="fechaVencimiento"
                    name="fechaVencimiento"
                    type="date"
                    value={nuevoPago.fechaVencimiento || ''}
                    onChange={handleInputChange}
                    className={formErrors.fechaVencimiento ? 'border-red-500' : ''}
                  />
                  {formErrors.fechaVencimiento && <p className="text-red-500 text-sm mt-1">{formErrors.fechaVencimiento}</p>}
                </div>
                <div>
                  <Label htmlFor="codigoSeguridad">Código de Seguridad</Label>
                  <Input
                    id="codigoSeguridad"
                    name="codigoSeguridad"
                    value={nuevoPago.codigoSeguridad || ''}
                    onChange={handleInputChange}
                    className={formErrors.codigoSeguridad ? 'border-red-500' : ''}
                    maxLength={4}
                  />
                  {formErrors.codigoSeguridad && <p className="text-red-500 text-sm mt-1">{formErrors.codigoSeguridad}</p>}
                </div>
              </>
            )}

            {nuevoPago.metodoPago === MetodoPago.CHEQUE && (
              <>
                <div>
                  <Label htmlFor="numeroCheque">Número de Cheque</Label>
                  <Input
                    id="numeroCheque"
                    name="numeroCheque"
                    value={nuevoPago.numeroCheque || ''}
                    onChange={handleInputChange}
                    className={formErrors.numeroCheque ? 'border-red-500' : ''}
                  />
                  {formErrors.numeroCheque && <p className="text-red-500 text-sm mt-1">{formErrors.numeroCheque}</p>}
                </div>
                <div>
                  <Label htmlFor="bancoEmisor">Banco Emisor</Label>
                  <Input
                    id="bancoEmisor"
                    name="bancoEmisor"
                    value={nuevoPago.bancoEmisor || ''}
                    onChange={handleInputChange}
                    className={formErrors.bancoEmisor ? 'border-red-500' : ''}
                  />
                  {formErrors.bancoEmisor && <p className="text-red-500 text-sm mt-1">{formErrors.bancoEmisor}</p>}
                </div>
                <div>
                  <Label htmlFor="fechaCobro">Fecha de Cobro</Label>
                  <Input
                    id="fechaCobro"
                    name="fechaCobro"
                    type="date"
                    value={nuevoPago.fechaCobro || ''}
                    onChange={handleInputChange}
                    className={formErrors.fechaCobro ? 'border-red-500' : ''}
                  />
                  {formErrors.fechaCobro && <p className="text-red-500 text-sm mt-1">{formErrors.fechaCobro}</p>}
                </div>
              </>
            )}

            <Button onClick={agregarPago} className="w-full">Agregar Pago</Button>
          </CardContent>
        </Card>
      </div>

      {}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Pagos Agregados</CardTitle>
        </CardHeader>
        <CardContent>
          {pagos.length === 0 ? (
            <p className="text-gray-500">No hay pagos agregados.</p>
          ) : (
            <div className="space-y-2">
              {pagos.map((pago, index) => (
                <div key={index} className="flex justify-between items-center p-3 border rounded bg-gray-50">
                  <div>
                    <span className="font-bold">{pago.metodoPago}</span> - {pago.tipoMoneda} ${Number(pago.importe).toFixed(2)}
                    {pago.numeroTarjeta && <span className="text-sm text-gray-500 ml-2">(Tarj: ...{pago.numeroTarjeta.slice(-4)})</span>}
                    {pago.numeroCheque && <span className="text-sm text-gray-500 ml-2">(Cheque: {pago.numeroCheque})</span>}
                  </div>
                  <Button variant="destructive" size="sm" onClick={() => eliminarPago(index)}>Eliminar</Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <div className="mt-6 flex justify-end">
        <Button 
          size="lg" 
          onClick={finalizarPago} 
          disabled={saldoRestante > 0 || loading}
          className="w-full md:w-auto"
        >
          {loading ? 'Procesando...' : 'Finalizar y Registrar Pago'}
        </Button>
      </div>
      {error && <p className="text-red-500 mt-2 text-right">{error}</p>}
    </div>
  );
}
