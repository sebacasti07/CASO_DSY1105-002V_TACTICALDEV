package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visitas")
public class Visita {
    @Id
    public String visitaId;
    public String minimarketId;
    public String clienteNombre;
    public String direccion;
    public String fecha;
    public String bloqueHorario;
    @Column(length = 1000)
    public String motivo;
    public String estado;
    @Column(length = 1000)
    public String observacionesTecnico;
    public String motivoCancelacion;
    public String motivoReprogramacion;
    public String ubicacionGps;
    @Column(length = 2000)
    public String fotosUrls;
}
