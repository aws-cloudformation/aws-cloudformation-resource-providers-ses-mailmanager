package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.cloudformation.LambdaWrapper;

import java.net.URI;

public class ClientBuilder {
  public static MailManagerClient getClient() {
    return MailManagerClient.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
  }
}
