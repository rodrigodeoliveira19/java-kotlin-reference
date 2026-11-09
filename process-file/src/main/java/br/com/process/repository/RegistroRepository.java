package br.com.process.repository;

import br.com.process.entity.Registro;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, UUID> {
}
