import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import AlertModal from '../ui/AlertModal';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Label } from '../ui/Label';
import { huespedService } from '../../services/api';
import { reservaService } from '../../services/reservaService';
import { Huesped, ReservaDTO, HabitacionEstadoDTO } from '../../types';

interface ReservationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onReject: () => void;
  selectedCells: { roomNumber: string; date: string }[];
  rooms: HabitacionEstadoDTO[];
  onSuccess: () => void;
}

const ReservationModal: React.FC<ReservationModalProps> = ({
  isOpen,
  onClose,
  onReject,
  selectedCells,
  rooms,
  onSuccess,
}) => {
  const [step, setStep] = useState(1);
  const [guestData, setGuestData] = useState({
    apellido: '',
    nombres: '',
    telefono: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [missingFields, setMissingFields] = useState<string[]>([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  //Reiniciar el estado cuando se abre el modal
  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setGuestData({ apellido: '', nombres: '', telefono: '' });
      setError('');
      setMissingFields([]);
      setShowSuccessModal(false);
    }
  }, [isOpen]);

  const addDays = (dateStr: string, days: number) => {
    const date = new Date(dateStr);
    date.setDate(date.getDate() + days);
    return date.toISOString().split('T')[0];
  };

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    //Ajustar la diferencia horaria para mostrar la fecha correcta
    const userTimezoneOffset = date.getTimezoneOffset() * 60000;
    const adjustedDate = new Date(date.getTime() + userTimezoneOffset);
    
    const dayName = adjustedDate.toLocaleDateString('es-ES', { weekday: 'long' });
    const formattedDate = adjustedDate.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
    return `${dayName}, ${formattedDate}`;
  };

  const groupCellsIntoReservations = (cells: { roomNumber: string; date: string }[], guest: any): ReservaDTO[] => {
    const sorted = [...cells].sort((a, b) => {
      if (a.roomNumber !== b.roomNumber) return a.roomNumber.localeCompare(b.roomNumber);
      return a.date.localeCompare(b.date);
    });
  
    const reservations: ReservaDTO[] = [];
    if (sorted.length === 0) return reservations;
  
    let currentRoom = sorted[0].roomNumber;
    let startDate = sorted[0].date;
    let endDate = sorted[0].date;
  
    for (let i = 1; i < sorted.length; i++) {
      const cell = sorted[i];
      const prevDate = new Date(endDate);
      const currDate = new Date(cell.date);
      const diffDays = (currDate.getTime() - prevDate.getTime()) / (1000 * 3600 * 24);
  
      if (cell.roomNumber === currentRoom && Math.round(diffDays) === 1) {
        endDate = cell.date;
      } else {
        reservations.push({
          fechaInicio: startDate,
          fechaFin: addDays(endDate, 1),
          numeroHabitacion: currentRoom,
          nombreHuesped: guest?.nombres,
          apellidoHuesped: guest?.apellido,
          telefonoHuesped: guest?.telefono || '',
        });
  
        currentRoom = cell.roomNumber;
        startDate = cell.date;
        endDate = cell.date;
      }
    }
  
    reservations.push({
      fechaInicio: startDate,
      fechaFin: addDays(endDate, 1),
      numeroHabitacion: currentRoom,
      nombreHuesped: guest?.nombres,
      apellidoHuesped: guest?.apellido,
      telefonoHuesped: guest?.telefono || '',
    });
  
    return reservations;
  };

  const handleAccept = () => {
    setStep(2);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    //Validacion de campos
    const missing = [];
    if (!guestData.apellido.trim()) missing.push('apellido');
    if (!guestData.nombres.trim()) missing.push('nombres');
    if (!guestData.telefono.trim()) missing.push('telefono');

    if (missing.length > 0) {
      setMissingFields(missing);
      //Pone foco en el primer campo faltante
      const firstMissing = document.getElementById(missing[0]);
      if (firstMissing) firstMissing.focus();
      return;
    }

    setLoading(true);
    setError('');

    try {
      const reservas = groupCellsIntoReservations(selectedCells, guestData);
      await reservaService.create(reservas);
      setShowSuccessModal(true);
    } catch (err: any) {
      setError(err.response?.data || 'Error al crear la reserva');
    } finally {
      setLoading(false);
    }
  };

  //Calcular las reservas para su visualizacion
  const previewReservations = groupCellsIntoReservations(selectedCells, null);

  return (
    <>
    <Modal isOpen={isOpen} onClose={onClose} title={step === 1 ? "Confirmar Reserva" : "Datos del Huésped"}>
      {step === 1 ? (
        <div className="space-y-6">
          <div className="bg-slate-50 p-4 rounded-md border border-slate-200">
              <h4 className="font-medium mb-2 text-slate-900">Detalle de la reserva:</h4>
              <div className="space-y-3 max-h-[300px] overflow-y-auto pr-2">
                {previewReservations.map((res, idx) => {
                  const room = rooms.find(r => r.numero === res.numeroHabitacion);
                  return (
                    <div key={idx} className="text-sm border-b border-slate-200 pb-2 last:border-0 last:pb-0">
                      <p className="font-semibold text-slate-800">
                        Habitación {res.numeroHabitacion} <span className="font-normal text-slate-500">({room?.categoria})</span>
                      </p>
                      <div className="ml-2 mt-1 space-y-1 text-slate-600">
                        <p>✔ Ingreso: {formatDate(res.fechaInicio)}, 12:00hs.</p>
                        <p>✔ Egreso: {formatDate(res.fechaFin)}, 10:00hs.</p>
                      </div>
                    </div>
                  );
                })}
              </div>
          </div>

          <div className="flex justify-end space-x-2 pt-4">
            <Button type="button" variant="outline" onClick={onReject}>
              Rechazar
            </Button>
            <Button onClick={handleAccept}>
              Aceptar
            </Button>
          </div>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-4">
            <p className="font-medium text-slate-900">Reserva a nombre de:</p>
            
            <div>
              <Label htmlFor="apellido" className={missingFields.includes('apellido') ? 'text-red-500' : ''}>
                Apellido (*)
              </Label>
              <Input
                id="apellido"
                value={guestData.apellido}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) {
                    setGuestData({...guestData, apellido: val});
                    if (missingFields.includes('apellido')) {
                      setMissingFields(prev => prev.filter(f => f !== 'apellido'));
                    }
                  }
                }}
                maxLength={50}
                className={missingFields.includes('apellido') ? 'border-red-500 focus:ring-red-500' : ''}
              />
            </div>

            <div>
              <Label htmlFor="nombres" className={missingFields.includes('nombres') ? 'text-red-500' : ''}>
                Nombre (*)
              </Label>
              <Input
                id="nombres"
                value={guestData.nombres}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) {
                    setGuestData({...guestData, nombres: val});
                    if (missingFields.includes('nombres')) {
                      setMissingFields(prev => prev.filter(f => f !== 'nombres'));
                    }
                  }
                }}
                maxLength={50}
                className={missingFields.includes('nombres') ? 'border-red-500 focus:ring-red-500' : ''}
              />
            </div>

            <div>
              <Label htmlFor="telefono" className={missingFields.includes('telefono') ? 'text-red-500' : ''}>
                Teléfono (*)
              </Label>
              <Input
                id="telefono"
                value={guestData.telefono}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[0-9]*$/.test(val)) {
                    setGuestData({...guestData, telefono: val});
                    if (missingFields.includes('telefono')) {
                      setMissingFields(prev => prev.filter(f => f !== 'telefono'));
                    }
                  }
                }}
                maxLength={20}
                className={missingFields.includes('telefono') ? 'border-red-500 focus:ring-red-500' : ''}
              />
            </div>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}

          <div className="flex justify-end space-x-2 pt-4">
            <Button type="button" variant="outline" onClick={() => setStep(1)}>
              Volver
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? 'Guardando...' : 'Confirmar Reserva'}
            </Button>
          </div>
        </form>
      )}
    </Modal>
    <AlertModal
      isOpen={showSuccessModal}
      onClose={() => {
        setShowSuccessModal(false);
        onSuccess();
      }}
      title="Éxito"
      message="Reserva/s confirmada/s"
      variant="primary"
    />
    </>
  );
};

export default ReservationModal;
