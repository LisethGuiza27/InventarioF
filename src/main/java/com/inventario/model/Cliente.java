package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 3, max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "razon_social", nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "nombre_comercial", length = 150)
    private String nombreComercial;
    
    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;
    
    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;
    
    @Column(length = 20)
    private String rfc;
    
    @Email
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 20)
    private String celular;
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Column(length = 100)
    private String ciudad;
    
    @Column(length = 100)
    private String estado;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(length = 50)
    private String pais = "México";
    
    @Column(name = "dias_credito")
    private Integer diasCredito = 0;
    
    @Column(name = "limite_credito")
    private Double limiteCredito = 0.0;

    @Column(name = "saldo_actual")
    private Double saldoActual = 0.0;

    @Column(name = "descuento_general")
    private Double descuentoGeneral = 0.0;

    @Column(name = "saldo_pendiente")
    private Double saldoPendiente = 0.0;
    
    @Column(name = "fecha_ultima_compra")
    private LocalDateTime fechaUltimaCompra;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
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
    public Cliente() {}
    
    public Cliente(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public Integer getDiasCredito() { return diasCredito; }
    public void setDiasCredito(Integer diasCredito) { this.diasCredito = diasCredito; }
    
    public Double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(Double limiteCredito) { this.limiteCredito = limiteCredito; }
    
    public Double getSaldoActual() { return saldoActual; }
    public void setSaldoActual(Double saldoActual) { this.saldoActual = saldoActual; }
    
    public Double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(Double saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    
    public Double getDescuentoGeneral() { return descuentoGeneral; }
    public void setDescuentoGeneral(Double descuentoGeneral) { this.descuentoGeneral = descuentoGeneral; }
    
    public LocalDateTime getFechaUltimaCompra() { return fechaUltimaCompra; }
    public void setFechaUltimaCompra(LocalDateTime fechaUltimaCompra) { this.fechaUltimaCompra = fechaUltimaCompra; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Métodos de utilidad
    public Double getCreditoDisponible() {
        return Math.max(0, (limiteCredito != null ? limiteCredito : 0.0) - 
                           (saldoActual != null ? saldoActual : 0.0));
    }
    
    @Override
    public String toString() {
        return "Cliente{id=" + id + ", codigo='" + codigo + "', nombre='" + nombre + "'}";
    }
}