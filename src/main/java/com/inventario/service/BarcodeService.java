package com.inventario.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Servicio para generación de códigos de barras y QR
 */
@Service
public class BarcodeService {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 100;
    private static final int QR_SIZE = 250;

    /**
     * Genera un código de barras EAN-13
     */
    public byte[] generarCodigoBarrasEAN13(String codigo) throws WriterException, IOException {
        if (codigo == null || codigo.length() != 13) {
            throw new IllegalArgumentException("El código EAN-13 debe tener exactamente 13 dígitos");
        }

        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(codigo, BarcodeFormat.EAN_13,
                DEFAULT_WIDTH, DEFAULT_HEIGHT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Genera un código QR
     */
    public byte[] generarCodigoQR(String contenido) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE,
                QR_SIZE, QR_SIZE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Guarda un código de barras en un archivo
     */
    public void guardarCodigoBarras(String codigo, String rutaArchivo)
            throws WriterException, IOException {

        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(codigo, BarcodeFormat.EAN_13,
                DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Path path = FileSystems.getDefault().getPath(rutaArchivo);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Guarda un código QR en un archivo
     */
    public void guardarCodigoQR(String contenido, String rutaArchivo)
            throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE,
                QR_SIZE, QR_SIZE);

        Path path = FileSystems.getDefault().getPath(rutaArchivo);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Genera código EAN-13 válido a partir de los primeros 12 dígitos
     */
    public String generarEAN13Valido(String codigo12Digitos) {
        if (codigo12Digitos.length() != 12) {
            throw new IllegalArgumentException("Se requieren exactamente 12 dígitos");
        }

        int suma = 0;
        for (int i = 0; i < 12; i++) {
            int digito = Character.getNumericValue(codigo12Digitos.charAt(i));
            suma += (i % 2 == 0) ? digito : digito * 3;
        }

        int digitoControl = (10 - (suma % 10)) % 10;
        return codigo12Digitos + digitoControl;
    }

    /**
     * Valida un código EAN-13
     */
    public boolean validarEAN13(String codigo) {
        if (codigo == null || codigo.length() != 13) {
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
}
