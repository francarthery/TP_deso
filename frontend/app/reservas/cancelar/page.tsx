'use client';

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { reservaService } from '@/services/reservaService';
import { ReservaDTO } from '@/types';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import Navbar from '@/components/Navbar';
import Modal from '@/components/ui/Modal';

export default function CancelarReservaPage() {
  const router = useRouter();
  const apellidoInputRef = useRef<HTMLInputElement>(null);
  const [apellido, setApellido] = useState('');
  const [nombres, setNombres] = useState('');
  const [reservas, setReservas] = useState<ReservaDTO[]>([]);
  const [selectedReservas, setSelectedReservas] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  useEffect(() => {
    const handleKeyDown = () => {
      if (showSuccessModal) {
        setShowSuccessModal(false);
        router.push('/habitaciones');
      }
    };

    if (showSuccessModal) {
      window.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [showSuccessModal, router]);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!apellido) {
      setError('El apellido es obligatorio.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const data = await reservaService.buscar({ apellido, nombres });
      if (data.length === 0) {
        setError('No existen reservas para los criterios de búsqueda');
        setReservas([]);
        apellidoInputRef.current?.focus();
      } else {
        setReservas(data);
        setError('');
      }
      setSelectedReservas([]);
    } catch (err) {
      console.error(err);
      setError('Error al buscar reservas.');
    } finally {
      setLoading(false);
    }
  };

  const handleCheckboxChange = (id: number) => {
    setSelectedReservas(prev => 
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  const handleCancelReservations = async () => {
    if (selectedReservas.length === 0) return;
    
    try {
      await reservaService.cancelar(selectedReservas);
      setShowSuccessModal(true);
      
      const data = await reservaService.buscar({ apellido, nombres });
      setReservas(data);
      setSelectedReservas([]);
    } catch (err) {
      console.error(err);
      setError('Error al cancelar las reservas.');
    }
  };

  return (
    <main className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto py-8">
        <Card className="max-w-4xl mx-auto mb-6">
          <CardHeader>
            <CardTitle>Cancelar Reserva</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSearch} className="flex gap-4 items-end">
              <div className="flex-1">
                <Label htmlFor="apellido">Apellido (*)</Label>
                <Input 
                  id="apellido" 
                  ref={apellidoInputRef}
                  value={apellido} 
                  onChange={(e) => {
                    const val = e.target.value.toUpperCase();
                    if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val) && val.length <= 50) {
                      setApellido(val);
                    }
                  }}
                  required 
                  className={error ? "border-red-500 focus-visible:ring-red-500" : ""}
                />
              </div>
              <div className="flex-1">
                <Label htmlFor="nombres">Nombres</Label>
                <Input 
                  id="nombres" 
                  value={nombres} 
                  onChange={(e) => {
                    const val = e.target.value.toUpperCase();
                    if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val) && val.length <= 50) {
                      setNombres(val);
                    }
                  }}
                />
              </div>
              <Button type="submit" disabled={loading}>
                {loading ? 'Buscando...' : 'Buscar'}
              </Button>
            </form>
            {error && <p className="text-red-500 mt-2">{error}</p>}
          </CardContent>
        </Card>

        {reservas.length > 0 && (
          <Card className="max-w-4xl mx-auto">
            <CardContent className="pt-6">
              <table className="w-full text-sm text-left">
                <thead className="bg-slate-100">
                  <tr>
                    <th className="p-3 w-10"></th>
                    <th className="p-3">Huésped</th>
                    <th className="p-3">Habitación</th>
                    <th className="p-3">Tipo</th>
                    <th className="p-3">Desde</th>
                    <th className="p-3">Hasta</th>
                    <th className="p-3">Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {reservas.map((reserva) => (
                    <tr key={reserva.id} className="border-t hover:bg-slate-50">
                      <td className="p-3">
                        <input 
                          type="checkbox" 
                          checked={selectedReservas.includes(reserva.id!)} 
                          onChange={() => handleCheckboxChange(reserva.id!)}
                          className="h-4 w-4"
                        />
                      </td>
                      <td className="p-3">{reserva.apellidoHuesped}, {reserva.nombreHuesped}</td>
                      <td className="p-3">{reserva.numeroHabitacion}</td>
                      <td className="p-3">{reserva.categoriaHabitacion || '-'}</td>
                      <td className="p-3">{reserva.fechaInicio}</td>
                      <td className="p-3">{reserva.fechaFin}</td>
                      <td className="p-3">{reserva.estado}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="flex justify-end gap-4 mt-6">
                <Button variant="outline" onClick={() => router.push('/habitaciones')}>
                  Cancelar
                </Button>
                <Button 
                  onClick={handleCancelReservations} 
                  disabled={selectedReservas.length === 0}
                  variant="destructive"
                >
                  Aceptar (Cancelar Seleccionadas)
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
        
        <Modal
            isOpen={showSuccessModal}
            onClose={() => {}} 
            title="Éxito"
        >
          <div className="p-4 text-center">
            <p className="text-lg mb-4">Las reservas seleccionadas han sido canceladas.</p>
            <p className="text-sm text-slate-500 animate-pulse">Presione cualquier tecla para continuar...</p>
          </div>
        </Modal>
      </div>
    </main>
  );
}
