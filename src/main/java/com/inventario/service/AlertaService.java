package com.inventario.service;

import com.inventario.model.Alerta;
import com.inventario.model.Producto;
import com.inventario.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AlertaService {

    @Autowired
    private AlertaRepository repository;

    // Crear alerta (evita duplicados)
    public Alerta crearAlerta(Alerta.TipoAlerta tipo, Producto producto, String mensaje) {
        // Buscar si ya existe una alerta activa similar
        List<Alerta> existentes = repository.findByProductoId(producto.getId()).stream()
                .filter(a -> a.getTipo() == tipo && a.getActiva() && !a.getLeida())
                .toList();
        
        if (!existentes.isEmpty()) {
            return existentes.get(0); // Ya existe, no crear duplicado
        }
        
        Alerta alerta = new Alerta(tipo, producto, mensaje);
        return repository.save(alerta);
    }
    
    // Obtener alertas activas no leídas
    public List<Alerta> obtenerAlertasActivas(int limite) {
        return repository.findAlertasActivasNoLeidas().stream()
                .limit(limite)
                .toList();
    }
    
    // Obtener alertas críticas
    public List<Alerta> obtenerAlertasCriticas() {
        return repository.findAlertasCriticas();
    }
    
    // Marcar alerta como leída
    public void marcarLeida(Integer alertaId) throws Exception {
        Alerta alerta = repository.findById(alertaId)
                .orElseThrow(() -> new Exception("Alerta no encontrada"));
        alerta.marcarLeida();
        repository.save(alerta);
    }
    
    // Desactivar alerta
    public void desactivar(Integer alertaId) throws Exception {
        Alerta alerta = repository.findById(alertaId)
                .orElseThrow(() -> new Exception("Alerta no encontrada"));
        alerta.desactivar();
        repository.save(alerta);
    }
    
    // Contar alertas activas
    public long contarAlertasActivas() {
        return repository.countAlertasActivas();
    }
    
    // Limpiar alertas antiguas leídas (más de 30 días)
    public void limpiarAlertasAntiguas() {
        List<Alerta> alertas = repository.findAll().stream()
                .filter(a -> a.getLeida() && 
                            a.getFechaLectura() != null &&
                            a.getFechaLectura().plusDays(30).isBefore(java.time.LocalDateTime.now()))
                .toList();
        
        repository.deleteAll(alertas);
    }
}