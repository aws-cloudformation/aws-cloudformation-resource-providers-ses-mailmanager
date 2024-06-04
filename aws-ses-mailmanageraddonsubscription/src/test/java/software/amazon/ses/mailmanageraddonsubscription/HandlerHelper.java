package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.services.mailmanager.model.CreateAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.ListAddonSubscriptionsResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.AddonSubscription;

public class HandlerHelper {
    public static final String AddonSubscription_ID = "addon_subscription_id";
    public static final String AddonSubscription_NAME = "SPAMHAUS_DBL";
    public static final String AddonSubscription_ARN = "addon_subscription_arn";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";

    static GetAddonSubscriptionResponse fakeGetAddonSubscriptionResponse() {
        return GetAddonSubscriptionResponse.builder()
                .addonName(AddonSubscription_NAME)
                .addonSubscriptionArn(AddonSubscription_ARN)
                .build();
    }

    static CreateAddonSubscriptionResponse fakeCreateAddonSubscriptionResponse() {
        return CreateAddonSubscriptionResponse.builder()
                .addonSubscriptionId(AddonSubscription_ID)
                .build();
    }

    static DeleteAddonSubscriptionResponse fakeDeleteAddonSubscriptionResponse() {
        return DeleteAddonSubscriptionResponse.builder().build();
    }

    static ListAddonSubscriptionsResponse fakeListAddonSubscriptionsResponse() {
        return ListAddonSubscriptionsResponse.builder().addonSubscriptions(
                AddonSubscription.builder().addonSubscriptionId("id_one").build(),
                AddonSubscription.builder().addonSubscriptionId("id_two").build()
        ).build();
    }

    static ListTagsForResourceResponse fakeListTagsForResourceResponse() {
        return ListTagsForResourceResponse.builder()
                .tags(
                        software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                                .key("keyOne")
                                .value("valueOne")
                                .build(),
                        software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                                .key("keyTwo")
                                .value("valueTwo")
                                .build()
                )
                .build();
    }
}
