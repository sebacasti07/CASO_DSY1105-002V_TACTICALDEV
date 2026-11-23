package com.tacticaldev.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "asistencias_remotas")
public class AsistenciaRemota {
    @Id
    public String asistenciaId;
    public String minimarketId;
    public String clienteNombre;
    public String fecha;
    public String bloqueHorario;
    @Column(length = 1000)
    public String motivo;
    public String estado;
    public String enlaceReunion;
    public String motivoCancelacion;
    public String motivoReprogramacion;
    @Column(length = 2000)
    public String fotosUrls;
}
