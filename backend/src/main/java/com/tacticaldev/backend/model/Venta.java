package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    public String ventaId;
    public String vendedorId;
    public String vendedorNombre;
    public String clienteNombre;
    public String minimarketId;
    public String repuestoId;
    public String productoNombre;
    public String tipoOperacion;
    public int cantidad;
    public double precioUnitario;
    public double total;
    public double montoTotal;
    public String fecha;
    public String estado;
    @Column(length = 1000)
    public String detalle;
    @Column(length = 1000)
    public String descripcion;
    @Column(length = 2000)
    public String fotosUrls;
    public String ubicacionGps;
}
