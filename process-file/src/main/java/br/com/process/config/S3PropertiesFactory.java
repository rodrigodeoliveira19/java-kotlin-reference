//package br.com.process.config;
//
//import io.micronaut.context.annotation.Bean;
//import io.micronaut.context.annotation.Factory;
//import io.micronaut.context.annotation.Value;
//import io.micronaut.core.annotation.Nullable;
//
//import java.time.Duration;
//
//@Factory
//public class S3PropertiesFactory {
//
//    @Bean
//    S3Properties s3Properties(
//            @Nullable @Value("${s3.bucket:process-file-uploads}") String bucket,
//            @Nullable @Value("${s3.region:us-east-1}") String region,
//            @Nullable @Value("${s3.scan-interval:PT1M}") String scanInterval,
//            @Nullable @Value("${s3.batch-size:100}") Integer batchSize,
//            @Nullable @Value("${s3.prefix.entrada:entrada/}") String entrada,
//            @Nullable @Value("${s3.prefix.processamento:processamento/}") String processamento,
//            @Nullable @Value("${s3.prefix.processado:processado/}") String processado,
//            @Nullable @Value("${s3.prefix.erro:erro/}") String erro) {
//
//        S3Properties properties = new S3Properties();
//        properties.setBucket(bucket == null ? "process-file-uploads" : bucket);
//        properties.setRegion(region == null ? "us-east-1" : region);
//        properties.setScanInterval(scanInterval == null ? Duration.ofMinutes(1) : Duration.parse(scanInterval));
//        properties.setBatchSize(batchSize == null ? 100 : batchSize);
//
//        S3Properties.Prefixes prefixes = new S3Properties.Prefixes();
//        prefixes.setEntrada(entrada == null ? "entrada/" : entrada);
//        prefixes.setProcessamento(processamento == null ? "processamento/" : processamento);
//        prefixes.setProcessado(processado == null ? "processado/" : processado);
//        prefixes.setErro(erro == null ? "erro/" : erro);
//        properties.setPrefix(prefixes);
//
//        return properties;
//    }
//}
