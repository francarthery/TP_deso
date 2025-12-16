'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import RoomGrid from '../../components/room/RoomGrid';
import Navbar from '../../components/Navbar';
import { Button } from '../../components/ui/Button';
import Modal from '../../components/ui/Modal';

export default function HabitacionesPage() {
  const router = useRouter();
  const [mode, setMode] = useState<'status' | 'reserve' | 'occupy'>('status');
  const [showReservationOptionModal, setShowReservationOptionModal] = useState(false);

  return (
    <main className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto py-8">
        <div className="mb-6 flex gap-4 justify-center">
          <Button 
            variant={mode === 'status' ? 'default' : 'outline'}
            onClick={() => setMode('status')}
          >
            Estado Habitaciones
          </Button>
          <Button 
            variant={mode === 'reserve' ? 'default' : 'outline'}
            onClick={() => setShowReservationOptionModal(true)}
          >
            Reservar Habitación
          </Button>
          <Button 
            variant={mode === 'occupy' ? 'default' : 'outline'}
            onClick={() => setMode('occupy')}
          >
            Ocupar Habitación
          </Button>
        </div>
        <RoomGrid mode={mode} />
      </div>

      <Modal
        isOpen={showReservationOptionModal}
        onClose={() => setShowReservationOptionModal(false)}
        title="¿Qué desea hacer?"
      >
        <div className="flex flex-col gap-4 p-4">
          <Button onClick={() => {
            setMode('reserve');
            setShowReservationOptionModal(false);
          }}>
            Crear reserva
          </Button>
          <Button variant="outline" onClick={() => {
            router.push('/reservas/cancelar');
            setShowReservationOptionModal(false);
          }}>
            Cancelar reserva
          </Button>
        </div>
      </Modal>
    </main>
  );
}
