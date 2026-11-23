package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipos_refrigeracion")
public class EquipoRefrigeracion {
    @Id
    public String equipoId;
    public String minimarketId;
    public String nombre;
    public String tipo;
    public String modelo;
    public String modalidad;
    public String estadoOperativo;
    public String fechaAdquisicion;
    @Column(length = 1000)
    public String descripcion;
    @Column(length = 2000)
    public String fotosUrls;
}
