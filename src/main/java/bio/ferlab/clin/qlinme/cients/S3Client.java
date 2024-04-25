package bio.ferlab.clin.qlinme.cients;

import bio.ferlab.clin.qlinme.model.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.time.Duration;

@Slf4j
public class S3Client {

  private final software.amazon.awssdk.services.s3.S3Client s3Client;

  public S3Client(String url, String accessKey, String secretKey, int timeoutMs) {
    var s3Conf = S3Configuration.builder().pathStyleAccessEnabled(true).build();
    var s3Creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    this.s3Client = software.amazon.awssdk.services.s3.S3Client.builder()
      .credentialsProvider(s3Creds)
      .endpointOverride(URI.create(url))
      .region(Region.US_EAST_1)
      .serviceConfiguration(s3Conf)
      .httpClientBuilder(ApacheHttpClient.builder()
        .connectionTimeout(Duration.ofMillis(timeoutMs))
        .socketTimeout(Duration.ofMillis(timeoutMs))
      ).build();
  }

  public void saveMetadata(String bucket, String batchId, String metadata) {
    var key = batchId+ "/metadata.json";
    var request = PutObjectRequest.builder().bucket(bucket).key(key).build();
    log.info("Save metadata: {}", key);
    s3Client.putObject(request, RequestBody.fromString(metadata));
  }

}
