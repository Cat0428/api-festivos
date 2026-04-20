package com.festivos.dominio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "tipo")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "tipo", length = 100, nullable = false)
    private String nombreTipo;

    public Tipo() {
    }

    public Tipo(String id, String nombreTipo) {
        this.id = id;
        this.nombreTipo = nombreTipo;
    }

    public String getId() {
        return id;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

}
