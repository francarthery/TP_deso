export enum TipoDocumento {
  DNI = 'DNI',
  LE = 'LE',
  LC = 'LC',
  PASAPORTE = 'PASAPORTE',
  OTRO = 'OTRO',
}

export enum IVA {
  CONSUMIDOR_FINAL = 'CONSUMIDOR_FINAL',
  RESPONSABLE_INSCRIPTO = 'RESPONSABLE_INSCRIPTO',
  MONOTRIBUTISTA = 'MONOTRIBUTISTA',
  EXENTO = 'EXENTO',
}

export interface Direccion {
  calle: string;
  numero: string;
  departamento?: string;
  piso?: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
}

export interface Huesped {
  id?: number;
  apellido: string;
  nombres: string;
  tipoDocumento: TipoDocumento;
  numeroDocumento: string;
  cuit?: string;
  posicionFrenteAlIVA?: IVA;
  fechaDeNacimiento?: string;
  telefono?: string;
  email?: string;
  ocupacion?: string;
  nacionalidad?: string;
  direccion: Direccion;
}

export interface PersonaJuridicaDTO {
  id?: number;
  razonSocial: string;
  cuit: string;
  telefono: string;
  direccion: Direccion;
}

export interface ConsumoDTO {
  id: number;
  descripcion: string;
  monto: number;
  cantidad: number;
  fecha: string;
  idEstadia: number;
  facturado: boolean;
}

export interface DatosFacturaDTO {
  fecha: string;
  idEstadia: number;
  idConsumos: number[];
  idResponsablePago: number;
}

export interface BusquedaResponsablePagoDTO {
  razonSocial?: string;
  cuit?: string;
}

export interface UsuarioDTO {
  username: string;
  password?: string;
}

export enum EstadoHabitacion {
  DISPONIBLE = 'DISPONIBLE',
  RESERVADA = 'RESERVADA',
  OCUPADA = 'OCUPADA',
  FUERASERVICIO = 'FUERASERVICIO',
}

export enum CategoriaHabitacion {
  INDIVIDUAL_ESTANDAR = 'INDIVIDUAL_ESTANDAR',
  DOBLE_ESTANDAR = 'DOBLE_ESTANDAR',
  DOBLE_SUPERIOR = 'DOBLE_SUPERIOR',
  SUPERIOR_FAMILY_PLAN = 'SUPERIOR_FAMILY_PLAN',
  SUITE_DOBLE = 'SUITE_DOBLE',
}

export enum EstadoReserva {
  PENDIENTE = 'PENDIENTE',
  CONFIRMADA = 'CONFIRMADA',
  CANCELADA = 'CANCELADA',
}

export interface EstadoDiaDTO {
  fecha: string; 
  estado: EstadoHabitacion;
  idReferencia?: number;
}

export interface HabitacionEstadoDTO {
  numero: string;
  categoria: CategoriaHabitacion;
  estadosPorDia: EstadoDiaDTO[];
}

export interface ReservaDTO {
  id?: number;
  fechaInicio: string;
  fechaFin: string;
  estado?: EstadoReserva;
  fechaCreacion?: string;
  numeroHabitacion: string;
  categoriaHabitacion?: string;
  idHuesped?: number;
  nombreHuesped?: string;
  apellidoHuesped?: string;
  telefonoHuesped?: string;
}

export interface EstadiaDTO {
  id?: number;
  checkIn: string;
  checkOut: string;
  numeroHabitacion: string;
  idHuespedTitular?: number;
  idsHuespedesInvitados?: number[];
  idReserva?: number;
  nombreHuesped?: string;
  apellidoHuesped?: string;
  consumos?: any[]; 
}

export interface FacturaDTO {
  id: number;
  numero: string;
  fecha: string;
  total: number;
  razonSocial: string;
  tipo?: string;
}

export enum MetodoPago {
  MONEDA = 'MONEDA',
  TARJETA_CREDITO = 'TARJETA_CREDITO',
  TARJETA_DEBITO = 'TARJETA_DEBITO',
  CHEQUE = 'CHEQUE'
}

export enum TipoMoneda {
  USD = 'USD',
  EUR = 'EUR',
  UYU = 'UYU',
  BRL = 'BRL',
  ARS = 'ARS'
}

export interface TipoPagoDTO {
  metodoPago: MetodoPago;
  importe: number;
  tipoMoneda?: TipoMoneda;
  cotizacion?: number;
  numeroTarjeta?: string;
  titular?: string;
  fechaVencimiento?: string;
  codigoSeguridad?: string;
  numeroCheque?: string;
  bancoEmisor?: string;
  plaza?: string;
  fechaCobro?: string;
}

export interface ResponsablePagoDTO {
  id: number;
  razonSocial: string;
  cuit: string;
}

export interface BusquedaFacturaResponsableDTO {
  cuit?: string;
  tipoDocumento?: TipoDocumento;
  numeroDocumento?: string;
}

export interface NotaCredito {
  id: number;
  fecha: string;
  total: number;
  numero: string;
}
