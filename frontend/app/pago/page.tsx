'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Label } from '@/components/ui/Label';
import { facturaService, notaCreditoService } from '@/services/api';
import { FacturaDTO, TipoDocumento } from '@/types';
import AlertModal from '@/components/ui/AlertModal';

export default function PagoPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  

  const [mode, setMode] = useState<'payment' | 'creditNote'>('payment');


  const [numeroHabitacion, setNumeroHabitacion] = useState('');
  const [facturas, setFacturas] = useState<FacturaDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hasSearched, setHasSearched] = useState(false);


  const [cnCuit, setCnCuit] = useState('');
  const [cnTipoDocumento, setCnTipoDocumento] = useState('');
  const [cnNumeroDocumento, setCnNumeroDocumento] = useState('');
  const [cnFacturas, setCnFacturas] = useState<FacturaDTO[]>([]);
  const [cnSelectedFacturas, setCnSelectedFacturas] = useState<number[]>([]);
  const [cnLoading, setCnLoading] = useState(false);
  const [cnError, setCnError] = useState('');
  const [cnHasSearched, setCnHasSearched] = useState(false);
  const [cnPdfUrl, setCnPdfUrl] = useState<string | null>(null);
  

  const [alertModal, setAlertModal] = useState<{
    isOpen: boolean;
    title: string;
    message: string;
    variant: 'default' | 'danger';
  }>({
    isOpen: false,
    title: '',
    message: '',
    variant: 'default'
  });

  useEffect(() => {
    const habitacionParam = searchParams.get('habitacion');
    if (habitacionParam) {
      setNumeroHabitacion(habitacionParam);
      
      searchFacturas(habitacionParam);
    }
  }, [searchParams]);

  const searchFacturas = async (habitacion: string) => {
    if (!habitacion) return;
    
    setLoading(true);
    setError('');
    setHasSearched(false);
    try {
      const data = await facturaService.getByRoom(habitacion);
      setFacturas(data);
    } catch (err: any) {
      if (err.response && err.response.status === 404) {
        
        setFacturas([]);
      } else {
        console.error(err);
        setError('Error al buscar facturas. Verifique el número de habitación.');
        setFacturas([]);
      }
    } finally {
      setLoading(false);
      setHasSearched(true);
    }
  };

  const handleSearch = () => {
    searchFacturas(numeroHabitacion);
  };

  const handlePagar = (factura: FacturaDTO) => {
    router.push(`/pago/${factura.id}?total=${factura.total}&fecha=${factura.fecha}&responsable=${factura.razonSocial}&numero=${factura.numero}`);
  };

  
  const handleCnSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    
    
    const hasCuit = !!cnCuit;
    const hasTipo = !!cnTipoDocumento;
    const hasNum = !!cnNumeroDocumento;
    
    if (!hasCuit && !hasTipo && !hasNum) {
      setCnError('Debe ingresar CUIT o Tipo y Número de Documento');
      return;
    }

    if ((hasTipo && !hasNum) || (!hasTipo && hasNum)) {
      setCnError('Si ingresa Tipo de Documento debe ingresar Número, y viceversa.');
      return;
    }

    if (hasCuit) {
      const cuitRegex = /^[0-9]{2}-?[0-9]{8}-?[0-9]$/;
      if (!cuitRegex.test(cnCuit)) {
        setCnError('El CUIT debe tener 11 números y respetar el formato XX-XXXXXXXX-X.');
        return;
      }
    }

    setCnLoading(true);
    setCnError('');
    setCnHasSearched(false);
    setCnSelectedFacturas([]);
    
    try {
      const params: any = {};
      if (cnCuit) params.cuit = cnCuit;
      if (cnTipoDocumento) params.tipoDocumento = cnTipoDocumento;
      if (cnNumeroDocumento) params.numeroDocumento = cnNumeroDocumento;

      const data = await facturaService.obtenerFacturasPorResponsable(params);
      setCnFacturas(data);
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.status === 404) {
        setCnFacturas([]);
      } else {
        setCnError('Error al buscar facturas.');
        setCnFacturas([]);
      }
    } finally {
      setCnLoading(false);
      setCnHasSearched(true);
    }
  };

  const toggleFacturaSelection = (id: number) => {
    setCnSelectedFacturas(prev => 
      prev.includes(id) ? prev.filter(fid => fid !== id) : [...prev, id]
    );
  };

  const calculateTotalSelected = () => {
    return cnFacturas
      .filter(f => cnSelectedFacturas.includes(f.id))
      .reduce((acc, curr) => acc + curr.total, 0);
  };

  const handleCreateCreditNote = async () => {
    if (cnSelectedFacturas.length === 0) return;

    setCnLoading(true);
    try {
      const idNotaCredito = await notaCreditoService.crearNotaCredito(cnSelectedFacturas);
      
      //Generar PDF
      const pdfBlob = await notaCreditoService.generarPDFNotaCredito(idNotaCredito);
      const url = window.URL.createObjectURL(new Blob([pdfBlob], { type: 'application/pdf' }));
      setCnPdfUrl(url);
      
      setAlertModal({
        isOpen: true,
        title: 'Éxito',
        message: 'Nota de crédito generada exitosamente.',
        variant: 'default'
      });
      
   
      setCnFacturas([]);
      setCnSelectedFacturas([]);
      setCnHasSearched(false);
      
    } catch (err: any) {
      console.error(err);
      setAlertModal({
        isOpen: true,
        title: 'Error',
        message: 'Error al generar la nota de crédito.',
        variant: 'danger'
      });
    } finally {
      setCnLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
      <AlertModal 
        isOpen={alertModal.isOpen} 
        onClose={() => setAlertModal(prev => ({ ...prev, isOpen: false }))}
        title={alertModal.title}
        message={alertModal.message}
        variant={alertModal.variant}
      />
      
      <h1 className="text-3xl font-bold mb-6">Gestión de Pagos</h1>
      
      <div className="mb-6 flex gap-4 justify-center">
        <Button 
          variant={mode === 'payment' ? 'default' : 'outline'}
          onClick={() => setMode('payment')}
        >
          Pagar Factura
        </Button>
        <Button 
          variant={mode === 'creditNote' ? 'default' : 'outline'}
          onClick={() => setMode('creditNote')}
        >
          Generar Nota Crédito
        </Button>
      </div>
      
      {mode === 'payment' ? (
        <>
          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Buscar Facturas por Habitación</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-4 items-end">
                <div className="flex-1">
                  <Label htmlFor="habitacion">Número de Habitación</Label>
                  <Input
                    id="habitacion"
                    value={numeroHabitacion}
                    onChange={(e) => {
                      const val = e.target.value;
                      if (/^[0-9]*$/.test(val)) setNumeroHabitacion(val);
                    }}
                    placeholder="Ingrese número de habitación"
                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                  />
                </div>
                <Button onClick={handleSearch} disabled={loading}>
                  {loading ? 'Buscando...' : 'Buscar'}
                </Button>
              </div>
              {error && <p className="text-red-500 mt-2">{error}</p>}
            </CardContent>
          </Card>

          {facturas.length > 0 && (
            <div className="grid gap-4">
              {facturas.map((factura) => (
                <Card key={factura.id}>
                  <CardContent className="p-6 flex justify-between items-center">
                    <div>
                      <p className="font-semibold">Factura #{factura.numero}</p>
                      <p className="text-sm text-gray-500">Fecha: {new Date(factura.fecha).toLocaleDateString()}</p>
                      <p className="text-sm text-gray-500">Responsable: {factura.razonSocial}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-xl font-bold mb-2">${factura.total.toFixed(2)}</p>
                      <Button onClick={() => handlePagar(factura)}>
                        Pagar
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
          
          {hasSearched && facturas.length === 0 && !loading && !error && (
            <p className="text-center text-gray-500">No se encontraron facturas pendientes para esta habitación.</p>
          )}
        </>
      ) : cnPdfUrl ? (
        <Card className="max-w-4xl mx-auto">
          <CardHeader>
            <CardTitle>Nota de Crédito Generada</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="aspect-[1/1.4] w-full border rounded-md overflow-hidden h-[600px]">
              <iframe src={cnPdfUrl} className="w-full h-full" title="Nota de Crédito PDF" />
            </div>
            <div className="flex justify-end space-x-4">
              <a href={cnPdfUrl} download="nota_credito.pdf">
                <Button>Descargar PDF</Button>
              </a>
              <Button onClick={() => {
                setCnPdfUrl(null);
                setCnFacturas([]);
                setCnSelectedFacturas([]);
                setCnHasSearched(false);
              }}>
                Finalizar
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : (
        <>
          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Buscar Facturas para Nota de Crédito</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleCnSearch} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <Label htmlFor="cnCuit">CUIT</Label>
                    <Input
                      id="cnCuit"
                      value={cnCuit}
                      onChange={(e) => {
                        const val = e.target.value;
                        const numbers = val.replace(/\D/g, '');
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
                        setCnCuit(formatted);
                      }}
                      placeholder="CUIT"
                      maxLength={13}
                    />
                  </div>
                  <div>
                    <Label htmlFor="cnTipoDocumento">Tipo Documento</Label>
                    <select
                      id="cnTipoDocumento"
                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                      value={cnTipoDocumento}
                      onChange={(e) => setCnTipoDocumento(e.target.value)}
                    >
                      <option value="">Seleccione...</option>
                      {Object.values(TipoDocumento).map((t) => (
                        <option key={t} value={t}>{t}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <Label htmlFor="cnNumeroDocumento">Número Documento</Label>
                    <Input
                      id="cnNumeroDocumento"
                      value={cnNumeroDocumento}
                      onChange={(e) => {
                        const val = e.target.value.toUpperCase();
                        if (/^[A-Z0-9]*$/.test(val)) setCnNumeroDocumento(val);
                      }}
                      placeholder="Número Documento"
                      maxLength={10}
                    />
                  </div>
                </div>
                <div className="flex justify-end">
                  <Button type="submit" disabled={cnLoading}>
                    {cnLoading ? 'Buscando...' : 'Buscar'}
                  </Button>
                </div>
              </form>
              {cnError && <p className="text-red-500 mt-2">{cnError}</p>}
            </CardContent>
          </Card>

          {cnFacturas.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle>Facturas Encontradas</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="relative w-full overflow-auto">
                  <table className="w-full caption-bottom text-sm">
                    <thead className="[&_tr]:border-b">
                      <tr className="border-b border-slate-200 transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                        <th className="h-12 px-4 text-left align-middle font-bold text-slate-900 w-[50px]"></th>
                        <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Número</th>
                        <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Fecha</th>
                        <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Responsable</th>
                        <th className="h-12 px-4 text-right align-middle font-bold text-slate-900">Total</th>
                      </tr>
                    </thead>
                    <tbody className="[&_tr:last-child]:border-0">
                      {cnFacturas.map((factura) => (
                        <tr 
                          key={factura.id} 
                          className="border-b border-slate-200 transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted cursor-pointer"
                          onClick={() => toggleFacturaSelection(factura.id)}
                        >
                          <td className="p-4 align-middle">
                            <input
                              type="checkbox"
                              checked={cnSelectedFacturas.includes(factura.id)}
                              onChange={() => toggleFacturaSelection(factura.id)}
                              className="h-4 w-4"
                              onClick={(e) => e.stopPropagation()}
                            />
                          </td>
                          <td className="p-4 align-middle">{factura.numero}</td>
                          <td className="p-4 align-middle">{new Date(factura.fecha).toLocaleDateString()}</td>
                          <td className="p-4 align-middle">{factura.razonSocial}</td>
                          <td className="p-4 align-middle text-right">${factura.total.toFixed(2)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                
                <div className="mt-6 flex justify-between items-center border-t pt-4">
                  <div className="text-lg font-bold">
                    Total Seleccionado: ${calculateTotalSelected().toFixed(2)}
                  </div>
                  <div className="flex gap-4">
                    <Button variant="outline" onClick={() => setMode('payment')}>
                      Cancelar
                    </Button>
                    <Button 
                      onClick={handleCreateCreditNote} 
                      disabled={cnSelectedFacturas.length === 0 || cnLoading}
                    >
                      {cnLoading ? 'Procesando...' : 'Aceptar'}
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {cnHasSearched && cnFacturas.length === 0 && !cnLoading && !cnError && (
            <p className="text-center text-gray-500 mt-4">No se encontraron facturas para los criterios ingresados.</p>
          )}
        </>
      )}
    </div>
  );
}
