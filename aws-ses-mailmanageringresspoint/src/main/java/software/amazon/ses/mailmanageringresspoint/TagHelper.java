package software.amazon.ses.mailmanageringresspoint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.services.mailmanager.model.Tag;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TagHelper {
    /**
     * convertToMap
     *
     * Converts a collection of Tag objects to a tag-name -> tag-value map.
     *
     * Note: Tag objects with null tag values will not be included in the output map.
     *
     * @param tags Collection of tags to convert
     * @return Converted Map of tags
     */
    public static Map<String, String> convertToMapFromResource(final Collection<software.amazon.ses.mailmanageringresspoint.Tag> tags){
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyMap();
        }
        return tags.stream()
                .filter(tag -> tag.getValue() != null)
                .collect(Collectors.toMap(
                        software.amazon.ses.mailmanageringresspoint.Tag::getKey,
                        software.amazon.ses.mailmanageringresspoint.Tag::getValue,
                        (oldValue, newValue) -> newValue));
    }

    /**
     * convertToSet
     *
     * Converts a tag map to a set of Tag objects.
     *
     * Note: Like convertToMap, convertToSet filters out value-less tag entries.
     *
     * @param tagMap Map of tags to convert
     * @return Set of Tag objects
     */
    public static Set<Tag> convertToSet(final Map<String, String> tagMap) {
        if (MapUtils.isEmpty(tagMap)) {
            return Collections.emptySet();
        }
        return tagMap.entrySet().stream()
                .filter(tag -> tag.getValue() != null)
                .map(tag -> Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * shouldUpdateTags
     *
     * Determines whether user defined tags have been changed during update.
     */
    public static boolean shouldUpdateTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        final Map<String, String> desiredTags = getNewDesiredTags(handlerRequest);
        return ObjectUtils.notEqual(previousTags, desiredTags);
    }

    /**
     * getPreviouslyAttachedTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get previously attached system (with `aws:cloudformation` prefix) and user defined tags from
     * handlerRequest.getPreviousSystemTags() (system tags),
     * handlerRequest.getPreviousResourceTags() (stack tags),
     * handlerRequest.getPreviousResourceState().getTags() (resource tags).
     *
     * System tags are an optional feature. Merge them to your tags if you have enabled them for your resource.
     * System tags can change on resource update if the resource is imported to the stack.
     */
    public static Map<String, String> getPreviouslyAttachedTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = new HashMap<>();

        if (handlerRequest.getPreviousSystemTags() != null) {
            previousTags.putAll(handlerRequest.getPreviousSystemTags());
        }

        // get previous stack level tags from handlerRequest
        if (handlerRequest.getPreviousResourceTags() != null) {
            previousTags.putAll(handlerRequest.getPreviousResourceTags());
        }

        if (handlerRequest.getPreviousResourceState() != null && handlerRequest.getPreviousResourceState().getTags() != null) {
            previousTags.putAll(convertToMapFromResource(handlerRequest.getPreviousResourceState().getTags()));
        }

        return previousTags;
    }

    /**
     * getNewDesiredTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get new desired system (with `aws:cloudformation` prefix) and user defined tags from
     * handlerRequest.getSystemTags() (system tags),
     * handlerRequest.getDesiredResourceTags() (stack tags),
     * handlerRequest.getDesiredResourceState().getTags() (resource tags).
     *
     * System tags are an optional feature. Merge them to your tags if you have enabled them for your resource.
     * System tags can change on resource update if the resource is imported to the stack.
     */
    public static Map<String, String> getNewDesiredTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> desiredTags = new HashMap<>();

        if (handlerRequest.getSystemTags() != null) {
            desiredTags.putAll(handlerRequest.getSystemTags());
        }

        // get desired stack level tags from handlerRequest
        if (handlerRequest.getDesiredResourceTags() != null) {
            desiredTags.putAll(handlerRequest.getDesiredResourceTags());
        }

        if (handlerRequest.getPreviousResourceState() != null && handlerRequest.getDesiredResourceState().getTags() != null) {
            desiredTags.putAll(convertToMapFromResource(handlerRequest.getDesiredResourceState().getTags()));
        }

        return desiredTags;
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public static Map<String, String> generateTagsToAdd(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        return desiredTags.entrySet().stream()
                .filter(e -> !previousTags.containsKey(e.getKey()) || !Objects.equals(previousTags.get(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    /**
     * getTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public static Set<String> generateTagsToRemove(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        final Set<String> desiredTagNames = desiredTags.keySet();

        return previousTags.keySet().stream()
                .filter(tagName -> !desiredTagNames.contains(tagName))
                .collect(Collectors.toSet());
    }
}
