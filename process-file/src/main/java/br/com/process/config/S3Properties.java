package br.com.process.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Introspected;

import java.time.Duration;

@Introspected
@ConfigurationProperties("s3")
public class S3Properties {

    private String bucket = "process-file-uploads";
    private String region = "us-east-1";
    private Duration scanInterval = Duration.ofMinutes(1);
    private int batchSize = 100;
    private Prefixes prefix = new Prefixes();

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Duration getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(Duration scanInterval) {
        this.scanInterval = scanInterval;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Prefixes getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefixes prefix) {
        this.prefix = prefix;
    }

    public static class Prefixes {
        private String entrada = "entrada/";
        private String processamento = "processamento/";
        private String processado = "processado/";
        private String erro = "erro/";

        public String getEntrada() {
            return entrada;
        }

        public void setEntrada(String entrada) {
            this.entrada = entrada;
        }

        public String getProcessamento() {
            return processamento;
        }

        public void setProcessamento(String processamento) {
            this.processamento = processamento;
        }

        public String getProcessado() {
            return processado;
        }

        public void setProcessado(String processado) {
            this.processado = processado;
        }

        public String getErro() {
            return erro;
        }

        public void setErro(String erro) {
            this.erro = erro;
        }
    }
}
