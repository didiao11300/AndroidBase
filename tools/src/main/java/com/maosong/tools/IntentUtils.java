package com.maosong.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

/**
 * create by colin on 2019-06-19
 */
public class IntentUtils {
    public static boolean startActivityByUrl(Context context, String url) {
        if (null == context || TextUtils.isEmpty(url)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isValid = !activities.isEmpty();
        if (isValid) {
            context.startActivity(intent);
        }
        return isValid;
    }
}
