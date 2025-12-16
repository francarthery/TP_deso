import api from './api';
import { ReservaDTO, EstadiaDTO } from '../types';

export const reservaService = {
  create: async (reservas: ReservaDTO[]) => {
    const response = await api.post<ReservaDTO[]>('/reservas', reservas);
    return response.data;
  },

  verificar: async (estadia: EstadiaDTO) => {
    const response = await api.post<ReservaDTO[]>('/reservas/verificar', estadia);
    return response.data;
  },

  buscar: async (params: { apellido: string; nombres?: string; estado?: string }) => {
    const response = await api.get<ReservaDTO[]>('/reservas', { params });
    return response.data;
  },

  cancelar: async (ids: number[]) => {
    const response = await api.put('/reservas', ids);
    return response.data;
  },
};
