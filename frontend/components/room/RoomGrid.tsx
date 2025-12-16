import React, { useState, useEffect } from 'react';
import { habitacionService } from '../../services/habitacionService';
import { reservaService } from '../../services/reservaService';
import { HabitacionEstadoDTO, EstadoHabitacion, CategoriaHabitacion, ReservaDTO } from '../../types';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Label } from '../ui/Label';
import ReservationModal from './ReservationModal';
import CheckInModal from './CheckInModal';
import OccupancyConflictModal from './OccupancyConflictModal';
import Modal from '../ui/Modal';
import { X } from 'lucide-react';

interface RoomGridProps {
  mode: 'status' | 'reserve' | 'occupy';
}

const RoomGrid: React.FC<RoomGridProps> = ({ mode }) => {
  const [startDate, setStartDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [endDate, setEndDate] = useState<string>(
    new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [rooms, setRooms] = useState<HabitacionEstadoDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [dates, setDates] = useState<string[]>([]);

  //Estado de selección de celdas
  const [selectedCells, setSelectedCells] = useState<{ roomNumber: string; date: string }[]>([]);
  const [showReservationModal, setShowReservationModal] = useState(false);
  const [showCheckInModal, setShowCheckInModal] = useState(false);
  const [showConflictModal, setShowConflictModal] = useState(false);
  const [conflicts, setConflicts] = useState<ReservaDTO[]>([]);
  const [error, setError] = useState('');
  const lastClickedRef = React.useRef<{ roomNumber: string; date: string } | null>(null);

  const fetchStatus = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await habitacionService.getEstado(startDate, endDate);
      //Ordenar habitaciones por numero
      const sortedData = data.sort((a, b) => {
        //Ordenar numéricamente si es posible, si no, lexicograficamente
        const numA = parseInt(a.numero);
        const numB = parseInt(b.numero);
        if (!isNaN(numA) && !isNaN(numB)) {
          return numA - numB;
        }
        return a.numero.localeCompare(b.numero);
      });
      setRooms(sortedData);
      generateDateRange(startDate, endDate);
      setSelectedCells([]); //Limpiar selección al actualizar
    } catch (error) {
      console.error('Error fetching room status:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStatus();
  }, []); //Carga inicial

  useEffect(() => {
    setSelectedCells([]);
  }, [mode]);

  const generateDateRange = (start: string, end: string) => {
    const dateArray = [];
    let currentDate = new Date(start);
    const stopDate = new Date(end);
    while (currentDate <= stopDate) {
      dateArray.push(currentDate.toISOString().split('T')[0]);
      currentDate.setDate(currentDate.getDate() + 1);
    }
    setDates(dateArray);
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    const start = new Date(startDate);
    const end = new Date(endDate);

    //Obtener fecha local como string YYYY-MM-DD
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const todayStr = `${year}-${month}-${day}`;

    if (startDate < todayStr) {
      setError('La fecha de inicio debe ser mayor o igual a la fecha actual.');
      return;
    }

    if (mode === 'occupy' && startDate !== todayStr) {
      setError('Para ocupar una habitación, la fecha de inicio debe ser el día de hoy.');
      return;
    }
    
    if (end < start) {
      setError('La fecha de fin no puede ser anterior a la fecha de inicio.');
      return;
    }

    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 

    if (diffDays > 40) {
      setError('El rango de fechas no puede exceder los 40 días.');
      setRooms([]);
      setDates([]);
      setSelectedCells([]);
      return;
    }

    fetchStatus();
  };

  const isSelected = (roomNumber: string, date: string) => {
    return selectedCells.some((cell) => cell.roomNumber === roomNumber && cell.date === date);
  };

  const getCellColor = (status: EstadoHabitacion, roomNumber: string, date: string) => {
    if (isSelected(roomNumber, date)) {
      return 'bg-blue-500 hover:bg-blue-600 cursor-pointer';
    }

    switch (status) {
      case EstadoHabitacion.DISPONIBLE:
        return mode === 'status' 
          ? 'bg-green-200' 
          : 'bg-green-200 hover:bg-green-300 cursor-pointer';
      case EstadoHabitacion.RESERVADA:
        return mode === 'occupy' 
          ? 'bg-yellow-200 hover:bg-yellow-300 cursor-pointer' 
          : 'bg-yellow-200 cursor-not-allowed';
      case EstadoHabitacion.OCUPADA:
        return 'bg-red-200 cursor-not-allowed';
      case EstadoHabitacion.FUERASERVICIO:
        return 'bg-gray-300 cursor-not-allowed';
      default:
        return 'bg-white';
    }
  };

  const handleCellClick = (roomNumber: string, date: string, status: EstadoHabitacion) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    if (end < start) {
      setError('La fecha de fin no puede ser anterior a la fecha de inicio.');
      return;
    }

    if (mode === 'status') return;

    //Logica para ocupar: rango de seleccion empieza a partir de la fecha actual
    if (mode === 'occupy') {
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      const todayStr = `${year}-${month}-${day}`;

      if (date < todayStr) return; //Dias pasados no es posible

      //Determina rango [hoy, dia seleccionado]
      //Necesitamos encontrar el objeto habitacion para chequear la disponibilidad en las fechas intermedias
      const room = rooms.find(r => r.numero === roomNumber);
      if (!room) return;

      const newSelection: { roomNumber: string; date: string }[] = [];
      let curr = new Date(todayStr);
      const end = new Date(date);
      
      let possible = true;
      while (curr <= end) {
        const dStr = curr.toISOString().split('T')[0];
        
        //Chequear estado de la habitacion en esa fecha
        const statusDay = room.estadosPorDia.find(d => d.fecha === dStr);
        const s = statusDay ? statusDay.estado : EstadoHabitacion.DISPONIBLE;
        
        if (s !== EstadoHabitacion.DISPONIBLE && s !== EstadoHabitacion.RESERVADA) {
          possible = false;
          break;
        }
        
        newSelection.push({ roomNumber, date: dStr });
        curr.setDate(curr.getDate() + 1);
      }

      if (possible) {
        setSelectedCells(newSelection);
      } else {
        setError('El rango seleccionado contiene fechas no disponibles.');
      }
      return;
    }

    //Logica para reservar
    let canSelect = false;
    if (mode === 'reserve' && status === EstadoHabitacion.DISPONIBLE) {
      canSelect = true;
    }

    if (canSelect) {
      const exists = selectedCells.some((cell) => cell.roomNumber === roomNumber && cell.date === date);

      if (exists) {
        setSelectedCells((prev) => prev.filter((cell) => !(cell.roomNumber === roomNumber && cell.date === date)));
        lastClickedRef.current = null;
      } else {
        let cellsToAdd: { roomNumber: string; date: string }[] = [];

        if (lastClickedRef.current && lastClickedRef.current.roomNumber === roomNumber) {
          const start = new Date(lastClickedRef.current.date);
          const end = new Date(date);
          const min = start < end ? start : end;
          const max = start < end ? end : start;

          const room = rooms.find((r) => r.numero === roomNumber);
          if (room) {
            let curr = new Date(min);
            let rangeValid = true;
            const tempCells: { roomNumber: string; date: string }[] = [];

            while (curr <= max) {
              const dStr = curr.toISOString().split('T')[0];
              const statusDay = room.estadosPorDia.find((d) => d.fecha === dStr);
              const s = statusDay ? statusDay.estado : EstadoHabitacion.DISPONIBLE;
              if (s !== EstadoHabitacion.DISPONIBLE) {
                rangeValid = false;
                break;
              }
              tempCells.push({ roomNumber, date: dStr });
              curr.setDate(curr.getDate() + 1);
            }

            if (rangeValid) {
              cellsToAdd = tempCells;
            } else {
              cellsToAdd = [{ roomNumber, date }];
            }
          } else {
            cellsToAdd = [{ roomNumber, date }];
          }
        } else {
          cellsToAdd = [{ roomNumber, date }];
        }

        setSelectedCells((prev) => {
          const newSelection = [...prev];
          cellsToAdd.forEach((c) => {
            if (!newSelection.some((p) => p.roomNumber === c.roomNumber && p.date === c.date)) {
              newSelection.push(c);
            }
          });
          return newSelection;
        });
        lastClickedRef.current = { roomNumber, date };
      }
    }
  };

  const handleConfirm = async () => {
    if (mode === 'reserve') {
      setShowReservationModal(true);
    } else if (mode === 'occupy') {
      //Validar por cada habitacion 
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      const todayStr = `${year}-${month}-${day}`;

      //Agrupar por habitacion
      const rooms = new Set(selectedCells.map(c => c.roomNumber));
      
      if (rooms.size > 1) {
        setError('Solo se puede ocupar una habitación a la vez.');
        return;
      }

      for (const room of rooms) {
        const roomCells = selectedCells.filter(c => c.roomNumber === room);
        const earliestDate = roomCells.reduce((min, c) => c.date < min ? c.date : min, roomCells[0].date);
        const latestDate = roomCells.reduce((max, c) => c.date > max ? c.date : max, roomCells[0].date);
        
        if (earliestDate > todayStr) {
          setError(`La ocupación de la habitación ${room} debe comenzar el día de hoy.`);
          return;
        }

        //Verificar posibles conflictos
        try {
          //Calcular checkout
          const checkoutDate = new Date(latestDate);
          checkoutDate.setDate(checkoutDate.getDate() + 1);
          const checkoutStr = checkoutDate.toISOString().split('T')[0];

          const estadia = {
            checkIn: earliestDate,
            checkOut: checkoutStr,
            numeroHabitacion: room,
            idHuespedTitular: 0 //ID para probar validacion
          };
          
          const conflictsList = await reservaService.verificar(estadia);
          if (conflictsList && conflictsList.length > 0) {
            setConflicts(conflictsList);
            setShowConflictModal(true);
            return;
          }
        } catch (err) {
          console.error('Error verifying reservation conflicts:', err);
          setError('Error al verificar conflictos de reserva.');
          return;
        }
      }

      setShowCheckInModal(true);
    }
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (mode === 'occupy' && selectedCells.length > 0 && !showCheckInModal && !showConflictModal && !showReservationModal) {
        handleConfirm();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [mode, selectedCells, showCheckInModal, showConflictModal, showReservationModal]);

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">
          {mode === 'status' && 'Estado de Habitaciones'}
          {mode === 'reserve' && 'Reservar Habitación'}
          {mode === 'occupy' && 'Ocupar Habitación'}
        </h2>
        {selectedCells.length > 0 && (
          <div className="flex items-center gap-4">
            {mode === 'occupy' && (
              <span className="text-sm text-slate-500 animate-pulse">
                Presione cualquier tecla para continuar...
              </span>
            )}
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setSelectedCells([])}>
                Limpiar
              </Button>
              <Button onClick={handleConfirm}>
                Aceptar ({selectedCells.length})
              </Button>
            </div>
          </div>
        )}
      </div>

      <form onSubmit={handleSearch} className="mb-6 flex items-end gap-4">
        <div>
          <Label htmlFor="startDate">Desde fecha (*)</Label>
          <Input
            id="startDate"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </div>
        <div>
          <Label htmlFor="endDate">Hasta fecha (*)</Label>
          <Input
            id="endDate"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            onDoubleClick={() => setEndDate(startDate)}
            required
          />
        </div>
        <Button type="submit" disabled={loading}>
          {loading ? 'Buscando...' : 'Buscar'}
        </Button>
      </form>

      {error && (
        <div className="mb-4 p-4 bg-red-100 text-red-700 rounded-md border border-red-200 flex justify-between items-center">
          <span>{error}</span>
          <button onClick={() => setError('')} className="text-red-700 hover:text-red-900">
            <X size={16} />
          </button>
        </div>
      )}

      <div className="mb-4 flex gap-4 text-sm">
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 bg-green-200 border border-slate-300"></div>
          <span>Disponible</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 bg-red-200 border border-slate-300"></div>
          <span>Ocupado</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 bg-yellow-200 border border-slate-300"></div>
          <span>Reservado</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 bg-gray-300 border border-slate-300"></div>
          <span>Fuera de Servicio</span>
        </div>
      </div>

      <div className="overflow-x-auto rounded-lg border shadow" style={{ transform: 'scaleY(-1)' }}>
        <table className="min-w-full border-collapse text-sm" style={{ transform: 'scaleY(-1)' }}>
          <thead>
            <tr className="bg-gray-100">
              <th className="border p-2 text-left sticky left-0 bg-gray-100 z-10">Habitación</th>
              {dates.map((date) => (
                <th key={date} className="border p-2 min-w-[40px] text-center">
                  {date.split('-')[2]}/{date.split('-')[1]}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rooms.map((room) => (
              <tr key={room.numero}>
                <td className="border p-2 font-medium sticky left-0 bg-white z-10">
                  {room.numero} <span className="text-xs text-gray-500 block">{room.categoria}</span>
                </td>
                {dates.map((date) => {
                  const statusDay = room.estadosPorDia.find((d) => d.fecha === date);
                  const status = statusDay ? statusDay.estado : EstadoHabitacion.DISPONIBLE;
                  
                  return (
                    <td
                      key={`${room.numero}-${date}`}
                      className={`border p-2 text-center transition-colors ${getCellColor(status, room.numero, date)}`}
                      onClick={() => handleCellClick(room.numero, date, status)}
                      title={status}
                    >
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/*Crea modal de reservacion*/}
      {selectedCells.length > 0 && (
        <ReservationModal
          isOpen={showReservationModal}
          onClose={() => setShowReservationModal(false)}
          onReject={() => {
            setShowReservationModal(false);
            setSelectedCells([]);
          }}
          selectedCells={selectedCells}
          rooms={rooms}
          onSuccess={() => {
            fetchStatus();
            setShowReservationModal(false);
            setSelectedCells([]);
          }}
        />
      )}

      {/*Modal de conflicto*/}
      <OccupancyConflictModal
        isOpen={showConflictModal}
        onClose={() => setShowConflictModal(false)}
        onConfirm={() => {
          setShowConflictModal(false);
          setShowCheckInModal(true);
        }}
        conflicts={conflicts}
      />

      {/*Modal de check-in*/}
      {selectedCells.length > 0 && (
        <CheckInModal
          isOpen={showCheckInModal}
          onClose={() => setShowCheckInModal(false)}
          selectedCells={selectedCells}
          rooms={rooms}
          onSuccess={(redirect?: boolean) => {
            fetchStatus();
            setShowCheckInModal(false);
            setSelectedCells([]);
          }}
        />
      )}
    </div>
  );
};

export default RoomGrid;
