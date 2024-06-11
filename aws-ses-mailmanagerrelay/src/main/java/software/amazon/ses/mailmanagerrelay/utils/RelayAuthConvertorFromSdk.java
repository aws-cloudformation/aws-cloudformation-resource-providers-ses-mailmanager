package software.amazon.ses.mailmanagerrelay.utils;

import software.amazon.awssdk.services.mailmanager.model.RelayAuthentication;

import java.util.HashMap;

public class RelayAuthConvertorFromSdk {
    public static software.amazon.ses.mailmanagerrelay.RelayAuthentication convertFromSdk(RelayAuthentication auth) {
        if (auth == null) {
            return null;
        }

        software.amazon.ses.mailmanagerrelay.RelayAuthentication.RelayAuthenticationBuilder builder = software.amazon.ses.mailmanagerrelay.RelayAuthentication.builder();

        if (auth.secretArn() != null) {
            builder.secretArn(auth.secretArn());
        } else {
            builder.noAuthentication(new HashMap<>());
        }
        return builder.build();
    }
}
