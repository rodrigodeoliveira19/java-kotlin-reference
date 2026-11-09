package br.com.process.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "registros")
public class Registro {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "id_cliente", nullable = false, columnDefinition = "UUID")
    private UUID idCliente;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    protected Registro() {
    }

    public Registro(UUID id, UUID idCliente, String nome, String email, String status, BigDecimal valor, LocalDate data) {
        this.id = id;
        this.idCliente = idCliente;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.valor = valor;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }
}
