package com.inventario.util;

import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos
 */
public class ValidationUtils {

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{7,15}$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[A-Za-z0-9]+$"
    );

    private static final Pattern CODIGO_PATTERN = Pattern.compile(
            "^[A-Z0-9-]+$"
    );

    /**
     * Valida si un email es válido
     */
    public static boolean esEmailValido(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida si un teléfono es válido
     */
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && PHONE_PATTERN.matcher(telefono).matches();
    }

    /**
     * Valida si una cadena es alfanumérica
     */
    public static boolean esAlfanumerico(String texto) {
        return texto != null && ALPHANUMERIC_PATTERN.matcher(texto).matches();
    }

    /**
     * Valida si un código es válido
     */
    public static boolean esCodigoValido(String codigo) {
        return codigo != null && CODIGO_PATTERN.matcher(codigo).matches();
    }

    /**
     * Valida si una cadena está vacía o es null
     */
    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida si una cadena no está vacía
     */
    public static boolean noEsVacio(String texto) {
        return !esVacio(texto);
    }

    /**
     * Valida longitud mínima
     */
    public static boolean longitudMinima(String texto, int minimo) {
        return texto != null && texto.length() >= minimo;
    }

    /**
     * Valida longitud máxima
     */
    public static boolean longitudMaxima(String texto, int maximo) {
        return texto != null && texto.length() <= maximo;
    }

    /**
     * Valida rango de longitud
     */
    public static boolean longitudEnRango(String texto, int minimo, int maximo) {
        return longitudMinima(texto, minimo) && longitudMaxima(texto, maximo);
    }

    /**
     * Valida si un número es positivo
     */
    public static boolean esPositivo(Number numero) {
        return numero != null && numero.doubleValue() > 0;
    }

    /**
     * Valida si un número es no negativo
     */
    public static boolean esNoNegativo(Number numero) {
        return numero != null && numero.doubleValue() >= 0;
    }

    /**
     * Valida si un número está en un rango
     */
    public static boolean enRango(Number numero, double minimo, double maximo) {
        if (numero == null) {
            return false;
        }
        double valor = numero.doubleValue();
        return valor >= minimo && valor <= maximo;
    }

    /**
     * Valida contraseña segura
     */
    public static boolean esPasswordSegura(String password) {
        if (esVacio(password) || password.length() < 8) {
            return false;
        }

        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    /**
     * Valida RUC colombiano (simplificado)
     */
    public static boolean esRUCValido(String ruc) {
        if (esVacio(ruc)) {
            return false;
        }

        // Formato: 9 dígitos - 1 dígito verificador
        String[] partes = ruc.split("-");
        if (partes.length != 2) {
            return false;
        }

        return partes[0].matches("[0-9]{9}") && partes[1].matches("[0-9]{1}");
    }

    /**
     * Sanitiza un string para SQL (básico)
     */
    public static String sanitizar(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[';\"\\\\]", "").trim();
    }

    /**
     * Valida código de barras EAN-13
     */
    public static boolean esEAN13Valido(String codigo) {
        if (codigo == null || codigo.length() != 13) {
            return false;
        }
        if (!codigo.matches("[0-9]{13}")) {
            return false;
        }

        try {
            int suma = 0;
            for (int i = 0; i < 12; i++) {
                int digito = Character.getNumericValue(codigo.charAt(i));
                suma += (i % 2 == 0) ? digito : digito * 3;
            }

            int digitoControl = (10 - (suma % 10)) % 10;
            int digitoEsperado = Character.getNumericValue(codigo.charAt(12));

            return digitoControl == digitoEsperado;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Trunca un texto a una longitud máxima
     */
    public static String truncar(String texto, int longitudMaxima) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= longitudMaxima) {
            return texto;
        }
        return texto.substring(0, longitudMaxima - 3) + "...";
    }

    /**
     * Capitaliza la primera letra
     */
    public static String capitalizar(String texto) {
        if (esVacio(texto)) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    // Prevenir instanciación
    private ValidationUtils() {
        throw new IllegalStateException("Clase de utilidad");
    }
}
