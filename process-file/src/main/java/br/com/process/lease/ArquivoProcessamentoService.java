package br.com.process.lease;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Singleton
public class ArquivoProcessamentoService {

    private static final Duration LEASE_DURATION = Duration.ofMinutes(10);

    private final ArquivoProcessamentoRepository repository;

    public ArquivoProcessamentoService(ArquivoProcessamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean reservar(String chaveS3, String leasedBy) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(LEASE_DURATION);

        int updated = repository.reserve(chaveS3, leasedBy, expiresAt, now);
        if (updated > 0) {
            return true;
        }

        //Possivel Bug: Arquivos com o status de PROCESSADO na base e com o mesmo nome no bucket. O arquivo não sera processado.
        Optional<ArquivoProcessamento> existing = repository.findByChaveS3(chaveS3);
        if (existing.isPresent()) {
            ArquivoProcessamento arquivo = existing.get();
            if (arquivo.getStatus() == ArquivoProcessamento.Status.EM_PROCESSAMENTO
                    && arquivo.getLeaseExpiresAt() != null
                    && arquivo.getLeaseExpiresAt().isBefore(now)) {
                arquivo.setLeasedBy(leasedBy);
                arquivo.setLeaseExpiresAt(expiresAt);
                arquivo.setStatus(ArquivoProcessamento.Status.EM_PROCESSAMENTO);
                repository.save(arquivo);
                return true;
            }
            return false;
        }

        ArquivoProcessamento novo = new ArquivoProcessamento(chaveS3, ArquivoProcessamento.Status.EM_PROCESSAMENTO, leasedBy, expiresAt);
        repository.save(novo);
        return true;
    }

    @Transactional
    public void marcarProcessado(String chaveS3, String leasedBy) {
        repository.findByChaveS3(chaveS3).ifPresent(arquivo -> {
            if (arquivo.getLeasedBy() != null && arquivo.getLeasedBy().equals(leasedBy)) {
                arquivo.setStatus(ArquivoProcessamento.Status.PROCESSADO);
                arquivo.setLeaseExpiresAt(null);
                arquivo.setUltimoErro(null);
                repository.save(arquivo);
            }
        });
    }

    @Transactional
    public void marcarErro(String chaveS3, String leasedBy, String erro) {
        repository.findByChaveS3(chaveS3).ifPresent(arquivo -> {
            if (arquivo.getLeasedBy() != null && arquivo.getLeasedBy().equals(leasedBy)) {
                arquivo.setStatus(ArquivoProcessamento.Status.ERRO);
                arquivo.setLeaseExpiresAt(null);
                arquivo.setUltimoErro(erro);
                repository.save(arquivo);
            }
        });
    }

    @Transactional
    public void recuperarArquivosExpirados() {
        repository.findRecoverable(ArquivoProcessamento.Status.PENDENTE, Instant.now()).forEach(arquivo -> {
            arquivo.setStatus(ArquivoProcessamento.Status.EM_PROCESSAMENTO);
            arquivo.setLeaseExpiresAt(Instant.now().plus(LEASE_DURATION));
            repository.save(arquivo);
        });
    }
}
