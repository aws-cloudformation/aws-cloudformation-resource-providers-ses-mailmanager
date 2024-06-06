package software.amazon.ses.mailmanageringresspoint;

import lombok.NonNull;
import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressPointConfiguration;
import software.amazon.awssdk.services.mailmanager.model.IngressPointType;
import software.amazon.awssdk.services.mailmanager.model.ListIngressPointsRequest;
import software.amazon.awssdk.services.mailmanager.model.ListIngressPointsResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointRequest;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanageringresspoint.TagHelper.convertToSet;
import static software.amazon.ses.mailmanageringresspoint.TagHelper.getNewDesiredTags;
import static software.amazon.ses.mailmanageringresspoint.utils.TagsConvertor.convertToSdk;

public class Translator {

  /**
   * Request to create a MailManager IngressPoint resource
   *
   * @param model resource model
   * @return CreateIngressPointRequest the aws service request to create a resource
   */
  static CreateIngressPointRequest translateToCreateRequest(final ResourceModel model, final ResourceHandlerRequest<ResourceModel> request) {
    modelValidator(model);

    Set<software.amazon.awssdk.services.mailmanager.model.Tag> tagsToBeAdded = convertToSet(getNewDesiredTags(request));

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
            .tags(tagsToBeAdded)
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
            .status(ingressPointResponse.statusAsString())
            .statusToUpdate(ingressPointResponse.statusAsString())
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
   * Request the list of resource's tags
   *
   * @param model resource model
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListTagsForResourceRequest translateToListTagsForResourceRequest(final ResourceModel model) {
    return ListTagsForResourceRequest.builder()
            .resourceArn(model.getIngressPointArn())
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
            .statusToUpdate(model.getStatusToUpdate())
            .trafficPolicyId(model.getTrafficPolicyId());

    // AUTH RELAY
    if (Objects.equals(model.getType(), IngressPointType.AUTH.toString()) && model.getIngressPointConfiguration() != null) {
      return builder.ingressPointConfiguration(IngressPointConfiguration.builder()
              .smtpPassword(model.getIngressPointConfiguration().getSmtpPassword()).build()
      ).build();
    }

    // OPEN RELAY
    return builder.build();
  }

  private static void modelValidator(final ResourceModel model) {
    if (Objects.equals(model.getType(), IngressPointType.OPEN.toString()) && model.getIngressPointConfiguration() != null) {
      throw new CfnInvalidRequestException("An OPEN IngressPoint MUST NOT be established using IngressPointConfiguration, since it's utilized for AUTH IngressPoint authentication.");
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
   *
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static TagResourceRequest tagResourceRequest(final ResourceModel model, final Map<String, String> addedTags) {
    return TagResourceRequest.builder()
            .resourceArn(model.getIngressPointArn())
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
            .resourceArn(model.getIngressPointArn())
            .tagKeys(removedTags)
            .build();
  }
}
