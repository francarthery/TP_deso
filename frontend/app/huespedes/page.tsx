'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { huespedService } from '@/services/api';
import { Huesped } from '@/types';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import ConfirmationModal from '@/components/ui/ConfirmationModal';
import AlertModal from '@/components/ui/AlertModal';
import { Plus, Search, Trash2, Edit } from 'lucide-react';

export default function HuespedesPage() {
  const router = useRouter();
  const [huespedes, setHuespedes] = useState<Huesped[]>([]);
  const [loading, setLoading] = useState(true);
  const [apellido, setApellido] = useState('');
  const [nombres, setNombres] = useState('');
  const [tipoDocumento, setTipoDocumento] = useState('');
  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [selectedGuestId, setSelectedGuestId] = useState<number | null>(null);
  
  //Borrar
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [guestToDelete, setGuestToDelete] = useState<number | null>(null);

  //Error
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  //Paginación
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 20;

  const fetchHuespedes = async () => {
    setLoading(true);
    setError(null);
    setSelectedGuestId(null); //Resetear selección al buscar
    setCurrentPage(1); //Resetear página al buscar
    try {
      const params: any = {};
      if (apellido) params.apellido = apellido;
      if (nombres) params.nombres = nombres;
      if (tipoDocumento) params.tipoDocumento = tipoDocumento;
      if (numeroDocumento) params.numeroDocumento = numeroDocumento;

      const data = await huespedService.getAll(params);
      const results = Array.isArray(data) ? data : [];
      setHuespedes(results);

      //Si se usaron parámetros de búsqueda y no se encontraron resultados, redirigir a la página de nuevo huésped
      if (results.length === 0 && (apellido || nombres || tipoDocumento || numeroDocumento)) {
        router.push('/huespedes/nuevo');
      }
    } catch (error: any) {
      console.error('Error fetching huespedes:', error);
      if (error.response && error.response.data) {
          setError(typeof error.response.data === 'string' ? error.response.data : 'Error al buscar huéspedes');
      } else {
          setError('Error de conexión o del servidor');
      }
      setHuespedes([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHuespedes();
  }, []);

  const handleDeleteClick = (id: number) => {
    setGuestToDelete(id);
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = async () => {
    if (guestToDelete) {
      try {
        await huespedService.delete(guestToDelete);
        fetchHuespedes();
      } catch (error) {
        setErrorMessage('Error al eliminar el huésped. Puede que tenga estadías asociadas.');
        setShowErrorModal(true);
      } finally {
        setShowDeleteModal(false);
        setGuestToDelete(null);
      }
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchHuespedes();
  };

  const handleNext = () => {
    if (selectedGuestId) {
      router.push(`/huespedes/${selectedGuestId}`);
    } else {
      router.push('/huespedes/nuevo');
    }
  };

  const totalPages = Math.ceil(huespedes.length / itemsPerPage);
  const currentHuespedes = huespedes.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <div className="container mx-auto py-10">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold tracking-tight text-slate-900">Huéspedes</h1>
        <div className="flex gap-2">
          <Button onClick={handleNext}>
            Siguiente
          </Button>
          <Link href="/huespedes/nuevo">
            <Button variant="outline">
              <Plus className="mr-2 h-4 w-4" /> Nuevo Huésped
            </Button>
          </Link>
        </div>
      </div>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Buscar</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSearch} className="flex flex-col gap-4">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <Input 
                placeholder="Apellido" 
                value={apellido}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) setApellido(val);
                }}
                maxLength={50}
              />
              <Input 
                placeholder="Nombres" 
                value={nombres}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-ZÁÉÍÓÚÑ\s]*$/.test(val)) setNombres(val);
                }}
                maxLength={50}
              />
              <select
                className="flex h-10 w-full rounded-md border border-slate-300 bg-transparent py-2 px-3 text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-400 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={tipoDocumento}
                onChange={(e) => setTipoDocumento(e.target.value)}
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
                value={numeroDocumento}
                onChange={(e) => {
                  const val = e.target.value.toUpperCase();
                  if (/^[A-Z0-9]*$/.test(val)) setNumeroDocumento(val);
                }}
                maxLength={10}
              />
            </div>
            <div className="flex justify-end">
                <Button type="submit" variant="secondary">
                <Search className="mr-2 h-4 w-4" /> Buscar
                </Button>
            </div>
          </form>
          {error && (
            <div className="mt-4 p-4 bg-red-100 text-red-700 rounded-md">
              {error}
            </div>
          )}
        </CardContent>
      </Card>

      <Card className="mb-6">
        <div className="relative w-full overflow-auto">
          <table className="w-full caption-bottom text-sm">
            <thead className="[&_tr]:border-b">
              <tr className="border-b border-slate-200 transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900 w-[50px]"></th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Apellido</th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Nombres</th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Documento</th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Email</th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Teléfono</th>
                <th className="h-12 px-4 text-right align-middle font-bold text-slate-900">Acciones</th>
              </tr>
            </thead>
            <tbody className="[&_tr:last-child]:border-0">
              {loading ? (
                <tr>
                  <td colSpan={7} className="p-4 text-center">Cargando...</td>
                </tr>
              ) : huespedes.length === 0 ? (
                <tr>
                  <td colSpan={7} className="p-4 text-center">No se encontraron huéspedes.</td>
                </tr>
              ) : (
                currentHuespedes.map((huesped, index) => (
                  <tr 
                    key={huesped.id || index} 
                    className={`border-b border-slate-200 transition-colors hover:bg-muted/50 ${selectedGuestId === huesped.id ? 'bg-blue-50' : ''}`}
                    onClick={() => huesped.id && setSelectedGuestId(selectedGuestId === huesped.id ? null : huesped.id)}
                  >
                    <td className="p-4 align-middle">
                      <input 
                        type="radio" 
                        name="selectedGuest" 
                        checked={selectedGuestId === huesped.id}
                        readOnly
                        onClick={(e) => {
                          e.stopPropagation();
                          huesped.id && setSelectedGuestId(selectedGuestId === huesped.id ? null : huesped.id);
                        }}
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 cursor-pointer"
                      />
                    </td>
                    <td className="p-4 align-middle">{huesped.apellido}</td>
                    <td className="p-4 align-middle">{huesped.nombres}</td>
                    <td className="p-4 align-middle">{huesped.tipoDocumento} {huesped.numeroDocumento}</td>
                    <td className="p-4 align-middle">{huesped.email}</td>
                    <td className="p-4 align-middle">{huesped.telefono}</td>
                    <td className="p-4 align-middle text-right">
                      <div className="flex justify-end gap-2">
                        <Button variant="ghost" size="icon" onClick={(e) => { e.stopPropagation(); handleDeleteClick(huesped.id!); }}>
                          <Trash2 className="h-4 w-4 text-red-500" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Paginación */}
      {huespedes.length > itemsPerPage && (
        <div className="flex justify-center items-center gap-4 mb-6">
          <Button 
            variant="outline" 
            onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
          >
            Anterior
          </Button>
          <span className="text-sm text-slate-600">
            Página {currentPage} de {totalPages}
          </span>
          <Button 
            variant="outline" 
            onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
          >
            Siguiente
          </Button>
        </div>
      )}

      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={handleConfirmDelete}
        title="Eliminar Huésped"
        message="¿Está seguro de que desea eliminar este huésped? Esta acción no se puede deshacer."
        confirmText="Eliminar"
        variant="danger"
      />

      <AlertModal
        isOpen={showErrorModal}
        onClose={() => setShowErrorModal(false)}
        title="Error"
        message={errorMessage}
        variant="danger"
      />
    </div>
  );
}
