package com.inventario.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "razon_social", length = 200)
    private String razonSocial;
    
    @Column(length = 20)
    private String ruc;
    
    @Column(length = 300)
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "persona_contacto", length = 150)
    private String personaContacto;
    
    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;
    
    @Column(length = 100)
    private String pais;
    
    @Column(length = 100)
    private String ciudad;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(name = "dias_credito")
    private Integer diasCredito = 0;
    
    @Column(name = "limite_credito")
    private Double limiteCredito = 0.0;
    
    @Column(name = "saldo_pendiente")
    private Double saldoPendiente = 0.0;
    
    @Column(length = 500)
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
    public Proveedor() {}
    
    public Proveedor(String codigo, String nombre, String telefono, String email) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }
    
    // Getters y Setters completos
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getRazonSocial() {
        return razonSocial;
    }
    
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    
    public String getRuc() {
        return ruc;
    }
    
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPersonaContacto() {
        return personaContacto;
    }
    
    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }
    
    public String getTelefonoContacto() {
        return telefonoContacto;
    }
    
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
    
    public String getPais() {
        return pais;
    }
    
    public void setPais(String pais) {
        this.pais = pais;
    }
    
    public String getCiudad() {
        return ciudad;
    }
    
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
    public String getCodigoPostal() {
        return codigoPostal;
    }
    
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    
    public Integer getDiasCredito() {
        return diasCredito;
    }
    
    public void setDiasCredito(Integer diasCredito) {
        this.diasCredito = diasCredito;
    }
    
    public Double getLimiteCredito() {
        return limiteCredito;
    }
    
    public void setLimiteCredito(Double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
    
    public Double getSaldoPendiente() {
        return saldoPendiente;
    }
    
    public void setSaldoPendiente(Double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Métodos de utilidad
    public boolean tieneCreditoDisponible() {
        return limiteCredito > saldoPendiente;
    }
    
    public Double getCreditoDisponible() {
        return Math.max(0, limiteCredito - saldoPendiente);
    }
    
    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }
}