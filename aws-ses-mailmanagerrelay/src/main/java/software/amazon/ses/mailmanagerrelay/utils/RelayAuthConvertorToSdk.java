package software.amazon.ses.mailmanagerrelay.utils;

import software.amazon.awssdk.services.mailmanager.model.NoAuthentication;
import software.amazon.awssdk.services.mailmanager.model.RelayAuthentication;

public class RelayAuthConvertorToSdk {
    public static RelayAuthentication ConvertToSdk(software.amazon.ses.mailmanagerrelay.RelayAuthentication auth) {
        if (auth == null) {
            return null;
        }

        RelayAuthentication.Builder builder = RelayAuthentication.builder();

        if (auth.getSecretArn() != null) {
            builder.secretArn(auth.getSecretArn());
        } else {
            builder.noAuthentication(NoAuthentication.builder().build());
        }
        return builder.build();
    }
}
