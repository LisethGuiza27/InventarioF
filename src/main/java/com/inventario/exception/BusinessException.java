package com.inventario.exception;

/**
 * Excepción de negocio personalizada Representa errores de lógica de negocio
 * que el usuario puede entender
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object[] args;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    // Métodos estáticos para facilitar el uso
    public static BusinessException stockInsuficiente(String producto, Integer stockDisponible) {
        return new BusinessException(
                "STOCK_INSUFICIENTE",
                String.format("Stock insuficiente para el producto '%s'. Disponible: %d",
                        producto, stockDisponible)
        );
    }

    public static BusinessException productoNoEncontrado(String codigo) {
        return new BusinessException(
                "PRODUCTO_NO_ENCONTRADO",
                String.format("No se encontró el producto con código: %s", codigo)
        );
    }

    public static BusinessException codigoDuplicado(String entidad, String codigo) {
        return new BusinessException(
                "CODIGO_DUPLICADO",
                String.format("Ya existe un %s con el código: %s", entidad, codigo)
        );
    }

    public static BusinessException movimientoNoPermitido(String razon) {
        return new BusinessException(
                "MOVIMIENTO_NO_PERMITIDO",
                razon
        );
    }

    public static BusinessException validacionFallida(String mensaje) {
        return new BusinessException(
                "VALIDACION_FALLIDA",
                mensaje
        );
    }
}
