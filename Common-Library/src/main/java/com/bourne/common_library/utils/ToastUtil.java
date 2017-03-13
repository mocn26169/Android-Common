package com.bourne.common_library.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast != null)
            toast.cancel();

        if (context != null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void closeToast() {
        if (toast != null)
            toast.cancel();
    }

}
