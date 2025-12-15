package tp_hotel.tp_hotel.strategy.impl;

public class ValidadorTarjeta {
    public static boolean esNumeroLuhnValido(String numeroTarjeta) {
        String numeroLimpio = numeroTarjeta.replaceAll("\\s+|-", "");

        if (!numeroLimpio.matches("\\d+")) {
            return false;
        }

        int suma = 0;
        boolean alternar = false;

        for (int i = numeroLimpio.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numeroLimpio.substring(i, i + 1));

            if (alternar) {
                n *= 2;
                if (n > 9) {
                    n -= 9; 
                }
            }
            
            suma += n;
            alternar = !alternar;
        }

        return (suma % 10 == 0);
    }
}
