package com.inventario.util;

/**
 * Constantes del sistema
 */
public class Constants {

    // Roles del sistema
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_GERENTE = "GERENTE";
    public static final String ROLE_OPERADOR = "OPERADOR";

    // Estados de movimientos
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADO = "APROBADO";
    public static final String ESTADO_COMPLETADO = "COMPLETADO";
    public static final String ESTADO_ANULADO = "ANULADO";

    // Tipos de movimiento
    public static final String TIPO_ENTRADA = "ENTRADA";
    public static final String TIPO_SALIDA = "SALIDA";
    public static final String TIPO_AJUSTE = "AJUSTE";
    public static final String TIPO_TRANSFERENCIA = "TRANSFERENCIA";

    // Alertas
    public static final int DIAS_ALERTA_VENCIMIENTO = 30;
    public static final int DIAS_LIMPIAR_ALERTAS = 30;

    // Códigos de error
    public static final String ERROR_STOCK_INSUFICIENTE = "STOCK_INSUFICIENTE";
    public static final String ERROR_PRODUCTO_NO_ENCONTRADO = "PRODUCTO_NO_ENCONTRADO";
    public static final String ERROR_CODIGO_DUPLICADO = "CODIGO_DUPLICADO";
    public static final String ERROR_VALIDACION = "VALIDACION_FALLIDA";

    // Formatos
    public static final String FORMATO_FECHA = "dd/MM/yyyy";
    public static final String FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm";
    public static final String FORMATO_MONEDA = "$#,##0.00";

    // Límites
    public static final int MAX_INTENTOS_LOGIN = 5;
    public static final int MINUTOS_BLOQUEO_USUARIO = 30;
    public static final int LONGITUD_MINIMA_PASSWORD = 8;
    public static final int LONGITUD_MINIMA_CODIGO = 3;

    // Archivos
    public static final String DIRECTORIO_UPLOADS = "uploads/";
    public static final String DIRECTORIO_REPORTES = "reportes/";
    public static final String DIRECTORIO_BACKUPS = "backups/";
    public static final long TAMANIO_MAXIMO_ARCHIVO = 10485760; // 10MB

    // Unidades de medida
    public static final String[] UNIDADES_MEDIDA = {
        "UND", "KG", "LT", "MT", "CAJA", "PAQUETE", "DOCENA"
    };

    // Tipos de documento
    public static final String[] TIPOS_DOCUMENTO = {
        "DNI", "RUC", "CE", "PASAPORTE"
    };

    // Mensajes
    public static final String MSG_OPERACION_EXITOSA = "Operación realizada exitosamente";
    public static final String MSG_ERROR_GENERICO = "Ha ocurrido un error. Intente nuevamente";
    public static final String MSG_REGISTRO_GUARDADO = "Registro guardado exitosamente";
    public static final String MSG_REGISTRO_ACTUALIZADO = "Registro actualizado exitosamente";
    public static final String MSG_REGISTRO_ELIMINADO = "Registro eliminado exitosamente";

    // Prevenir instanciación
    private Constants() {
        throw new IllegalStateException("Clase de utilidad");
    }
}
