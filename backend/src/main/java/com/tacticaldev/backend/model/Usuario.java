package com.tacticaldev.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    public String usuarioId;
    public String rut;
    public String primerNombre;
    public String segundoNombre;
    public String apellido;
    public String correo;
    public String telefono;
    public String contrasena;
    public String rol;
}
