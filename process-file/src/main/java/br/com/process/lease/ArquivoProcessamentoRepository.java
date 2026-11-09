package br.com.process.lease;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArquivoProcessamentoRepository extends JpaRepository<ArquivoProcessamento, Long> {

    Optional<ArquivoProcessamento> findByChaveS3(String chaveS3);

    @Query("SELECT a FROM ArquivoProcessamento a WHERE a.status = :status OR (a.status = 'EM_PROCESSAMENTO' AND a.leaseExpiresAt <= :now)")
    List<ArquivoProcessamento> findRecoverable(ArquivoProcessamento.Status status, Instant now);

    @Query("UPDATE ArquivoProcessamento a SET a.status = 'EM_PROCESSAMENTO', a.leasedBy = :leasedBy, a.leaseExpiresAt = :expiresAt, a.ultimoErro = null WHERE a.chaveS3 = :chaveS3 AND (a.status = 'PENDENTE' OR (a.status = 'EM_PROCESSAMENTO' AND a.leaseExpiresAt <= :now))")
    int reserve(String chaveS3, String leasedBy, Instant expiresAt, Instant now);
}
