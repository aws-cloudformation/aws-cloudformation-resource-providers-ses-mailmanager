package software.amazon.ses.mailmanageraddoninstance;

import software.amazon.awssdk.services.mailmanager.model.AddonInstance;
import software.amazon.awssdk.services.mailmanager.model.CreateAddonInstanceResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonInstanceResponse;
import software.amazon.awssdk.services.mailmanager.model.GetAddonInstanceResponse;
import software.amazon.awssdk.services.mailmanager.model.ListAddonInstancesResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;

public class HandlerHelper {
    public static final String AddonSubscription_ID = "addon_Subscription_id";
    public static final String AddonInstance_ID = "addon_Instance_id";
    public static final String AddonInstance_NAME = "SPAMHAUS_DBL";
    public static final String AddonInstance_ARN = "addon_Instance_arn";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";

    static GetAddonInstanceResponse fakeGetAddonInstanceResponse() {
        return GetAddonInstanceResponse.builder()
                .addonName(AddonInstance_NAME)
                .addonSubscriptionId(AddonSubscription_ID)
                .addonInstanceArn(AddonInstance_ARN)
                .build();
    }

    static CreateAddonInstanceResponse fakeCreateAddonInstanceResponse() {
        return CreateAddonInstanceResponse.builder()
                .addonInstanceId(AddonInstance_ID)
                .build();
    }

    static DeleteAddonInstanceResponse fakeDeleteAddonInstanceResponse() {
        return DeleteAddonInstanceResponse.builder().build();
    }

    static ListAddonInstancesResponse fakeListAddonInstancesResponse() {
        return ListAddonInstancesResponse.builder().addonInstances(
                AddonInstance.builder().addonInstanceId("id_one").build(),
                AddonInstance.builder().addonInstanceId("id_two").build()
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
