package software.amazon.ses.mailmanageraddoninstance;

import software.amazon.awssdk.services.mailmanager.model.CreateAddonInstanceRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonInstanceRequest;
import software.amazon.awssdk.services.mailmanager.model.GetAddonInstanceRequest;
import software.amazon.awssdk.services.mailmanager.model.GetAddonInstanceResponse;
import software.amazon.awssdk.services.mailmanager.model.ListAddonInstancesRequest;
import software.amazon.awssdk.services.mailmanager.model.ListAddonInstancesResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.Tag;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanageraddoninstance.TagHelper.convertToSet;
import static software.amazon.ses.mailmanageraddoninstance.TagHelper.getNewDesiredTags;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a resource
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static CreateAddonInstanceRequest translateToCreateRequest(final ResourceModel model, final ResourceHandlerRequest<ResourceModel> request) {
    Set<Tag> tagsToBeAdded = convertToSet(getNewDesiredTags(request));

    return CreateAddonInstanceRequest.builder()
            .addonSubscriptionId(model.getAddonSubscriptionId())
            .tags(tagsToBeAdded)
            .build();
  }

  /**
   * Request to read a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetAddonInstanceRequest translateToReadRequest(final ResourceModel model) {
    return GetAddonInstanceRequest.builder()
            .addonInstanceId(model.getAddonInstanceId())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   *
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetAddonInstanceResponse response, String resourceId) {
    return ResourceModel.builder()
            .addonInstanceId(resourceId)
            .addonName(response.addonName())
            .addonInstanceArn(response.addonInstanceArn())
            .addonSubscriptionId(response.addonSubscriptionId())
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteAddonInstanceRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteAddonInstanceRequest.builder()
            .addonInstanceId(model.getAddonInstanceId())
            .build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListAddonInstancesRequest translateToListRequest(final String nextToken) {
    return ListAddonInstancesRequest.builder()
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
            .resourceArn(model.getAddonInstanceArn())
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   *
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListAddonInstancesResponse response) {
    return streamOfOrEmpty(response.addonInstances())
            .map(resource -> ResourceModel.builder()
                    // include only primary identifier
                    .addonInstanceId(resource.addonInstanceId())
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
            .resourceArn(model.getAddonInstanceArn())
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
            .resourceArn(model.getAddonInstanceArn())
            .tagKeys(removedTags)
            .build();
  }
}
