import api from './api';
import { HabitacionEstadoDTO, EstadiaDTO } from '../types';

export const habitacionService = {
  getEstado: async (desde: string, hasta: string) => {
    const response = await api.get<HabitacionEstadoDTO[]>('/habitaciones/estado', {
      params: { desde, hasta },
    });
    return response.data;
  },

  ocupar: async (estadias: EstadiaDTO[]) => {
    const response = await api.post<EstadiaDTO[]>('/habitaciones/ocupar-habitacion', estadias);
    return response.data;
  },
};
