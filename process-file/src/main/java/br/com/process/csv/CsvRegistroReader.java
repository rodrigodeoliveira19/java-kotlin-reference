package br.com.process.csv;

import br.com.process.entity.Registro;
import jakarta.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class CsvRegistroReader {

    public List<Registro> read(InputStream inputStream) throws IOException {
        List<Registro> registros = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String header = String.join(",", parser.getHeaderNames());
            CsvRowValidator.validateHeader(header);

            for (CSVRecord record : parser) {
                long lineNumber = record.getRecordNumber();
                String[] values = record.values();
                CsvRowValidator.validateRecord(values, lineNumber);

                Registro registro = new Registro(
                        UUID.randomUUID(),//UUID.fromString(values[0].trim()),
                        UUID.fromString(values[1].trim()),
                        values[2].trim(),
                        values[3].trim(),
                        values[4].trim(),
                        new BigDecimal(values[5].trim()),
                        LocalDate.parse(values[6].trim())
                );

                registros.add(registro);
            }
        }

        return registros;
    }
}
