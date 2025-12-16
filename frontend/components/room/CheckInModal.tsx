import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Modal from '../ui/Modal';
import AlertModal from '../ui/AlertModal';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Label } from '../ui/Label';
import { huespedService } from '../../services/api';
import { habitacionService } from '../../services/habitacionService';
import { Huesped, EstadiaDTO, HabitacionEstadoDTO } from '../../types';
import { Search, Plus, Trash2 } from 'lucide-react';

interface CheckInModalProps {
  isOpen: boolean;
  onClose: () => void;
  selectedCells: { roomNumber: string; date: string }[];
  rooms: HabitacionEstadoDTO[];
  onSuccess: (redirect?: boolean) => void;
}

const CheckInModal: React.FC<CheckInModalProps> = ({
  isOpen,
  onClose,
  selectedCells,
  rooms,
  onSuccess,
}) => {
  const router = useRouter();
  const [step, setStep] = useState(1);
  
  //Paso 1: Buscar Huéspedes y Selección
  const [searchParams, setSearchParams] = useState({
    apellido: '',
    nombres: '',
    tipoDocumento: '',
    numeroDocumento: ''
  });
  const [searchResults, setSearchResults] = useState<Huesped[]>([]);
  const [selectedGuests, setSelectedGuests] = useState<Huesped[]>([]);
  
  //Paso 2: Seleccionar Responsable
  const [responsibleId, setResponsibleId] = useState<number | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [actionAfterSuccess, setActionAfterSuccess] = useState<'another' | 'exit' | null>(null);

  //Resetear estado al abrir el modal
  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setSearchParams({ apellido: '', nombres: '', tipoDocumento: '', numeroDocumento: '' });
      setSearchResults([]);
      setSelectedGuests([]);
      setResponsibleId(null);
      setError('');
      setSuccessMessage('');
      setShowSuccessModal(false);
      setActionAfterSuccess(null);
    }
  }, [isOpen]);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
     
      const params: any = {};
      if (searchParams.apellido) params.apellido = searchParams.apellido;
      if (searchParams.nombres) params.nombres = searchParams.nombres;
      if (searchParams.tipoDocumento) params.tipoDocumento = searchParams.tipoDocumento;
      if (searchParams.numeroDocumento) params.numeroDocumento = searchParams.numeroDocumento;

      const results = await huespedService.getAll(params);
      const found = Array.isArray(results) ? results : [];
      setSearchResults(found);

      if (found.length === 0) {
        // Si no se encuentran resultados, abrir nueva pestaña para crear huésped
        window.open('/huespedes/nuevo', '_blank');
      }
    } catch (err) {
      console.error(err);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  };

  const toggleGuestSelection = (guest: Huesped) => {
    if (selectedGuests.find(g => g.id === guest.id)) {
      setSelectedGuests(prev => prev.filter(g => g.id !== guest.id));
    } else {
      setSelectedGuests(prev => [...prev, guest]);
    }
  };

  const addDays = (dateStr: string, days: number) => {
    const date = new Date(dateStr);
    date.setDate(date.getDate() + days);
    return date.toISOString().split('T')[0];
  };

  const groupCellsIntoStays = (cells: { roomNumber: string; date: string }[], responsibleId: number, allGuests: Huesped[]): EstadiaDTO[] => {
    const sorted = [...cells].sort((a, b) => {
      if (a.roomNumber !== b.roomNumber) return a.roomNumber.localeCompare(b.roomNumber);
      return a.date.localeCompare(b.date);
    });
  
    const stays: EstadiaDTO[] = [];
    if (sorted.length === 0) return stays;

    const guestIds = allGuests.map(g => g.id!).filter(id => id !== responsibleId);
  
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
        stays.push({
          checkIn: startDate,
          checkOut: addDays(endDate, 1),
          numeroHabitacion: currentRoom,
          idHuespedTitular: responsibleId,
          idsHuespedesInvitados: guestIds,
        });
  
        currentRoom = cell.roomNumber;
        startDate = cell.date;
        endDate = cell.date;
      }
    }
  
    stays.push({
      checkIn: startDate,
      checkOut: addDays(endDate, 1),
      numeroHabitacion: currentRoom,
      idHuespedTitular: responsibleId,
      idsHuespedesInvitados: guestIds,
    });
  
    return stays;
  };

  const handleFinalize = async (action: 'another' | 'exit') => {
    if (!responsibleId) {
      setError('Debe seleccionar un responsable.');
      return;
    }

    const responsibleGuest = selectedGuests.find(g => g.id === responsibleId);
    if (responsibleGuest) {
      if (!responsibleGuest.fechaDeNacimiento) {
         setError('El responsable no tiene fecha de nacimiento registrada.');
         return;
      }
      const birthDate = new Date(responsibleGuest.fechaDeNacimiento);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }

      if (age < 18) {
        setError('El responsable debe ser mayor de 18 años.');
        return;
      }
    }

    setLoading(true);
    setError('');

    try {
      const estadias = groupCellsIntoStays(selectedCells, responsibleId, selectedGuests);
      await habitacionService.ocupar(estadias);
      
      if (action === 'exit') {
        setSuccessMessage('Check-in cargado exitosamente');
        setActionAfterSuccess('exit');
        setShowSuccessModal(true);
      } else {
        setSuccessMessage('Habitación cargada con éxito');
        setActionAfterSuccess('another');
        setShowSuccessModal(true);
      }
    } catch (err: any) {
      setError(err.response?.data || 'Error al realizar el Check-in');
    } finally {
      setLoading(false);
    }
  };

  const handleSuccessClose = () => {
    setShowSuccessModal(false);
    if (actionAfterSuccess === 'exit') {
      router.push('/huespedes');
    } else {
      onSuccess(); //Limipia la selección y cierra el modal
    }
  };

  return (
    <>
    <Modal isOpen={isOpen} onClose={onClose} title={step === 1 ? "Buscar Huéspedes" : "Seleccionar Responsable"}>
      {step === 1 ? (
        <div className="space-y-6">
          <form onSubmit={handleSearch} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                placeholder="Apellido"
                value={searchParams.apellido}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) setSearchParams({...searchParams, apellido: val});
                }}
                maxLength={50}
              />
              <Input
                placeholder="Nombres"
                value={searchParams.nombres}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) setSearchParams({...searchParams, nombres: val});
                }}
                maxLength={50}
              />
              <select
                className="flex h-10 w-full rounded-md border border-slate-300 bg-transparent py-2 px-3 text-sm"
                value={searchParams.tipoDocumento}
                onChange={(e) => setSearchParams({...searchParams, tipoDocumento: e.target.value})}
              >
                <option value="">Tipo Documento</option>
                <option value="DNI">DNI</option>
                <option value="LE">LE</option>
                <option value="LC">LC</option>
                <option value="PASAPORTE">PASAPORTE</option>
                <option value="OTRO">OTRO</option>
              </select>
              <Input
                placeholder="Número Documento"
                value={searchParams.numeroDocumento}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-Z0-9]*$/.test(val)) setSearchParams({...searchParams, numeroDocumento: val});
                }}
                maxLength={10}
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              <Search className="mr-2 h-4 w-4" /> Buscar
            </Button>
          </form>

          {/* Resultados de busqueda */}
          <div className="border rounded-md p-2 max-h-40 overflow-y-auto">
            <h4 className="text-sm font-medium mb-2">Resultados de búsqueda:</h4>
            {searchResults.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-4 gap-2">
                <p className="text-sm text-slate-500">No se encontraron resultados.</p>
                <Button variant="outline" size="sm" onClick={() => window.open('/huespedes/nuevo', '_blank')}>
                  <Plus className="mr-2 h-4 w-4" /> Crear Nuevo Huésped
                </Button>
              </div>
            ) : (
              <ul className="space-y-1">
                {searchResults.map(guest => (
                  <li key={guest.id} className="flex items-center gap-2 p-2 hover:bg-slate-50 rounded">
                    <input
                      type="checkbox"
                      checked={!!selectedGuests.find(g => g.id === guest.id)}
                      onChange={() => toggleGuestSelection(guest)}
                      className="h-4 w-4"
                    />
                    <span className="text-sm">{guest.apellido}, {guest.nombres} ({guest.numeroDocumento})</span>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Resumen de Huespedes Seleccionados */}
          <div className="border-t pt-4">
            <h4 className="text-sm font-medium mb-2">Huéspedes seleccionados ({selectedGuests.length}):</h4>
            <ul className="space-y-1 max-h-20 overflow-y-auto">
              {selectedGuests.map(guest => (
                <li key={guest.id} className="text-sm text-slate-600 flex justify-between items-center">
                  <span>{guest.apellido}, {guest.nombres}</span>
                  <Button variant="ghost" size="icon" onClick={() => toggleGuestSelection(guest)} className="h-6 w-6">
                    <Trash2 className="h-3 w-3 text-red-500" />
                  </Button>
                </li>
              ))}
            </ul>
          </div>

          <div className="flex justify-end pt-4">
            <Button onClick={() => setStep(2)} disabled={selectedGuests.length === 0}>
              Aceptar
            </Button>
          </div>
        </div>
      ) : (
        <div className="space-y-6">
          <p className="text-slate-700">Seleccione el responsable de la habitación:</p>
          
          <div className="border rounded-md overflow-hidden">
            <table className="w-full text-sm text-left">
              <thead className="bg-slate-100">
                <tr>
                  <th className="p-3 w-10"></th>
                  <th className="p-3">Apellido y Nombre</th>
                  <th className="p-3">Documento</th>
                </tr>
              </thead>
              <tbody>
                {selectedGuests.map(guest => (
                  <tr key={guest.id} className="border-t hover:bg-slate-50 cursor-pointer" onClick={() => setResponsibleId(guest.id!)}>
                    <td className="p-3">
                      <input
                        type="radio"
                        name="responsible"
                        checked={responsibleId === guest.id}
                        onChange={() => setResponsibleId(guest.id!)}
                        className="h-4 w-4"
                      />
                    </td>
                    <td className="p-3">{guest.apellido}, {guest.nombres}</td>
                    <td className="p-3">{guest.tipoDocumento} {guest.numeroDocumento}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}

          <div className="flex flex-col gap-2 pt-4">
            <Button variant="outline" onClick={() => setStep(1)} className="w-full">
              SEGUIR CARGANDO
            </Button>
            <div className="flex gap-2">
              <Button className="flex-1" onClick={() => handleFinalize('another')} disabled={loading || !responsibleId}>
                CARGAR OTRA HABITACIÓN
              </Button>
              <Button className="flex-1 bg-slate-800 hover:bg-slate-900" onClick={() => handleFinalize('exit')} disabled={loading || !responsibleId}>
                SALIR
              </Button>
            </div>
          </div>
        </div>
      )}
    </Modal>
    <AlertModal
      isOpen={showSuccessModal}
      onClose={handleSuccessClose}
      title="Éxito"
      message={successMessage}
      variant="primary"
    />
    </>
  );
};

export default CheckInModal;
