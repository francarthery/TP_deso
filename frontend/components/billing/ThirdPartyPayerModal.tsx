import React, { useState } from 'react';
import Modal from '../ui/Modal';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { Label } from '../ui/Label';
import { responsablePagoService } from '../../services/api';
import { PersonaJuridicaDTO } from '../../types';
import { Search } from 'lucide-react';

interface ThirdPartyPayerModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (payer: PersonaJuridicaDTO) => void;
}

const ThirdPartyPayerModal: React.FC<ThirdPartyPayerModalProps> = ({
  isOpen,
  onClose,
  onSelect,
}) => {
  const [razonSocial, setRazonSocial] = useState('');
  const [cuit, setCuit] = useState('');
  const [results, setResults] = useState<PersonaJuridicaDTO[]>([]);
  const [selectedPayer, setSelectedPayer] = useState<PersonaJuridicaDTO | null>(null);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setSearched(true);
    setSelectedPayer(null);
    try {
      const params: any = {};
      if (razonSocial) params.razonSocial = razonSocial;
      if (cuit) params.cuit = cuit;

      const data = await responsablePagoService.buscarPersonaJuridica(params);
      setResults(data);

      if (data.length === 0) {
        window.open('/responsable-pago/nuevo', '_blank');
      }
    } catch (error) {
      console.error(error);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCuitChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      let value = e.target.value.replace(/\D/g, '');
      if (value.length > 11) value = value.slice(0, 11);
      
      let formatted = '';
      if (value.length > 0) formatted = value.slice(0, 2);
      if (value.length > 2) formatted += '-' + value.slice(2, 10);
      if (value.length > 10) formatted += '-' + value.slice(10);
      
      setCuit(formatted);
  };

  const handleRazonSocialChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value;
      if (/^[a-zA-Z0-9ñÑÁÉÍÓÚÜáéíóúü.,&\- ]*$/.test(value)) {
          setRazonSocial(value);
      }
  };

  const handleConfirm = () => {
    if (selectedPayer) {
      onSelect(selectedPayer);
      // No cerramos el modal para evitar que se vea la pantalla anterior antes de la redirección
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Facturar a Tercero (Persona Jurídica)">
      <div className="space-y-4">
        <form onSubmit={handleSearch} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="razonSocial">Razón Social</Label>
              <Input
                id="razonSocial"
                value={razonSocial}
                onChange={handleRazonSocialChange}
                maxLength={100}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="cuit">CUIT</Label>
              <Input
                id="cuit"
                value={cuit}
                onChange={handleCuitChange}
                placeholder="XX-XXXXXXXX-X"
              />
            </div>
          </div>
          <Button type="submit" disabled={loading} className="w-full">
            {loading ? 'Buscando...' : <><Search className="w-4 h-4 mr-2" /> Buscar</>}
          </Button>
        </form>

        {searched && results.length > 0 && (
          <div className="space-y-2">
            <Label>Resultados</Label>
            <div className="border rounded-md p-2 max-h-60 overflow-y-auto space-y-2">
              {results.map((payer) => (
                <div
                  key={payer.id}
                  className={`p-3 border rounded cursor-pointer flex items-center space-x-3 ${
                    selectedPayer?.id === payer.id ? 'bg-blue-50 border-blue-500' : 'hover:bg-gray-50'
                  }`}
                  onClick={() => setSelectedPayer(payer)}
                >
                  <input
                    type="radio"
                    name="thirdPartyPayer"
                    checked={!!selectedPayer && !!payer.id && selectedPayer.id === payer.id}
                    onChange={() => setSelectedPayer(payer)}
                    className="h-4 w-4 text-blue-600"
                  />
                  <div>
                    <p className="font-medium">{payer.razonSocial}</p>
                    <p className="text-sm text-gray-500">CUIT: {payer.cuit}</p>
                  </div>
                </div>
              ))}
            </div>
            <Button
              onClick={handleConfirm}
              disabled={!selectedPayer}
              className="w-full mt-4"
            >
              Siguiente
            </Button>
          </div>
        )}
        
        {searched && results.length === 0 && !loading && (
             <div className="text-center text-gray-500 py-4">
                No se encontraron resultados. Se ha abierto una nueva pestaña para dar de alta.
             </div>
        )}
      </div>
    </Modal>
  );
};

export default ThirdPartyPayerModal;
