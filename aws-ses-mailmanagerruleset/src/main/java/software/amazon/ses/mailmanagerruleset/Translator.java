package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.model.CreateRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetResponse;
import software.amazon.awssdk.services.mailmanager.model.ListRuleSetsRequest;
import software.amazon.awssdk.services.mailmanager.model.ListRuleSetsResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetRequest;
import software.amazon.ses.mailmanagerruleset.utils.RuleConvertorFromSdk;
import software.amazon.ses.mailmanagerruleset.utils.RuleConvertorToSdk;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.ses.mailmanagerruleset.TagHelper.convertToSet;
import static software.amazon.ses.mailmanagerruleset.utils.TagsConvertor.convertToSdk;

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
  static CreateRuleSetRequest translateToCreateRequest(final ResourceModel model) {
    return CreateRuleSetRequest.builder()
            .ruleSetName(model.getRuleSetName())
            .rules(RuleConvertorToSdk.ConvertToSdk(model.getRules()))
            .tags(convertToSdk(model.getTags()))
            .build();
  }

  /**
   * Request to read a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetRuleSetRequest translateToReadRequest(final ResourceModel model) {
    return GetRuleSetRequest.builder()
            .ruleSetId(model.getRuleSetId())
            .build();
  }

  /**
   * Translates the resource object from sdk into a resource model
   *
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetRuleSetResponse response) {
    return ResourceModel.builder()
            .ruleSetId(response.ruleSetId())
            .ruleSetName(response.ruleSetName())
            .ruleSetArn(response.ruleSetArn())
            .rules(RuleConvertorFromSdk.ConvertFromSdk(response.rules()))
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteRuleSetRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteRuleSetRequest.builder()
            .ruleSetId(model.getRuleSetId())
            .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static UpdateRuleSetRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateRuleSetRequest.builder()
            .ruleSetId(model.getRuleSetId())
            .ruleSetName(model.getRuleSetName())
            .rules(RuleConvertorToSdk.ConvertToSdk(model.getRules()))
            .build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListRuleSetsRequest translateToListRequest(final String nextToken) {
    return ListRuleSetsRequest.builder()
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
            .resourceArn(model.getRuleSetArn())
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   *
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListRuleSetsResponse response) {
    return streamOfOrEmpty(response.ruleSets())
            .map(resource -> ResourceModel.builder()
                    // include only primary identifier
                    .ruleSetId(resource.ruleSetId())
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
            .resourceArn(model.getRuleSetArn())
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
            .resourceArn(model.getRuleSetArn())
            .tagKeys(removedTags)
            .build();
  }
}
