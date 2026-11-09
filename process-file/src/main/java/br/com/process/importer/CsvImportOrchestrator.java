package br.com.process.importer;

import br.com.process.config.S3Properties;
import br.com.process.csv.CsvRegistroReader;
import br.com.process.entity.Registro;
import br.com.process.lease.ArquivoProcessamentoService;
import br.com.process.s3.S3StorageService;
import br.com.process.service.RegistroBatchService;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Singleton
public class CsvImportOrchestrator {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImportOrchestrator.class);

    private final S3StorageService storageService;
    private final ArquivoProcessamentoService leaseService;
    private final CsvRegistroReader csvRegistroReader;
    private final RegistroBatchService registroBatchService;
    private final int batchSize;

    public CsvImportOrchestrator(S3StorageService storageService,
                                ArquivoProcessamentoService leaseService,
                                CsvRegistroReader csvRegistroReader,
                                RegistroBatchService registroBatchService,
                                S3Properties s3Properties) {
        this.storageService = storageService;
        this.leaseService = leaseService;
        this.csvRegistroReader = csvRegistroReader;
        this.registroBatchService = registroBatchService;
        this.batchSize = s3Properties.getBatchSize();
    }

    public void processFile(String sourceKey) {
        String processingKey = null;
        String leaseOwner = "instance-" + UUID.randomUUID();

        try {
            if (sourceKey == null || !sourceKey.toLowerCase(Locale.ROOT).endsWith(".csv")) {
                LOG.info("Arquivo ignorado por não ser CSV: {}", sourceKey);
                return;
            }

            if (!leaseService.reservar(sourceKey, leaseOwner)) {
                LOG.info("Arquivo {} já está em processamento por outra instância ou lease ativa.", sourceKey);
                return;
            }

            processingKey = storageService.moveToProcessing(sourceKey);
            List<Registro> registros = readAndPersistCsv(processingKey);

            LOG.info("Arquivo {} validado com sucesso e {} registros persistidos. Movendo para processado.", processingKey, registros.size());
            storageService.moveToProcessed(processingKey);
            leaseService.marcarProcessado(sourceKey, leaseOwner);
        } catch (Exception e) {
            LOG.error("Falha no processamento do arquivo {}: {}", sourceKey, e.getMessage(), e);
            leaseService.marcarErro(sourceKey, leaseOwner, e.getMessage());
            if (processingKey != null) {
                try {
                    storageService.moveToError(processingKey);
                } catch (Exception moveException) {
                    LOG.error("Não foi possível mover {} para erro/ após falha de processamento.", processingKey, moveException);
                }
            }
        }
    }

    public void recuperarArquivosExpirados() {
        leaseService.recuperarArquivosExpirados();
    }

    private List<Registro> readAndPersistCsv(String key) throws IOException {
        List<Registro> registros = csvRegistroReader.read(storageService.readObject(key));
        if (registros.isEmpty()) {
            LOG.info("Arquivo CSV sem registros para persistir: {}", key);
            return registros;
        }
        registroBatchService.saveBatch(registros, batchSize);
        return registros;
    }
}
