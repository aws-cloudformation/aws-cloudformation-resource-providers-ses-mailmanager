package software.amazon.ses.mailmanagerarchive.utils;

import software.amazon.ses.mailmanagerarchive.Tag;

import java.util.List;

public class TagsConvertor {
    public static List<Tag> convertFromSdk(List<software.amazon.awssdk.services.mailmanager.model.Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .filter(tag -> tag.value() != null)
                .map(tag -> new Tag(tag.key(), tag.value()))
                .toList();

    }

    public static List<software.amazon.awssdk.services.mailmanager.model.Tag> convertToSdk(List<Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .filter(tag -> tag.getValue() != null)
                .map(tag -> software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .toList();
    }
}
