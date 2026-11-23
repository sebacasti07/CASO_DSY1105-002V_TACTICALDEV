package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario_repuestos")
public class InventarioRepuesto {
    @Id
    public String inventarioId;
    public String repuestoId;
    public String nombre;
    public String nombreRepuesto;
    public String tipo;
    public String tipoItem;
    public String tipoEquipo;
    public String categoria;
    public int cantidadDisponible;
    public int stockDisponible;
    public double precioUnitario;
    public double precioArriendo;
    public String bodegaUbicacion;
    @Column(length = 1000)
    public String descripcion;
    @Column(length = 2000)
    public String fotosUrls;
}
