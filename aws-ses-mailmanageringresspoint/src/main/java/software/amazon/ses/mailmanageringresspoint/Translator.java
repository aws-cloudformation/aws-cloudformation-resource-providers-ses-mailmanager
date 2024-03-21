package software.amazon.ses.mailmanageringresspoint;

import lombok.NonNull;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressPointConfiguration;
import software.amazon.awssdk.services.mailmanager.model.IngressPointType;
import software.amazon.awssdk.services.mailmanager.model.ListIngressPointsRequest;
import software.amazon.awssdk.services.mailmanager.model.ListIngressPointsResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointRequest;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {

  /**
   * Request to create a MailManager IngressPoint resource
   *
   * @param model resource model
   * @return CreateIngressPointRequest the aws service request to create a resource
   */
  static CreateIngressPointRequest translateToCreateRequest(final ResourceModel model) {
    modelValidator(model);

    return CreateIngressPointRequest.builder()
            .ingressPointName(model.getIngressPointName())
            .type(model.getType())
            .ingressPointConfiguration(
                    model.getIngressPointConfiguration() == null ?
                            null : IngressPointConfiguration.builder()
                            .smtpPassword(model.getIngressPointConfiguration().getSmtpPassword())
                            .secretArn(model.getIngressPointConfiguration().getSecretArn())
                            .build()
            )
            .ruleSetId(model.getRuleSetId())
            .trafficPolicyId(model.getTrafficPolicyId())
            .build();
  }

  /**
   * Request to read a MailManager IngressPoint resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetIngressPointRequest translateToReadRequest(@NonNull final ResourceModel model) {
    return GetIngressPointRequest.builder()
            .ingressPointId(model.getIngressPointId())
            .build();
  }

  /**
   * Translates MailManager IngressPoint resource object from sdk into a resource model
   *
   * @param ingressPointResponse the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(@NonNull final GetIngressPointResponse ingressPointResponse) {
    return ResourceModel.builder()
            .ingressPointName(ingressPointResponse.ingressPointName())
            .ingressPointId(ingressPointResponse.ingressPointId())
            .ingressPointArn(ingressPointResponse.ingressPointArn())
            .trafficPolicyId(ingressPointResponse.trafficPolicyId())
            .ruleSetId(ingressPointResponse.ruleSetId())
            .ingressPointStatus(ingressPointResponse.statusAsString())
            .type(ingressPointResponse.typeAsString())
            .aRecord(ingressPointResponse.aRecord())
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteIngressPointRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteIngressPointRequest.builder()
            .ingressPointId(model.getIngressPointId())
            .build();
  }

  /**
   * Request to update properties of a previously created resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateIngressPointRequest translateToUpdateRequest(final ResourceModel model) {
    modelValidator(model);
    UpdateIngressPointRequest.Builder builder = UpdateIngressPointRequest.builder()
            .ingressPointId(model.getIngressPointId())
            .ruleSetId(model.getRuleSetId())
            .trafficPolicyId(model.getTrafficPolicyId());

    // AUTH RELAY
    if (Objects.equals(model.getType(), IngressPointType.AUTH_RELAY.toString())) {
      return builder.ingressPointConfiguration(IngressPointConfiguration.builder()
              .smtpPassword(model.getIngressPointConfiguration().getSmtpPassword()).build()
      ).build();
    }

    // OPEN RELAY
    return builder.build();
  }

  private static void modelValidator(final ResourceModel model) {
    if (Objects.equals(model.getType(), IngressPointType.OPEN_RELAY.toString()) && model.getIngressPointConfiguration() != null) {
      throw new CfnInvalidRequestException("An OPEN_RELAY MUST NOT be established using IngressPointConfiguration, since it's utilized for AUTH_RELAY authentication.");
    }
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListIngressPointsRequest translateToListRequest(final String nextToken) {
    return ListIngressPointsRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  static List<ResourceModel> translateFromListResponse(final ListIngressPointsResponse response) {
    return streamOfOrEmpty(response.ingressPoints())
            .map(ingressPoint -> ResourceModel.builder()
                    // include only primary identifier
                    .ingressPointId(ingressPoint.ingressPointId())
                    .build()
            )
            .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
  }

  /**
   * Request to add tags to a resource
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
