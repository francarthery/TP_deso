'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { responsablePagoService } from '@/services/api';
import { ResponsablePagoDTO } from '@/types';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { Plus, Search, Trash2 } from 'lucide-react';
import AlertModal from '@/components/ui/AlertModal';
import ConfirmationModal from '@/components/ui/ConfirmationModal';

export default function ResponsablesPage() {
  const router = useRouter();
  const [responsables, setResponsables] = useState<ResponsablePagoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [razonSocial, setRazonSocial] = useState('');
  const [cuit, setCuit] = useState('');
  const [error, setError] = useState<string | null>(null);
  

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [responsableToDelete, setResponsableToDelete] = useState<number | null>(null);


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

  const fetchResponsables = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: any = {};
      if (razonSocial) params.razonSocial = razonSocial;
      if (cuit) params.cuit = cuit;

      const data = await responsablePagoService.buscarResponsablePago(params);
      const results = Array.isArray(data) ? data : [];
      setResponsables(results);
    } catch (error: any) {
      console.error('Error fetching responsables:', error);
      if (error.response && error.response.data) {
          setError(typeof error.response.data === 'string' ? error.response.data : 'Error al buscar responsables');
      } else {
          setError('Error de conexión o del servidor');
      }
      setResponsables([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchResponsables();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (cuit) {
      const cuitRegex = /^[0-9]{2}-?[0-9]{8}-?[0-9]$/;
      if (!cuitRegex.test(cuit)) {
        setAlertModal({
          isOpen: true,
          title: 'Error de Validación',
          message: 'El CUIT debe tener 11 números y respetar el formato XX-XXXXXXXX-X.',
          variant: 'danger'
        });
        return;
      }
    }

    fetchResponsables();
  };

  const handleDeleteClick = (id: number) => {
    setResponsableToDelete(id);
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = async () => {
    if (responsableToDelete) {
      try {
        const result = await responsablePagoService.delete(responsableToDelete);
        
        if (!result.success && result.status === 409) {
          setAlertModal({
            isOpen: true,
            title: 'Error',
            message: 'No se puede eliminar el responsable de pago porque tiene facturas asociadas.',
            variant: 'danger'
          });
        } else {
          fetchResponsables();
          setAlertModal({
            isOpen: true,
            title: 'Éxito',
            message: 'Responsable de pago eliminado correctamente.',
            variant: 'default'
          });
        }
      } catch (error: any) {
        console.error('Error deleting responsable:', error);
        setAlertModal({
          isOpen: true,
          title: 'Error',
          message: 'Error al eliminar el responsable de pago.',
          variant: 'danger'
        });
      } finally {
        setShowDeleteModal(false);
        setResponsableToDelete(null);
      }
    }
  };

  return (
    <div className="container mx-auto py-10">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold tracking-tight text-slate-900">Responsables de Pago</h1>
        <div className="flex gap-2">
          <Link href="/responsable-pago/nuevo">
            <Button variant="outline">
              <Plus className="mr-2 h-4 w-4" /> Crear Persona Jurídica
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
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input 
                placeholder="Razón Social" 
                value={razonSocial}
                onChange={(e) => setRazonSocial(e.target.value.toUpperCase())}
                maxLength={50}
              />
              <Input 
                placeholder="CUIT" 
                value={cuit}
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
                  setCuit(formatted);
                }}
                maxLength={13}
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
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">Razón Social</th>
                <th className="h-12 px-4 text-left align-middle font-bold text-slate-900">CUIT</th>
                <th className="h-12 px-4 text-right align-middle font-bold text-slate-900">Acciones</th>
              </tr>
            </thead>
            <tbody className="[&_tr:last-child]:border-0">
              {loading ? (
                <tr>
                  <td colSpan={3} className="p-4 text-center text-slate-500">
                    Cargando...
                  </td>
                </tr>
              ) : responsables.length === 0 ? (
                <tr>
                  <td colSpan={3} className="p-4 text-center text-slate-500">
                    No se encontraron responsables de pago.
                  </td>
                </tr>
              ) : (
                responsables.map((responsable) => (
                  <tr key={responsable.id} className="border-b border-slate-200 transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                    <td className="p-4 align-middle">{responsable.razonSocial}</td>
                    <td className="p-4 align-middle">{responsable.cuit}</td>
                    <td className="p-4 align-middle text-right">
                      <Button 
                        variant="ghost" 
                        size="sm"
                        onClick={() => handleDeleteClick(responsable.id)}
                        title="Eliminar"
                      >
                        <Trash2 className="h-4 w-4 text-red-500" />
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      <AlertModal
        isOpen={alertModal.isOpen}
        onClose={() => setAlertModal(prev => ({ ...prev, isOpen: false }))}
        title={alertModal.title}
        message={alertModal.message}
        variant={alertModal.variant}
      />

      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={handleConfirmDelete}
        title="Eliminar Responsable"
        message="¿Está seguro que desea eliminar este responsable de pago? Esta acción no se puede deshacer."
        variant="danger"
        confirmText="Eliminar"
      />
    </div>
  );
}
