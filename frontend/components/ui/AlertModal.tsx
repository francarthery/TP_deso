import React from 'react';
import Modal from './Modal';
import { Button } from './Button';

interface AlertModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message: string;
  buttonText?: string;
  variant?: 'danger' | 'primary' | 'default';
}

const AlertModal: React.FC<AlertModalProps> = ({
  isOpen,
  onClose,
  title,
  message,
  buttonText = 'Aceptar',
  variant = 'primary',
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      <div className="space-y-4">
        <p className="text-slate-600">{message}</p>
        <div className="flex justify-end">
          <Button 
            onClick={onClose}
            className={variant === 'danger' ? 'bg-red-600 hover:bg-red-700' : ''}
          >
            {buttonText}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default AlertModal;
