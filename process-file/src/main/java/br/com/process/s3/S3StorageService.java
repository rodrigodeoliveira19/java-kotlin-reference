package br.com.process.s3;

import br.com.process.config.S3Properties;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;

@Singleton
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;

    public S3StorageService(S3Client s3Client, S3Properties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    public List<String> listObjects(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(properties.getBucket())
                .prefix(prefix)
                .build();

        return s3Client.listObjectsV2Paginator(request).stream()
                .flatMap(response -> response.contents().stream())
                .map(S3Object::key)
                .filter(key -> !key.endsWith("/"))
                .toList();
    }

    public List<String> listInboundObjects() {
        return listObjects(properties.getPrefix().getEntrada());
    }

    public String moveToProcessing(String sourceKey) {
        return moveObject(sourceKey, properties.getPrefix().getEntrada(), properties.getPrefix().getProcessamento());
    }

    public String moveToProcessed(String sourceKey) {
        return moveObject(sourceKey, properties.getPrefix().getProcessamento(), properties.getPrefix().getProcessado());
    }

    public String moveToError(String sourceKey) {
        return moveObject(sourceKey, properties.getPrefix().getProcessamento(), properties.getPrefix().getErro());
    }

    public InputStream readObject(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    private String moveObject(String sourceKey, String sourcePrefix, String destinationPrefix) {
        String fileName = sourceKey.startsWith(sourcePrefix)
                ? sourceKey.substring(sourcePrefix.length())
                : sourceKey;
        String destinationKey = destinationPrefix + fileName;

        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(properties.getBucket())
                .sourceKey(sourceKey)
                .destinationBucket(properties.getBucket())
                .destinationKey(destinationKey)
                .build());

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(sourceKey)
                .build());

        return destinationKey;
    }
}
