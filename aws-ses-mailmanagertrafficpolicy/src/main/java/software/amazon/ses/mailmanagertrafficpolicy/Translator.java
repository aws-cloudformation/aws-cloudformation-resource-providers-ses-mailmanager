package software.amazon.ses.mailmanagertrafficpolicy;

import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTrafficPoliciesRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTrafficPoliciesResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateTrafficPolicyRequest;
import software.amazon.ses.mailmanagertrafficpolicy.utils.PolicyStatementConvertorFromSdk;
import software.amazon.ses.mailmanagertrafficpolicy.utils.PolicyStatementConvertorToSdk;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Translator {

  /**
   * Request to create a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static CreateTrafficPolicyRequest translateToCreateRequest(final ResourceModel model) {
    return CreateTrafficPolicyRequest.builder()
            .trafficPolicyName(model.getTrafficPolicyName())
            .policyStatements(PolicyStatementConvertorToSdk.ConvertToSdk(model.getPolicyStatements()))
            .maxMessageSizeBytes(model.getMaxMessageSizeBytes() == null ? null : model.getMaxMessageSizeBytes().intValue())
            .defaultAction(model.getDefaultAction())
            .build();
  }

  /**
   * Request to read a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetTrafficPolicyRequest translateToReadRequest(final ResourceModel model) {
    return GetTrafficPolicyRequest.builder()
            .trafficPolicyId(model.getTrafficPolicyId())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   *
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetTrafficPolicyResponse response) {
    return ResourceModel.builder()
            .trafficPolicyId(response.trafficPolicyId())
            .trafficPolicyName(response.trafficPolicyName())
            .trafficPolicyArn(response.trafficPolicyArn())
            .policyStatements(PolicyStatementConvertorFromSdk.ConvertFromSdk(response.policyStatements()))
            .maxMessageSizeBytes(response.maxMessageSizeBytes() == null ? 0 : Double.valueOf(response.maxMessageSizeBytes()))
            .defaultAction(response.defaultActionAsString())
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteTrafficPolicyRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteTrafficPolicyRequest.builder()
            .trafficPolicyId(model.getTrafficPolicyId())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateTrafficPolicyRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateTrafficPolicyRequest.builder()
            .trafficPolicyId(model.getTrafficPolicyId())
            .trafficPolicyName(model.getTrafficPolicyName())
            .policyStatements(PolicyStatementConvertorToSdk.ConvertToSdk(model.getPolicyStatements()))
            .maxMessageSizeBytes(model.getMaxMessageSizeBytes() == null ? null : model.getMaxMessageSizeBytes().intValue())
            .defaultAction(model.getDefaultAction())
            .build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListTrafficPoliciesRequest translateToListRequest(final String nextToken) {
    return ListTrafficPoliciesRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   *
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListTrafficPoliciesResponse response) {
    return streamOfOrEmpty(response.trafficPolicies())
            .map(resource -> ResourceModel.builder()
                    // include only primary identifier
                    .trafficPolicyId(resource.trafficPolicyId())
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
