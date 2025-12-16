'use client';

import { useState, useEffect, Suspense } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { Button } from '@/components/ui/Button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { estadiaService, facturacionService, facturaService } from '@/services/api';
import { ConsumoDTO, DatosFacturaDTO, FacturaDTO } from '@/types';
import AlertModal from '@/components/ui/AlertModal';

function ConsumosContent() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const estadiaId = searchParams.get('estadiaId');
  const responsableId = searchParams.get('responsableId');
  const habitacion = searchParams.get('habitacion');

  const [consumos, setConsumos] = useState<ConsumoDTO[]>([]);
  const [selectedConsumoIds, setSelectedConsumoIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [facturaGenerada, setFacturaGenerada] = useState<FacturaDTO | null>(null);

  useEffect(() => {
    const fetchConsumos = async () => {
      if (!estadiaId) {
        setError('Falta ID de estadía.');
        setShowErrorModal(true);
        setLoading(false);
        return;
      }

      try {
        const data = await estadiaService.getConsumos(Number(estadiaId));
        setConsumos(data);
      } catch (err: any) {
        console.error(err);
        setError('Error al cargar consumos.');
        setShowErrorModal(true);
      } finally {
        setLoading(false);
      }
    };

    fetchConsumos();
  }, [estadiaId]);

  const toggleConsumo = (id: number) => {
    setSelectedConsumoIds(prev => 
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    );
  };

  const handleGenerateFactura = async () => {
    if (selectedConsumoIds.length === 0) {
      setError('Debe seleccionar al menos un consumo.');
      setShowErrorModal(true);
      return;
    }

    if (!responsableId || !estadiaId) {
      setError('Faltan datos para facturar.');
      setShowErrorModal(true);
      return;
    }

    setLoading(true);
    try {
      const datos: DatosFacturaDTO = {
        fecha: new Date().toISOString().split('T')[0],
        idEstadia: Number(estadiaId),
        idConsumos: selectedConsumoIds,
        idResponsablePago: Number(responsableId)
      };

      const idFactura = await facturacionService.generarFactura(datos);
      
      
      const pdfBlob = await facturacionService.getFacturaPdf(idFactura);
      const url = window.URL.createObjectURL(new Blob([pdfBlob], { type: 'application/pdf' }));
      setPdfUrl(url);

     
      if (habitacion) {
        try {
          const facturas = await facturaService.getByRoom(habitacion);
          const factura = facturas.find(f => f.id === idFactura);
          if (factura) {
            setFacturaGenerada(factura);
          }
        } catch (e) {
          console.error("Could not fetch invoice details", e);
        }
      }
    } catch (err: any) {
      console.error(err);
      setError('Error al generar la factura.');
      setShowErrorModal(true);
    } finally {
      setLoading(false);
    }
  };

  const totalSelected = consumos
    .filter(c => selectedConsumoIds.includes(c.id))
    .reduce((sum, c) => sum + (c.monto * (c.cantidad || 1)), 0);

  if (pdfUrl) {
    return (
      <div className="container mx-auto py-10">
        <Card className="max-w-4xl mx-auto">
          <CardHeader>
            <CardTitle>Factura Generada</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="aspect-[1/1.4] w-full border rounded-md overflow-hidden h-[600px]">
              <iframe src={pdfUrl} className="w-full h-full" title="Factura PDF" />
            </div>
            <div className="flex justify-end space-x-4">
              <a href={pdfUrl} download="factura.pdf">
                <Button>Descargar PDF</Button>
              </a>
              <Button 
                onClick={() => {
                  if (facturaGenerada) {
                    router.push(`/pago/${facturaGenerada.id}?total=${facturaGenerada.total}&fecha=${facturaGenerada.fecha}&responsable=${facturaGenerada.razonSocial}&numero=${facturaGenerada.numero}`);
                  } else {
                    router.push(habitacion ? `/pago?habitacion=${habitacion}` : '/pago');
                  }
                }}
              >
                Pagar
              </Button>
              <Button onClick={() => router.push('/facturar')}>
                Finalizar
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <Card className="max-w-4xl mx-auto">
        <CardHeader>
          <CardTitle>Seleccionar Consumos</CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="text-center py-10">Cargando consumos...</div>
          ) : (
            <div className="space-y-6">
              <div className="border rounded-md overflow-hidden">
                <table className="w-full text-sm text-left">
                  <thead className="bg-slate-100">
                    <tr>
                      <th className="p-3 w-10">
                        <input 
                          type="checkbox" 
                          onChange={(e) => {
                            if (e.target.checked) {
                              setSelectedConsumoIds(consumos.filter(c => !c.facturado).map(c => c.id));
                            } else {
                              setSelectedConsumoIds([]);
                            }
                          }}
                          checked={consumos.length > 0 && consumos.some(c => !c.facturado) && selectedConsumoIds.length === consumos.filter(c => !c.facturado).length}
                          disabled={!consumos.some(c => !c.facturado)}
                        />
                      </th>
                      <th className="p-3">Descripción</th>
                      <th className="p-3">Fecha</th>
                      <th className="p-3 text-right">Precio Unit.</th>
                      <th className="p-3 text-right">Cant.</th>
                      <th className="p-3 text-right">Total</th>
                      <th className="p-3 text-center">Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {consumos.map(consumo => {
                      const totalItemPrice = consumo.monto * (consumo.cantidad || 1);
                      return (
                      <tr key={consumo.id} className={`border-t ${consumo.facturado ? 'bg-slate-50 text-slate-400' : 'hover:bg-slate-50'}`}>
                        <td className="p-3">
                          <input
                            type="checkbox"
                            checked={selectedConsumoIds.includes(consumo.id)}
                            onChange={() => toggleConsumo(consumo.id)}
                            disabled={consumo.facturado}
                            className="h-4 w-4"
                          />
                        </td>
                        <td className="p-3">{consumo.descripcion}</td>
                        <td className="p-3">
                          {(() => {
                            if (!consumo.fecha) return '';
                            
                            const parts = consumo.fecha.split('-');
                            if (parts.length === 3) {
                              return `${parts[2]}/${parts[1]}/${parts[0]}`;
                            }
                            return new Date(consumo.fecha).toLocaleDateString();
                          })()}
                        </td>
                        <td className="p-3 text-right">${consumo.monto.toFixed(2)}</td>
                        <td className="p-3 text-right">{consumo.cantidad || 1}</td>
                        <td className="p-3 text-right font-medium">${totalItemPrice.toFixed(2)}</td>
                        <td className="p-3 text-center">
                          {consumo.facturado ? (
                            <span className="px-2 py-1 rounded-full bg-green-100 text-green-800 text-xs">Facturado</span>
                          ) : (
                            <span className="px-2 py-1 rounded-full bg-yellow-100 text-yellow-800 text-xs">Pendiente</span>
                          )}
                        </td>
                      </tr>
                      );
                    })}
                    {consumos.length === 0 && (
                      <tr>
                        <td colSpan={7} className="p-6 text-center text-slate-500">
                          No hay consumos registrados para esta estadía.
                        </td>
                      </tr>
                    )}
                  </tbody>
                  {consumos.length > 0 && (
                    <tfoot className="bg-slate-100 font-bold">
                      <tr>
                        <td colSpan={5} className="p-3 text-right">Total a Facturar:</td>
                        <td className="p-3 text-right">${totalSelected.toFixed(2)}</td>
                        <td></td>
                      </tr>
                    </tfoot>
                  )}
                </table>
              </div>

              <div className="flex justify-end space-x-4">
                <Button variant="outline" onClick={() => router.back()}>
                  Atrás
                </Button>
                <Button onClick={handleGenerateFactura} disabled={selectedConsumoIds.length === 0}>
                  Siguiente
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <AlertModal
        isOpen={showErrorModal}
        onClose={() => setShowErrorModal(false)}
        title="Error"
        message={error}
        variant="danger"
      />
    </div>
  );
}

export default function ConsumosPage() {
  return (
    <Suspense fallback={<div>Cargando...</div>}>
      <ConsumosContent />
    </Suspense>
  );
}
