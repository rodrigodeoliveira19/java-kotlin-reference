package br.com.process.importer;

import br.com.process.s3.S3StorageService;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.micronaut.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class CsvImportScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImportScheduler.class);

    private final S3StorageService storageService;
    private final CsvImportOrchestrator orchestrator;

    public CsvImportScheduler(S3StorageService storageService, CsvImportOrchestrator orchestrator) {
        this.storageService = storageService;
        this.orchestrator = orchestrator;
    }

    /*
    * fixedDelay	1 minuto	Espera após o término da execução para a próxima tentativa name	process-file-import	Identifica o lock compartilhado
      lockAtMostFor	10 minutos	Limita a validade do lock em caso de falha
      lockAtLeastFor	30 segundos	Impede a liberação antecipada do lock
      */
    @Scheduled(fixedDelay = "${s3.scan-interval:PT10M}")
    @SchedulerLock(name = "${shedlock.name:process-file-import}",
            lockAtMostFor = "${shedlock.defaults.lock-at-most-for:PT20M}",
            lockAtLeastFor = "${shedlock.defaults.lock-at-least-for:PT30S}")
    public void scanIncomingFiles() {
        orchestrator.recuperarArquivosExpirados();

//        LockAssert.assertLocked();
//        LOG.info("Lock do ShedLock adquirido!");

        List<String> files = storageService.listInboundObjects();
        LOG.info("Arquivos pendentes em entrada/: {}", files.size());

        for (String file : files) {
            orchestrator.processFile(file);
        }
    }
}
