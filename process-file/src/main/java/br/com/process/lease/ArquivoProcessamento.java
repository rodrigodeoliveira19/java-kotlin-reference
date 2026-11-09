package br.com.process.lease;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "arquivos_processamento")
public class ArquivoProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_s3", nullable = false, unique = true, length = 1024)
    private String chaveS3;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "leased_by", length = 100)
    private String leasedBy;

    @Column(name = "lease_expires_at")
    private Instant leaseExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "ultimo_erro")
    private String ultimoErro;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public ArquivoProcessamento() {
    }

    public ArquivoProcessamento(String chaveS3, Status status, String leasedBy, Instant leaseExpiresAt) {
        this.chaveS3 = chaveS3;
        this.status = status;
        this.leasedBy = leasedBy;
        this.leaseExpiresAt = leaseExpiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getChaveS3() {
        return chaveS3;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLeasedBy() {
        return leasedBy;
    }

    public void setLeasedBy(String leasedBy) {
        this.leasedBy = leasedBy;
    }

    public Instant getLeaseExpiresAt() {
        return leaseExpiresAt;
    }

    public void setLeaseExpiresAt(Instant leaseExpiresAt) {
        this.leaseExpiresAt = leaseExpiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUltimoErro() {
        return ultimoErro;
    }

    public void setUltimoErro(String ultimoErro) {
        this.ultimoErro = ultimoErro;
    }

    public enum Status {
        PENDENTE,
        EM_PROCESSAMENTO,
        PROCESSADO,
        ERRO
    }
}
