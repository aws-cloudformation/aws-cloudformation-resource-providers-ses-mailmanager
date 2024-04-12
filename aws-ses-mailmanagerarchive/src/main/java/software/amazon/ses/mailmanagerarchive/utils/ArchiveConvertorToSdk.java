package software.amazon.ses.mailmanagerarchive.utils;

import software.amazon.ses.mailmanagerarchive.ArchiveRetention;


public class ArchiveConvertorToSdk {
    public static software.amazon.awssdk.services.mailmanager.model.ArchiveRetention convertToSdk(ArchiveRetention source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.ArchiveRetention.builder()
                .retentionPeriod(source.getRetentionPeriod())
                .build();
    }
}
