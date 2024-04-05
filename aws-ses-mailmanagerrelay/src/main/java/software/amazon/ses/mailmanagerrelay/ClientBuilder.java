package software.amazon.ses.mailmanagerrelay;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    public static MailManagerClient getClient() {
        return MailManagerClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
    }
}
