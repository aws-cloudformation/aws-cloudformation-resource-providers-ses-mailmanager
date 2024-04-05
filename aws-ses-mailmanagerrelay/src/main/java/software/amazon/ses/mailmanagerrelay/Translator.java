package software.amazon.ses.mailmanagerrelay;

import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.ListRelaysRequest;
import software.amazon.awssdk.services.mailmanager.model.ListRelaysResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateRelayRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanagerrelay.utils.RelayAuthConvertorToSdk.ConvertToSdk;

public class Translator {

    /**
     * Request to create a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static CreateRelayRequest translateToCreateRequest(final ResourceModel model) {
        return CreateRelayRequest.builder()
                .authentication(ConvertToSdk(model.getAuthentication()))
                .relayName(model.getRelayName())
                .serverName(model.getServerName())
                .serverPort(model.getServerPort().intValue())
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
                .relayARN(response.relayARN())
                .serverName(response.serverName())
                .serverPort(response.serverPort().doubleValue())
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
    static AwsRequest tagResourceRequest(final ResourceModel model, final Map<String, String> addedTags) {
        final AwsRequest awsRequest = null;
        // TODO: construct a request
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L39-L43
        return awsRequest;
    }

    /**
     * Request to add tags to a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static AwsRequest untagResourceRequest(final ResourceModel model, final Set<String> removedTags) {
        final AwsRequest awsRequest = null;
        // TODO: construct a request
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L39-L43
        return awsRequest;
    }
}
