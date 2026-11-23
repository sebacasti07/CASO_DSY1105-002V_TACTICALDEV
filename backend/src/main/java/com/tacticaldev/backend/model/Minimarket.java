package com.tacticaldev.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "minimarkets")
public class Minimarket {
    @Id
    public String minimarketId;
    public String rutCliente;
    public String rutComercial;
    public String nombre;
    public String direccion;
    public String telefono;
}
