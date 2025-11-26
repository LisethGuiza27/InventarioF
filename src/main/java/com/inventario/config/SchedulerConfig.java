package com.inventario.config;

import com.inventario.service.AlertaService;
import com.inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private AlertaService alertaService;
    
    @Autowired
    private UsuarioService usuarioService;

    // Limpiar alertas antiguas - cada día a las 3:00 AM
    @Scheduled(cron = "0 0 3 * * ?")
    public void limpiarAlertasAntiguas() {
        try {
            alertaService.limpiarAlertasAntiguas();
            System.out.println("Alertas antiguas limpiadas exitosamente");
        } catch (Exception e) {
            System.err.println("Error limpiando alertas: " + e.getMessage());
        }
    }

    // Desbloquear usuarios con bloqueo expirado - cada hora
    @Scheduled(cron = "0 0 * * * ?")
    public void desbloquearUsuariosExpirados() {
        try {
            usuarioService.desbloquearUsuariosExpirados();
            System.out.println("Usuarios desbloqueados exitosamente");
        } catch (Exception e) {
            System.err.println("Error desbloqueando usuarios: " + e.getMessage());
        }
    }
}