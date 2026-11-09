package br.com.process.importer;

import br.com.process.config.S3Properties;
import br.com.process.csv.CsvRegistroReader;
import br.com.process.lease.ArquivoProcessamentoService;
import br.com.process.s3.S3StorageService;
import br.com.process.service.RegistroBatchService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CsvImportOrchestratorTest {

    @Test
    void shouldProcessValidCsvAndMoveToProcessed() throws Exception {
        S3StorageService storageService = mock(S3StorageService.class);
        ArquivoProcessamentoService leaseService = mock(ArquivoProcessamentoService.class);
        RegistroBatchService batchService = mock(RegistroBatchService.class);
        CsvImportOrchestrator orchestrator = new CsvImportOrchestrator(
                storageService,
                leaseService,
                new CsvRegistroReader(),
                batchService,
                new S3Properties()
        );

        when(leaseService.reservar(eq("entrada/arquivo.csv"), anyString())).thenReturn(true);
        when(storageService.moveToProcessing("entrada/arquivo.csv")).thenReturn("processamento/arquivo.csv");
        when(storageService.readObject("processamento/arquivo.csv")).thenReturn(
                new ByteArrayInputStream(
                        "id,id_cliente,nome,email,status,valor,data\n550e8400-e29b-41d4-a716-446655440000,660e8400-e29b-41d4-a716-446655440000,Joao,joao@example.com,ATIVO,150.50,2026-10-27\n".getBytes(StandardCharsets.UTF_8)
                )
        );

        orchestrator.processFile("entrada/arquivo.csv");

        verify(batchService).saveBatch(anyList(), eq(100));
        verify(storageService).moveToProcessing("entrada/arquivo.csv");
        verify(storageService).moveToProcessed("processamento/arquivo.csv");
        verify(leaseService).marcarProcessado(eq("entrada/arquivo.csv"), anyString());
    }

    @Test
    void shouldMoveFileToErrorWhenHeaderIsInvalid() throws Exception {
        S3StorageService storageService = mock(S3StorageService.class);
        ArquivoProcessamentoService leaseService = mock(ArquivoProcessamentoService.class);
        RegistroBatchService batchService = mock(RegistroBatchService.class);
        CsvImportOrchestrator orchestrator = new CsvImportOrchestrator(
                storageService,
                leaseService,
                new CsvRegistroReader(),
                batchService,
                new S3Properties()
        );

        when(leaseService.reservar(eq("entrada/invalid.csv"), anyString())).thenReturn(true);
        when(storageService.moveToProcessing("entrada/invalid.csv")).thenReturn("processamento/invalid.csv");
        when(storageService.readObject("processamento/invalid.csv")).thenReturn(
                new ByteArrayInputStream("id,nome\n1,Joao\n".getBytes(StandardCharsets.UTF_8))
        );

        orchestrator.processFile("entrada/invalid.csv");

        verify(storageService).moveToError("processamento/invalid.csv");
        verify(storageService, never()).moveToProcessed(anyString());
        verify(leaseService).marcarErro(eq("entrada/invalid.csv"), anyString(), anyString());
    }

    @Test
    void shouldMoveFileToErrorWhenUuidIsInvalid() throws Exception {
        S3StorageService storageService = mock(S3StorageService.class);
        ArquivoProcessamentoService leaseService = mock(ArquivoProcessamentoService.class);
        RegistroBatchService batchService = mock(RegistroBatchService.class);
        CsvImportOrchestrator orchestrator = new CsvImportOrchestrator(
                storageService,
                leaseService,
                new CsvRegistroReader(),
                batchService,
                new S3Properties()
        );

        when(leaseService.reservar(eq("entrada/bad-uuid.csv"), anyString())).thenReturn(true);
        when(storageService.moveToProcessing("entrada/bad-uuid.csv")).thenReturn("processamento/bad-uuid.csv");
        when(storageService.readObject("processamento/bad-uuid.csv")).thenReturn(
                new ByteArrayInputStream(
                        "id,id_cliente,nome,email,status,valor,data\ninvalid-uuid,660e8400-e29b-41d4-a716-446655440000,Joao,joao@example.com,ATIVO,150.50,2026-10-27\n".getBytes(StandardCharsets.UTF_8)
                )
        );

        orchestrator.processFile("entrada/bad-uuid.csv");

        verify(storageService).moveToError("processamento/bad-uuid.csv");
        verify(batchService, never()).saveBatch(anyList(), anyInt());
    }

    @Test
    void shouldIgnoreNonCsvFiles() {
        S3StorageService storageService = mock(S3StorageService.class);
        ArquivoProcessamentoService leaseService = mock(ArquivoProcessamentoService.class);
        RegistroBatchService batchService = mock(RegistroBatchService.class);
        CsvImportOrchestrator orchestrator = new CsvImportOrchestrator(
                storageService,
                leaseService,
                new CsvRegistroReader(),
                batchService,
                new S3Properties()
        );

        orchestrator.processFile("entrada/arquivo.txt");

        verify(storageService, never()).moveToProcessing(anyString());
        verify(storageService, never()).moveToProcessed(anyString());
        verify(batchService, never()).saveBatch(anyList(), anyInt());
    }
}
