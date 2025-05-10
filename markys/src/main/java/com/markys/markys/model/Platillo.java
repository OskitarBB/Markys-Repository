package com.markys.markys.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
@Table(name="platillos")
public class Platillo {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    private String imagen;

    public Platillo() {
    }

    public Platillo(String nombre, String descripcion, BigDecimal precio, Estado estado, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
