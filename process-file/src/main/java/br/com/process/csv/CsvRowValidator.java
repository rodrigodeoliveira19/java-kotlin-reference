package br.com.process.csv;

import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Singleton
public class CsvRowValidator {

    public static final List<String> EXPECTED_HEADERS = List.of(
            "id",
            "id_cliente",
            "nome",
            "email",
            "status",
            "valor",
            "data"
    );

    public static final String EXPECTED_HEADER_LINE = String.join(",", EXPECTED_HEADERS);

    public static void validateHeader(String header) {
        if (header == null) {
            throw new IllegalArgumentException("Cabeçalho ausente.");
        }

        String normalized = header.trim();
        if (!EXPECTED_HEADER_LINE.equals(normalized)) {
            throw new IllegalArgumentException("Cabeçalho inválido. Esperado: '" + EXPECTED_HEADER_LINE + "' e recebido: '" + normalized + "'.");
        }
    }

    public static void validateRecord(String[] values, long lineNumber) {
        if (values == null || values.length != EXPECTED_HEADERS.size()) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": quantidade inesperada de colunas. Esperado " + EXPECTED_HEADERS.size() + ", recebido " + (values == null ? 0 : values.length) + ".");
        }

        String[] normalized = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            normalized[i] = values[i] == null ? "" : values[i].trim();
        }

        String id = requireNonBlank(normalized[0], "id", lineNumber);
        String idCliente = requireNonBlank(normalized[1], "id_cliente", lineNumber);
        String nome = requireNonBlank(normalized[2], "nome", lineNumber);
        String email = requireNonBlank(normalized[3], "email", lineNumber);
        String status = requireNonBlank(normalized[4], "status", lineNumber);
        String valor = requireNonBlank(normalized[5], "valor", lineNumber);
        String data = requireNonBlank(normalized[6], "data", lineNumber);

        parseUuid(id, "id", lineNumber);
        parseUuid(idCliente, "id_cliente", lineNumber);
        parseDecimal(valor, lineNumber);
        parseDate(data, lineNumber);

        if (nome.length() > 255) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'nome' excede 255 caracteres.");
        }
        if (email.length() > 255) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'email' excede 255 caracteres.");
        }
        if (status.length() > 30) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'status' excede 30 caracteres.");
        }
    }

    private static String requireNonBlank(String value, String field, long lineNumber) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo obrigatório '" + field + "' ausente ou em branco.");
        }
        return value.trim();
    }

    private static UUID parseUuid(String value, String field, long lineNumber) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo '" + field + "' não é um UUID válido: '" + value + "'.");
        }
    }

    private static BigDecimal parseDecimal(String value, long lineNumber) {
        try {
            BigDecimal decimal = new BigDecimal(value.trim());
            if (decimal.scale() > 2) {
                throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'valor' deve ter no máximo 2 casas decimais.");
            }
            return decimal;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'valor' não é um decimal válido: '" + value + "'.");
        }
    }

    private static LocalDate parseDate(String value, long lineNumber) {
        try {
            return LocalDate.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Linha " + lineNumber + ": campo 'data' não está no formato ISO yyyy-MM-dd: '" + value + "'.");
        }
    }
}
