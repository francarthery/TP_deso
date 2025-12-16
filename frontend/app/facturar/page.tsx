'use client';

import { useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { estadiaService, huespedService, responsablePagoService, facturacionService } from '@/services/api';
import { EstadiaDTO, Huesped, PersonaJuridicaDTO, ConsumoDTO, DatosFacturaDTO } from '@/types';
import AlertModal from '@/components/ui/AlertModal';
import ThirdPartyPayerModal from '@/components/billing/ThirdPartyPayerModal';

export default function FacturarPage() {
  const router = useRouter();
  const [roomNumber, setRoomNumber] = useState('');
  const [exitTime, setExitTime] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [estadia, setEstadia] = useState<EstadiaDTO | null>(null);
  const [huespedes, setHuespedes] = useState<Huesped[]>([]);
  const [selectedPayerId, setSelectedPayerId] = useState<number | null>(null);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [showThirdPartyModal, setShowThirdPartyModal] = useState(false);

 
  const [step, setStep] = useState(1);
  const [consumos, setConsumos] = useState<ConsumoDTO[]>([]);
  const [selectedConsumoIds, setSelectedConsumoIds] = useState<number[]>([]);
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);

  const roomInputRef = useRef<HTMLInputElement>(null);
  const timeInputRef = useRef<HTMLInputElement>(null);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setEstadia(null);
    setHuespedes([]);
    setSelectedPayerId(null);

    let missing = false;
    if (!roomNumber) {
      if (roomInputRef.current) roomInputRef.current.focus();
      missing = true;
    } else if (!exitTime) {
      if (timeInputRef.current) timeInputRef.current.focus();
      missing = true;
    }

    if (missing) {
      setError('Por favor complete los campos obligatorios.');
      return;
    }

    setLoading(true);
    try {
      const estadiaData = await estadiaService.buscarEstadia(roomNumber);
      setEstadia(estadiaData);

      
      const guestIds: number[] = [];
      if (estadiaData.idHuespedTitular) guestIds.push(estadiaData.idHuespedTitular);
      if (estadiaData.idsHuespedesInvitados) guestIds.push(...estadiaData.idsHuespedesInvitados);

      if (guestIds.length > 0) {
        const guestsData = await huespedService.getByIds(guestIds);
        setHuespedes(guestsData);
      }
    } catch (err: any) {
      console.log('Error fetching estadia:', err);
      let msg = 'Error al buscar la estadía';
      
      if (err.response) {
        if (err.response.status === 404) {
           if (typeof err.response.data === 'string' && err.response.data) {
             msg = err.response.data;
           } else {
             msg = 'Habitación no encontrada o sin estadía activa.';
           }
        } else if (err.response.data) {
          if (typeof err.response.data === 'string') {
            msg = err.response.data;
          } else if (typeof err.response.data === 'object' && err.response.data !== null) {
            msg = err.response.data.message || JSON.stringify(err.response.data);
          }
        }
      } else if (err.message) {
        msg = err.message;
      }
      
      setError(msg);
      setShowErrorModal(true);
    } finally {
      setLoading(false);
    }
  };

  const handlePayerSelection = (huesped: Huesped) => {
    if (!huesped.fechaDeNacimiento) {
      setError('El huésped no tiene fecha de nacimiento registrada.');
      setShowErrorModal(true);
      return;
    }

    const birthDate = new Date(huesped.fechaDeNacimiento);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    if (age < 18) {
      setError('El responsable de pago debe ser mayor de edad.');
      setShowErrorModal(true);
      return;
    }

    setSelectedPayerId(huesped.id!);
  };

  const handleThirdPartyBilling = () => {
    setSelectedPayerId(null);
    setShowThirdPartyModal(true);
  };

  const handleThirdPartySelect = (payer: PersonaJuridicaDTO) => {
    if (payer.id !== undefined && payer.id !== null && estadia?.id) {
        router.push(`/facturar/consumos?responsableId=${payer.id}&estadiaId=${estadia.id}`);
    } else {
        setError('El responsable seleccionado no tiene un ID válido o no hay estadía seleccionada.');
        setShowErrorModal(true);
    }
  };

  const handleAccept = async () => {
    if (selectedPayerId && estadia?.id) {
        try {
            const responsableId = await responsablePagoService.darAltaPersonaFisica(selectedPayerId);
            router.push(`/facturar/consumos?responsableId=${responsableId}&estadiaId=${estadia.id}&habitacion=${estadia.numeroHabitacion}`);
        } catch (err: any) {
            setError(err.response?.data || 'Error al asignar responsable de pago');
            setShowErrorModal(true);
        }
    }
  };

  return (
    <div className="container mx-auto py-10">
      <Card className="max-w-4xl mx-auto">
        <CardHeader>
          <CardTitle>Facturar</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSearch} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="roomNumber" className={!roomNumber && error ? 'text-red-500' : ''}>
                  Número de Habitación
                </Label>
                <Input
                  id="roomNumber"
                  ref={roomInputRef}
                  value={roomNumber}
                  onChange={(e) => {
                    const val = e.target.value;
                    if (/^\d*$/.test(val)) setRoomNumber(val);
                  }}
                  className={!roomNumber && error ? 'border-red-500 focus:ring-red-500' : ''}
                  placeholder="Solo números"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="exitTime" className={!exitTime && error ? 'text-red-500' : ''}>
                  Hora de Salida
                </Label>
                <Input
                  id="exitTime"
                  type="time"
                  ref={timeInputRef}
                  value={exitTime}
                  onChange={(e) => setExitTime(e.target.value)}
                  className={!exitTime && error ? 'border-red-500 focus:ring-red-500' : ''}
                />
              </div>
            </div>
            <Button type="submit" disabled={loading}>
              {loading ? 'Buscando...' : 'Buscar'}
            </Button>
          </form>

          {estadia && (
            <div className="mt-8 space-y-6 border-t pt-6">
              <h3 className="text-lg font-medium">Seleccionar Responsable de Pago</h3>
              
              <div className="border rounded-md overflow-hidden">
                <table className="w-full text-sm text-left">
                  <thead className="bg-slate-100">
                    <tr>
                      <th className="p-3 w-10"></th>
                      <th className="p-3">Apellido y Nombre</th>
                      <th className="p-3">Documento</th>
                      <th className="p-3">Edad</th>
                    </tr>
                  </thead>
                  <tbody>
                    {huespedes.map(guest => {
                      const birthDate = guest.fechaDeNacimiento ? new Date(guest.fechaDeNacimiento) : null;
                      let age = 'N/A';
                      if (birthDate) {
                        const today = new Date();
                        let a = today.getFullYear() - birthDate.getFullYear();
                        const m = today.getMonth() - birthDate.getMonth();
                        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                          a--;
                        }
                        age = a.toString();
                      }

                      return (
                        <tr 
                          key={guest.id} 
                          className="border-t hover:bg-slate-50 cursor-pointer"
                          onClick={() => handlePayerSelection(guest)}
                        >
                          <td className="p-3">
                            <input
                              type="radio"
                              name="payer"
                              checked={selectedPayerId === guest.id}
                              onChange={() => handlePayerSelection(guest)}
                              className="h-4 w-4"
                            />
                          </td>
                          <td className="p-3">{guest.apellido}, {guest.nombres}</td>
                          <td className="p-3">{guest.tipoDocumento} {guest.numeroDocumento}</td>
                          <td className="p-3">{age}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              <div className="flex justify-between items-center pt-4">
                <Button 
                  variant="outline"
                  onClick={handleThirdPartyBilling}
                >
                  Facturar a nombre de tercero
                </Button>

                <Button 
                  onClick={handleAccept}
                  disabled={!selectedPayerId}
                >
                  Aceptar
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <ThirdPartyPayerModal
        isOpen={showThirdPartyModal}
        onClose={() => setShowThirdPartyModal(false)}
        onSelect={handleThirdPartySelect}
      />

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
