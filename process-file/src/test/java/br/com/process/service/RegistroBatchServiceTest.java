package br.com.process.service;

import br.com.process.entity.Registro;
import br.com.process.exception.DuplicateRegistroException;
import br.com.process.repository.RegistroRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroBatchServiceTest {

    @Test
    void shouldPersistBatchSuccessfully() {
        RegistroRepository repository = mock(RegistroRepository.class);
        RegistroBatchService registroBatchService = new RegistroBatchService(repository);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Registro r1 = new Registro(id1, UUID.randomUUID(), "Joao", "joao@example.com", "ATIVO", new BigDecimal("150.50"), LocalDate.of(2026, 10, 27));
        Registro r2 = new Registro(id2, UUID.randomUUID(), "Maria", "maria@example.com", "ATIVO", new BigDecimal("250.75"), LocalDate.of(2026, 10, 28));

        when(repository.existsById(id1)).thenReturn(false);
        when(repository.existsById(id2)).thenReturn(false);

        assertDoesNotThrow(() -> registroBatchService.saveBatch(List.of(r1, r2), 2));
        verify(repository, times(1)).saveAll(anyList());
        verify(repository, times(1)).flush();
    }

    @Test
    void shouldRejectDuplicateIds() {
        RegistroRepository repository = mock(RegistroRepository.class);
        RegistroBatchService registroBatchService = new RegistroBatchService(repository);

        UUID id = UUID.randomUUID();
        Registro r1 = new Registro(id, UUID.randomUUID(), "Joao", "joao@example.com", "ATIVO", new BigDecimal("150.50"), LocalDate.of(2026, 10, 27));
        Registro r2 = new Registro(id, UUID.randomUUID(), "Maria", "maria@example.com", "ATIVO", new BigDecimal("250.75"), LocalDate.of(2026, 10, 28));

        when(repository.existsById(id)).thenReturn(true);

        assertThrows(DuplicateRegistroException.class, () -> registroBatchService.saveBatch(List.of(r1, r2), 2));
        verify(repository, never()).saveAll(anyList());
    }
}
