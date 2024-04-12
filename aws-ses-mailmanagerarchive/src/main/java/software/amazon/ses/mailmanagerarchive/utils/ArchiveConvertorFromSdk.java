package software.amazon.ses.mailmanagerarchive.utils;

import software.amazon.ses.mailmanagerarchive.ArchiveRetention;


public class ArchiveConvertorFromSdk {
    public static ArchiveRetention convertFromSdk(software.amazon.awssdk.services.mailmanager.model.ArchiveRetention source) {
        if (source == null) {
            return null;
        }
        return ArchiveRetention.builder()
                .retentionPeriod(source.retentionPeriodAsString())
                .build();
    }
}
