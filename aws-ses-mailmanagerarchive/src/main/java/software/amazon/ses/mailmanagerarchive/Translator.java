package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.model.ArchiveState;
import software.amazon.awssdk.services.mailmanager.model.CreateArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.ListArchivesRequest;
import software.amazon.awssdk.services.mailmanager.model.ListArchivesResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ses.mailmanagerarchive.utils.ArchiveConvertorFromSdk;
import software.amazon.ses.mailmanagerarchive.utils.ArchiveConvertorToSdk;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanagerarchive.TagHelper.convertToSet;
import static software.amazon.ses.mailmanagerarchive.TagHelper.getNewDesiredTags;


public class Translator {

    /**
     * Request to create a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static CreateArchiveRequest translateToCreateRequest(final ResourceModel model, final ResourceHandlerRequest<ResourceModel> request) {
        Set<software.amazon.awssdk.services.mailmanager.model.Tag> tagsTobeAdded = convertToSet(getNewDesiredTags(request));

        return CreateArchiveRequest.builder()
                .archiveName(model.getArchiveName())
                .retention(ArchiveConvertorToSdk.convertToSdk(model.getRetention()))
                .kmsKeyArn(model.getKmsKeyArn())
                .tags(tagsTobeAdded)
                .build();
    }

    /**
     * Request to read a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to describe a resource
     */
    static GetArchiveRequest translateToReadRequest(final ResourceModel model) {
        return GetArchiveRequest.builder()
                .archiveId(model.getArchiveId())
                .build();
    }

    /**
     * Translates resource object from sdk into a resource model
     *
     * @param response the aws service describe resource response
     * @return model resource model
     */
    static ResourceModel translateFromReadResponse(final GetArchiveResponse response) {
        return ResourceModel.builder()
                .archiveId(response.archiveId())
                .archiveName(response.archiveName())
                .archiveState(response.archiveStateAsString())
                .archiveArn(response.archiveArn())
                .retention(ArchiveConvertorFromSdk.convertFromSdk(response.retention()))
                .kmsKeyArn(response.kmsKeyArn())
                .build();
    }

    /**
     * Request to delete a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to delete a resource
     */
    static DeleteArchiveRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteArchiveRequest.builder()
                .archiveId(model.getArchiveId())
                .build();
    }

    /**
     * Request to update properties of a previously created resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to modify a resource
     */
    static UpdateArchiveRequest translateToUpdateRequest(final ResourceModel model) {
        return UpdateArchiveRequest.builder()
                .archiveId(model.getArchiveId())
                .archiveName(model.getArchiveName())
                .retention(ArchiveConvertorToSdk.convertToSdk(model.getRetention()))
                .build();
    }

    /**
     * Request to list resources
     *
     * @param nextToken token passed to the aws service list resources request
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListArchivesRequest translateToListRequest(final String nextToken) {
        return ListArchivesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    /**
     * Request the list of resource's tags
     *
     * @param model resource model
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListTagsForResourceRequest translateToListTagsForResourceRequest(final ResourceModel model) {
        return ListTagsForResourceRequest.builder()
                .resourceArn(model.getArchiveArn())
                .build();
    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     *
     * @param response the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final ListArchivesResponse response) {
        return streamOfOrEmpty(response.archives())
                .filter(resource -> resource.archiveState() != null && !ArchiveState.PENDING_DELETION.equals(resource.archiveState()))
                .map(resource -> ResourceModel.builder()
                        // include only primary identifier
                        .archiveId(resource.archiveId())
                        .build())
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    /**
     * Request to add tags to a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static TagResourceRequest tagResourceRequest(final ResourceModel model, final Map<String, String> addedTags) {
        return TagResourceRequest.builder()
                .resourceArn(model.getArchiveArn())
                .tags(convertToSet(addedTags))
                .build();
    }

    /**
     * Request to add tags to a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static UntagResourceRequest untagResourceRequest(final ResourceModel model, final Set<String> removedTags) {
        return UntagResourceRequest.builder()
                .resourceArn(model.getArchiveArn())
                .tagKeys(removedTags)
                .build();
    }
}
