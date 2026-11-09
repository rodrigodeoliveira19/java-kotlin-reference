package br.com.process.service;

import br.com.process.entity.Registro;
import br.com.process.exception.DuplicateRegistroException;
import br.com.process.repository.RegistroRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class RegistroBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistroBatchService.class);

    private final RegistroRepository registroRepository;

    public RegistroBatchService(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    public void saveBatch(List<Registro> registros, int batchSize) {
        if (registros == null || registros.isEmpty()) {
            return;
        }

        //Verificar a necessidade de validação de duplicidade antes de persistir os registros.Gerar os id na inserção pode ser uma alternativa para evitar duplicidade de id.
        List<Registro> batch = new ArrayList<>();
        for (Registro registro : registros) {
            if (registroRepository.existsById(registro.getId())) {
                throw new DuplicateRegistroException("UUID duplicado detectado para o registro " + registro.getId());
            }
            batch.add(registro);

            if (batch.size() == batchSize) {
                persistBatch(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            persistBatch(batch);
        }
    }

    private void persistBatch(List<Registro> batch) {
        registroRepository.saveAll(batch);
        registroRepository.flush();
        LOG.info("Lote persistido com {} registros.", batch.size());
    }
}
