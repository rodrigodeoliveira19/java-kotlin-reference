package br.com.process.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvRowValidatorTest {

    @Test
    void shouldAcceptValidHeader() {
        assertDoesNotThrow(() -> CsvRowValidator.validateHeader("id,id_cliente,nome,email,status,valor,data"));
    }

    @Test
    void shouldRejectInvalidHeader() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateHeader("id,nome,email"));
        assertTrue(ex.getMessage().contains("Cabeçalho inválido"));
    }

    @Test
    void shouldAcceptValidRow() {
        String[] row = {
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440000",
                "Joao",
                "joao@example.com",
                "ATIVO",
                "150.50",
                "2026-10-27"
        };

        assertDoesNotThrow(() -> CsvRowValidator.validateRecord(row, 2));
    }

    @Test
    void shouldRejectWrongColumnCount() {
        String[] row = {"1", "2", "3"};
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateRecord(row, 2));
        assertTrue(ex.getMessage().contains("quantidade inesperada de colunas"));
    }

    @Test
    void shouldRejectInvalidUuid() {
        String[] row = {
                "not-a-uuid",
                "660e8400-e29b-41d4-a716-446655440000",
                "Joao",
                "joao@example.com",
                "ATIVO",
                "150.50",
                "2026-10-27"
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateRecord(row, 2));
        assertTrue(ex.getMessage().contains("UUID válido"));
    }

    @Test
    void shouldRejectInvalidDate() {
        String[] row = {
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440000",
                "Joao",
                "joao@example.com",
                "ATIVO",
                "150.50",
                "2026-02-30"
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateRecord(row, 2));
        assertTrue(ex.getMessage().contains("data"));
    }

    @Test
    void shouldRejectInvalidDecimal() {
        String[] row = {
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440000",
                "Joao",
                "joao@example.com",
                "ATIVO",
                "abc",
                "2026-10-27"
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateRecord(row, 2));
        assertTrue(ex.getMessage().contains("decimal"));
    }

    @Test
    void shouldRejectMissingRequiredField() {
        String[] row = {
                "550e8400-e29b-41d4-a716-446655440000",
                "660e8400-e29b-41d4-a716-446655440000",
                "",
                "joao@example.com",
                "ATIVO",
                "150.50",
                "2026-10-27"
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CsvRowValidator.validateRecord(row, 2));
        assertTrue(ex.getMessage().contains("campo obrigatório 'nome'"));
    }
}
