package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.services.mailmanager.model.CreateAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.ListAddonSubscriptionsRequest;
import software.amazon.awssdk.services.mailmanager.model.ListAddonSubscriptionsResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanageraddonsubscription.TagHelper.convertToSet;
import static software.amazon.ses.mailmanageraddonsubscription.utils.TagsConvertor.convertToSdk;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static CreateAddonSubscriptionRequest translateToCreateRequest(final ResourceModel model) {
    return CreateAddonSubscriptionRequest.builder()
            .addonName(model.getAddonName())
            .tags(convertToSdk(model.getTags()))
            .build();
  }

  /**
   * Request to read a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetAddonSubscriptionRequest translateToReadRequest(final ResourceModel model) {
    return GetAddonSubscriptionRequest.builder()
            .addonSubscriptionId(model.getAddonSubscriptionId())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   *
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetAddonSubscriptionResponse response, String resourceId) {
    return ResourceModel.builder()
            .addonSubscriptionId(resourceId)
            .addonName(response.addonName())
            .addonSubscriptionArn(response.addonSubscriptionArn())
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteAddonSubscriptionRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteAddonSubscriptionRequest.builder()
            .addonSubscriptionId(model.getAddonSubscriptionId())
            .build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListAddonSubscriptionsRequest translateToListRequest(final String nextToken) {
    return ListAddonSubscriptionsRequest.builder()
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
            .resourceArn(model.getAddonSubscriptionArn())
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   *
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListAddonSubscriptionsResponse response) {
    return streamOfOrEmpty(response.addonSubscriptions())
            .map(resource -> ResourceModel.builder()
                    // include only primary identifier
                    .addonSubscriptionId(resource.addonSubscriptionId())
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
            .resourceArn(model.getAddonSubscriptionArn())
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
            .resourceArn(model.getAddonSubscriptionArn())
            .tagKeys(removedTags)
            .build();
  }
}
