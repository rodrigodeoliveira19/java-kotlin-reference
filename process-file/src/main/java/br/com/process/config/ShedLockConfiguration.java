package br.com.process.config;

import io.lettuce.core.api.StatefulRedisConnection;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.lettuce.LettuceLockProvider;

/*
StatefulRedisConnection: conexão Redis disponibilizada pelo Lettuce.
LettuceLockProvider: implementação do ShedLock que utiliza Redis.
csv-import: identificador do ambiente ou namespace utilizado pelo provider para organizar os locks.
* */
@Factory
public class ShedLockConfiguration {

    @Singleton
    public LockProvider lockProvider(
            StatefulRedisConnection<String, String> connection) {

        return new LettuceLockProvider(
                connection,
                "csv-import"
        );
    }
}