package software.amazon.ses.mailmanagerarchive.utils;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ArchiveState;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.ProxyClient;

public class ArchiveGetHelper {
    public static GetArchiveResponse getArchiveWithStateCheck(
            final ProxyClient<MailManagerClient> proxyClient,
            final String archiveId
    ) {
        GetArchiveRequest request = GetArchiveRequest.builder()
                .archiveId(archiveId)
                .build();

        // First check if the Archive is in PENDING_DELETION state. If so, throw ResourceNotFoundException.
        // Otherwise, ResourceNotFoundException will directly be thrown by proxyClient.
        GetArchiveResponse response = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getArchive);

        // Since Archive resource can be kept during a Cooldown period before performing real deletion.
        if (response != null && ArchiveState.PENDING_DELETION.equals(response.archiveState())) {
            throw ResourceNotFoundException.builder().message(String.format("Archive with ID <%s> is PENDING_DELETION state", archiveId)).build();
        }

        return response;
    }
}
