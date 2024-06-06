package software.amazon.ses.mailmanagerrelay;

import software.amazon.awssdk.services.mailmanager.model.CreateRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.ListRelaysRequest;
import software.amazon.awssdk.services.mailmanager.model.ListRelaysResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateRelayRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanagerrelay.TagHelper.convertToSet;
import static software.amazon.ses.mailmanagerrelay.TagHelper.getNewDesiredTags;
import static software.amazon.ses.mailmanagerrelay.utils.RelayAuthConvertorFromSdk.ConvertFromSdk;
import static software.amazon.ses.mailmanagerrelay.utils.RelayAuthConvertorToSdk.ConvertToSdk;

public class Translator {

    /**
     * Request to create a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static CreateRelayRequest translateToCreateRequest(final ResourceModel model, final ResourceHandlerRequest<ResourceModel> request) {

        Set<software.amazon.awssdk.services.mailmanager.model.Tag> tagsToBeAdded = convertToSet(getNewDesiredTags(request));

        return CreateRelayRequest.builder()
                .authentication(ConvertToSdk(model.getAuthentication()))
                .relayName(model.getRelayName())
                .serverName(model.getServerName())
                .serverPort(model.getServerPort().intValue())
                .tags(tagsToBeAdded)
                .build();
    }

    /**
     * Request to read a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to describe a resource
     */
    static GetRelayRequest translateToReadRequest(final ResourceModel model) {
        return GetRelayRequest.builder()
                .relayId(model.getRelayId())
                .build();
    }

    /**
     * Translates resource object from sdk into a resource model
     *
     * @param response the aws service describe resource response
     * @return model resource model
     */
    static ResourceModel translateFromReadResponse(final GetRelayResponse response) {
        return ResourceModel.builder()
                .relayId(response.relayId())
                .relayName(response.relayName())
                .relayArn(response.relayArn())
                .serverName(response.serverName())
                .serverPort(response.serverPort().doubleValue())
                .authentication(ConvertFromSdk(response.authentication()))
                .build();
    }

    /**
     * Request to delete a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to delete a resource
     */
    static DeleteRelayRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteRelayRequest.builder()
                .relayId(model.getRelayId())
                .build();
    }

    /**
     * Request to update properties of a previously created resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to modify a resource
     */
    static UpdateRelayRequest translateToUpdateRequest(final ResourceModel model) {
        return UpdateRelayRequest.builder()
                .relayId(model.getRelayId())
                .relayName(model.getRelayName())
                .authentication(ConvertToSdk(model.getAuthentication()))
                .serverName(model.getServerName())
                .serverPort(model.getServerPort().intValue())
                .build();
    }

    /**
     * Request to list resources
     *
     * @param nextToken token passed to the aws service list resources request
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListRelaysRequest translateToListRequest(final String nextToken) {
        return ListRelaysRequest.builder()
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
                .resourceArn(model.getRelayArn())
                .build();
    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     *
     * @param response the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final ListRelaysResponse response) {
        return streamOfOrEmpty(response.relays())
                .map(resource -> ResourceModel.builder()
                        // include only primary identifier
                        .relayId(resource.relayId())
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
                .resourceArn(model.getRelayArn())
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
                .resourceArn(model.getRelayArn())
                .tagKeys(removedTags)
                .build();
    }
}
