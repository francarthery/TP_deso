import React from 'react';
import Modal from '../ui/Modal';
import { Button } from '../ui/Button';
import { ReservaDTO } from '../../types';

interface OccupancyConflictModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  conflicts: ReservaDTO[];
}

const OccupancyConflictModal: React.FC<OccupancyConflictModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  conflicts,
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Conflicto de Reservas">
      <div className="space-y-4">
        <p className="text-slate-700">
          La habitación seleccionada tiene las siguientes reservas en el período indicado:
        </p>
        <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3 max-h-60 overflow-y-auto">
          {conflicts.map((res, idx) => (
            <div key={idx} className="mb-2 last:mb-0 border-b border-yellow-100 last:border-0 pb-2 last:pb-0">
              <p className="font-semibold text-yellow-900">
                {res.nombreHuesped} {res.apellidoHuesped}
              </p>
              <p className="text-sm text-yellow-800">
                Desde: {res.fechaInicio} - Hasta: {res.fechaFin}
              </p>
            </div>
          ))}
        </div>
        <p className="text-sm text-slate-500">
          ¿Desea continuar con la ocupación de todas formas?
        </p>
        <div className="flex justify-end space-x-2 pt-2">
          <Button variant="outline" onClick={onClose}>
            Volver
          </Button>
          <Button onClick={onConfirm} className="bg-red-600 hover:bg-red-700 text-white">
            Ocupar Igual
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default OccupancyConflictModal;
