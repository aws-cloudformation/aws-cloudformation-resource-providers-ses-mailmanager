package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.model.ArchiveRetention;
import software.amazon.awssdk.services.mailmanager.model.ArchiveState;
import software.amazon.awssdk.services.mailmanager.model.CreateArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.ListArchivesResponse;
import software.amazon.awssdk.services.mailmanager.model.Archive;
import software.amazon.awssdk.services.mailmanager.model.RetentionPeriod;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveResponse;


public class HandlerHelper {
    public static final String ARCHIVE_ID = "archive_id";
    public static final String ARCHIVE_NAME = "archive_name";
    public static final String ARCHIVE_ARN = "archive_arn";
    public static final String ARCHIVE_KMS_ARN = "archive_kms_arn";
    public static final Integer MESSAGE_RETENTION_PERIOD_DAYS = 10;
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";

    static GetArchiveResponse fakeGetArchiveResponse() {
        return GetArchiveResponse.builder()
                .archiveId(ARCHIVE_ID)
                .archiveArn(ARCHIVE_ARN)
                .archiveName(ARCHIVE_NAME)
                .archiveState(ArchiveState.ACTIVE)
                .messageRetentionPeriodDays(MESSAGE_RETENTION_PERIOD_DAYS)
                .retention(ArchiveRetention.builder()
                        .retentionPeriod(RetentionPeriod.FIVE_YEARS)
                        .build())
                .kmsKeyArn(ARCHIVE_KMS_ARN)
                .build();
    }

    static GetArchiveResponse fakeGetDeletedArchiveResponse() {
        return GetArchiveResponse.builder()
                .archiveId(ARCHIVE_ID)
                .archiveArn(ARCHIVE_ARN)
                .archiveName(ARCHIVE_NAME)
                .archiveState(ArchiveState.PENDING_DELETION)
                .messageRetentionPeriodDays(MESSAGE_RETENTION_PERIOD_DAYS)
                .retention(ArchiveRetention.builder()
                        .retentionPeriod(RetentionPeriod.FIVE_YEARS)
                        .build())
                .kmsKeyArn(ARCHIVE_KMS_ARN)
                .build();
    }

    static CreateArchiveResponse fakeCreateArchiveResponse() {
        return CreateArchiveResponse.builder()
                .archiveId(ARCHIVE_ID)
                .build();
    }

    static UpdateArchiveResponse fakeUpdateArchiveResponse() {
        return UpdateArchiveResponse.builder().build();
    }

    static DeleteArchiveResponse fakeDeleteArchiveResponse() {
        return DeleteArchiveResponse.builder().build();
    }

    static ListArchivesResponse fakeListArchivesResponse() {
        return ListArchivesResponse.builder().archives(
                Archive.builder().archiveId("id_one").archiveState(ArchiveState.ACTIVE).build(),
                Archive.builder().archiveId("id_two").archiveState(ArchiveState.PENDING_DELETION).build()
        ).build();
    }
}
