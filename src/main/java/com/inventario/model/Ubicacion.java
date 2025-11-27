package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Modelo para gestionar ubicaciones específicas dentro de almacenes
 * Permite trazabilidad y organización física del inventario
 */
@Entity
@Table(name = "ubicaciones", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"almacen_id", "codigo"}))
public class Ubicacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String codigo; // Ej: A-01-01 (Pasillo-Rack-Nivel)
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 300)
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;
    
    @Column(length = 50)
    private String zona; // Ej: REFRIGERADO, SECO, PELIGROSO
    
    @Column(length = 20)
    private String pasillo;
    
    @Column(length = 20)
    private String rack;
    
    @Column(length = 20)
    private String nivel;
    
    @Column(length = 20)
    private String posicion;
    
    @Column(name = "capacidad_maxima")
    private Double capacidadMaxima; // En unidades o peso
    
    @Column(name = "capacidad_utilizada")
    private Double capacidadUtilizada = 0.0;
    
    @Column(name = "tipo_almacenamiento", length = 50)
    private String tipoAlmacenamiento; // ESTANTERIA, PALLET, FRIO, etc.
    
    @Column(name = "temperatura_min")
    private Double temperaturaMin;
    
    @Column(name = "temperatura_max")
    private Double temperaturaMax;
    
    @Column(name = "humedad_max")
    private Double humedadMax;
    
    @Column(name = "permite_mixto")
    private Boolean permiteMixto = true; // Permite diferentes productos
    
    @Column(name = "coordenada_x")
    private Integer coordenadaX; // Para mapeo visual
    
    @Column(name = "coordenada_y")
    private Integer coordenadaY;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "bloqueada")
    private Boolean bloqueada = false; // Bloqueo temporal
    
    @Column(name = "motivo_bloqueo", length = 500)
    private String motivoBloqueo;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructores
    public Ubicacion() {}
    
    public Ubicacion(String codigo, String nombre, Almacen almacen) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.almacen = almacen;
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }
    
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    
    public String getPasillo() { return pasillo; }
    public void setPasillo(String pasillo) { this.pasillo = pasillo; }
    
    public String getRack() { return rack; }
    public void setRack(String rack) { this.rack = rack; }
    
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    
    public Double getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Double capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    
    public Double getCapacidadUtilizada() { return capacidadUtilizada; }
    public void setCapacidadUtilizada(Double capacidadUtilizada) { this.capacidadUtilizada = capacidadUtilizada; }
    
    public String getTipoAlmacenamiento() { return tipoAlmacenamiento; }
    public void setTipoAlmacenamiento(String tipoAlmacenamiento) { this.tipoAlmacenamiento = tipoAlmacenamiento; }
    
    public Double getTemperaturaMin() { return temperaturaMin; }
    public void setTemperaturaMin(Double temperaturaMin) { this.temperaturaMin = temperaturaMin; }
    
    public Double getTemperaturaMax() { return temperaturaMax; }
    public void setTemperaturaMax(Double temperaturaMax) { this.temperaturaMax = temperaturaMax; }
    
    public Double getHumedadMax() { return humedadMax; }
    public void setHumedadMax(Double humedadMax) { this.humedadMax = humedadMax; }
    
    public Boolean getPermiteMixto() { return permiteMixto; }
    public void setPermiteMixto(Boolean permiteMixto) { this.permiteMixto = permiteMixto; }
    
    public Integer getCoordenadaX() { return coordenadaX; }
    public void setCoordenadaX(Integer coordenadaX) { this.coordenadaX = coordenadaX; }
    
    public Integer getCoordenadaY() { return coordenadaY; }
    public void setCoordenadaY(Integer coordenadaY) { this.coordenadaY = coordenadaY; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Boolean getBloqueada() { return bloqueada; }
    public void setBloqueada(Boolean bloqueada) { this.bloqueada = bloqueada; }
    
    public String getMotivoBloqueo() { return motivoBloqueo; }
    public void setMotivoBloqueo(String motivoBloqueo) { this.motivoBloqueo = motivoBloqueo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Métodos de utilidad
    public boolean tieneCapacidadDisponible() {
        if (capacidadMaxima == null) return true;
        return capacidadUtilizada < capacidadMaxima;
    }
    
    public Double getCapacidadDisponible() {
        if (capacidadMaxima == null) return null;
        return Math.max(0, capacidadMaxima - capacidadUtilizada);
    }
    
    public Double getPorcentajeOcupacion() {
        if (capacidadMaxima == null || capacidadMaxima == 0) return 0.0;
        return (capacidadUtilizada / capacidadMaxima) * 100;
    }
    
    public boolean puedeAlmacenar(Double cantidad) {
        if (bloqueada || !activo) return false;
        if (capacidadMaxima == null) return true;
        return (capacidadUtilizada + cantidad) <= capacidadMaxima;
    }
    
    public void bloquear(String motivo) {
        this.bloqueada = true;
        this.motivoBloqueo = motivo;
    }
    
    public void desbloquear() {
        this.bloqueada = false;
        this.motivoBloqueo = null;
    }
    
    public String getCodigoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (pasillo != null) sb.append(pasillo);
        if (rack != null) sb.append("-").append(rack);
        if (nivel != null) sb.append("-").append(nivel);
        if (posicion != null) sb.append("-").append(posicion);
        return sb.length() > 0 ? sb.toString() : codigo;
    }
    
    @Override
    public String toString() {
        return "Ubicacion{id=" + id + ", codigo='" + codigo + "', nombre='" + nombre + 
               "', almacen=" + (almacen != null ? almacen.getNombre() : "null") + "}";
    }
}