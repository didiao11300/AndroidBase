package com.maosong.tools;

import android.content.Context;
import android.net.Uri;

/**
 * create by colin on 2019/3/15
 */
public class ResourceUtil {
    public static String resourceIdToString(Context context, int resourceId) {
        return ("android.resource://" + context.getPackageName() + "/" + resourceId);
    }

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(resourceIdToString(context, resourceId));
    }

}
