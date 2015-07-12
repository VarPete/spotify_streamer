package com.dataunavailable.spotifystreamer.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Panayiotis on 7/10/2015.
 */
public class ToastUtil {

    private static final ToastUtil instance = new ToastUtil();

    private static Toast lastToast;

    /**
     * ToastUtil provides static functions that manages clearing stale toast before creating new, fresh toast
     */
    private ToastUtil() {
    }

    /**
     * Same as calling ToastUtil.showToast(Context context, String message, int length) with Toast.LENGTH_LONG
     *
     */
    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    /**
     * Cancel any previous Toast message prior to displaying a new one
     *
     * @param context context that this toast applies to
     * @param message what information to show the users
     * @param length    One of Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     */
    public static void showToast(Context context, String message, int length) {
        synchronized (instance) {
            if (lastToast != null) {
                lastToast.cancel();
            }

            lastToast = Toast.makeText(context, message, length);
            lastToast.show();
        }
    }
}
