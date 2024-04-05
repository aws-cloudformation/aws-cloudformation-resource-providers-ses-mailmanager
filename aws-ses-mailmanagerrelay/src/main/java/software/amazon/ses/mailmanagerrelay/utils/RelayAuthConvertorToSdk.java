package software.amazon.ses.mailmanagerrelay.utils;

import software.amazon.awssdk.services.mailmanager.model.RelayAuthentication;
import software.amazon.awssdk.services.mailmanager.model.RelayPasswordAuthentication;

public class RelayAuthConvertorToSdk {
    public static RelayAuthentication ConvertToSdk(software.amazon.ses.mailmanagerrelay.RelayAuthentication auth) {
        if (auth == null) {
            return null;
        }
        return RelayAuthentication.builder()
                .passwordAuthentication(
                        auth.getPasswordAuthentication() == null ? null :
                                RelayPasswordAuthentication.builder()
                                        .username(auth.getPasswordAuthentication().getUsername())
                                        .password(auth.getPasswordAuthentication().getPassword())
                                        .build()
                )
                .secretArn(auth.getSecretArn())
                .build();
    }
}
