package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {
    @Id
    public String ordenId;
    public String proveedor;
    public String repuestoId;
    public String nombreRepuesto;
    public int cantidad;
    public double precioCosto;
    public double montoTotal;
    public String fecha;
    public String estado;
    @Column(length = 1000)
    public String notas;
}
