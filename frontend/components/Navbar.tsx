'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Button } from './ui/Button';
import ConfirmationModal from './ui/ConfirmationModal';

export default function Navbar() {
  const [showLogoutModal, setShowLogoutModal] = useState(false);

  return (
    <nav className="border-b bg-white text-slate-900">
      <div className="flex h-16 items-center px-4 container mx-auto">
        <div className="mr-4 hidden md:flex">
          <Link href="/huespedes" className="mr-6 flex items-center space-x-2">
            <span className="hidden font-bold sm:inline-block text-slate-900">
              Hotel Premier
            </span>
          </Link>
          <nav className="flex items-center space-x-6 text-sm font-medium">
            <Link
              href="/huespedes"
              className="transition-colors hover:text-slate-900 text-slate-600"
            >
              Huéspedes
            </Link>
            <Link
              href="/habitaciones"
              className="transition-colors hover:text-slate-900 text-slate-600"
            >
              Habitaciones
            </Link>
            <Link
              href="/facturar"
              className="transition-colors hover:text-slate-900 text-slate-600"
            >
              Facturar
            </Link>
            <Link
              href="/pago"
              className="transition-colors hover:text-slate-900 text-slate-600"
            >
              Pago
            </Link>
            <Link
              href="/responsables"
              className="transition-colors hover:text-slate-900 text-slate-600"
            >
              Responsables
            </Link>
          </nav>
        </div>
        <div className="flex flex-1 items-center justify-between space-x-2 md:justify-end">
          <div className="w-full flex-1 md:w-auto md:flex-none">
          </div>
          <nav className="flex items-center">
            <Button variant="ghost" size="sm" onClick={() => setShowLogoutModal(true)}>
              Cerrar Sesión
            </Button>
          </nav>
        </div>
      </div>

      <ConfirmationModal
        isOpen={showLogoutModal}
        onClose={() => setShowLogoutModal(false)}
        onConfirm={() => window.location.href = '/login'}
        title="Cerrar Sesión"
        message="¿Está seguro de que desea cerrar sesión?"
        confirmText="Cerrar Sesión"
        variant="primary"
      />
    </nav>
  );
}
