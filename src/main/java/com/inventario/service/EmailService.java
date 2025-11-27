package com.inventario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de correos electrónicos
 */
@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@inventario.com}")
    private String fromEmail;
    
    @Value("${inventario.notifications.email-enabled:false}")
    private boolean emailEnabled;
    
    /**
     * Envía un email simple
     */
    public void enviarEmail(String to, String subject, String text) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email deshabilitado. Mensaje no enviado a: " + to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("Email enviado exitosamente a: " + to);
            
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Envía un email HTML
     */
    public void enviarEmailHtml(String to, String subject, String htmlContent) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email deshabilitado. Mensaje HTML no enviado a: " + to);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Email HTML enviado exitosamente a: " + to);
            
        } catch (MessagingException e) {
            System.err.println("Error al enviar email HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Notifica stock bajo
     */
    public void notificarStockBajo(String to, String productoNombre, int stockActual, int stockMinimo) {
        String subject = "⚠️ Alerta de Stock Bajo - " + productoNombre;
        
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <div style="background: #fef3c7; border-left: 4px solid #f59e0b; padding: 20px; border-radius: 8px;">
                    <h2 style="color: #92400e; margin: 0 0 15px 0;">⚠️ Alerta de Stock Bajo</h2>
                    <p style="color: #78350f; font-size: 16px; margin: 10px 0;">
                        El producto <strong>%s</strong> tiene stock bajo:
                    </p>
                    <ul style="color: #78350f; font-size: 14px;">
                        <li>Stock actual: <strong>%d unidades</strong></li>
                        <li>Stock mínimo: <strong>%d unidades</strong></li>
                    </ul>
                    <p style="color: #78350f; font-size: 14px;">
                        Por favor, considere reabastecer este producto.
                    </p>
                </div>
                <p style="color: #64748b; font-size: 12px; margin-top: 20px;">
                    Este es un mensaje automático del Sistema de Inventario InventaPro
                </p>
            </body>
            </html>
            """, productoNombre, stockActual, stockMinimo);
        
        enviarEmailHtml(to, subject, htmlContent);
    }
    
    /**
     * Notifica producto vencido o próximo a vencer
     */
    public void notificarProductoVencimiento(String to, String productoNombre, String fechaVencimiento, int diasRestantes) {
        String subject = diasRestantes <= 0 ? 
            "🚨 Producto Vencido - " + productoNombre : 
            "⏰ Producto Próximo a Vencer - " + productoNombre;
        
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <div style="background: %s; border-left: 4px solid %s; padding: 20px; border-radius: 8px;">
                    <h2 style="color: %s; margin: 0 0 15px 0;">%s</h2>
                    <p style="color: %s; font-size: 16px; margin: 10px 0;">
                        El producto <strong>%s</strong>:
                    </p>
                    <ul style="color: %s; font-size: 14px;">
                        <li>Fecha de vencimiento: <strong>%s</strong></li>
                        <li>Estado: <strong>%s</strong></li>
                    </ul>
                    <p style="color: %s; font-size: 14px;">
                        %s
                    </p>
                </div>
                <p style="color: #64748b; font-size: 12px; margin-top: 20px;">
                    Este es un mensaje automático del Sistema de Inventario InventaPro
                </p>
            </body>
            </html>
            """, 
            diasRestantes <= 0 ? "#fee2e2" : "#fef3c7",
            diasRestantes <= 0 ? "#ef4444" : "#f59e0b",
            diasRestantes <= 0 ? "#991b1b" : "#92400e",
            diasRestantes <= 0 ? "🚨 Producto Vencido" : "⏰ Producto Próximo a Vencer",
            diasRestantes <= 0 ? "#7f1d1d" : "#78350f",
            productoNombre,
            diasRestantes <= 0 ? "#7f1d1d" : "#78350f",
            fechaVencimiento,
            diasRestantes <= 0 ? "VENCIDO" : "Vence en " + diasRestantes + " días",
            diasRestantes <= 0 ? "#7f1d1d" : "#78350f",
            diasRestantes <= 0 ? "Debe retirarse del inventario inmediatamente." : "Planifique su rotación o descuento."
        );
        
        enviarEmailHtml(to, subject, htmlContent);
    }
    
    /**
     * Verifica si el servicio de email está disponible
     */
    public boolean isEmailEnabled() {
        return emailEnabled && mailSender != null;
    }
}