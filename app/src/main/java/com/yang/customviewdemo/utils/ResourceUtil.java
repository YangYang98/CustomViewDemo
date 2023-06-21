package com.yang.customviewdemo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Create by Yang Yang on 2023/6/20
 */
public class ResourceUtil {

    /**
     * @param context 必须是包含 Theme 的 Context
     */
    public static int getDrawableResId(Context context, @AttrRes int attrId) {
        TypedValue typedValue = getTypedValue(context, attrId);
        return typedValue.resourceId;
    }

    @NonNull
    public static TypedValue getTypedValue(Context context, @AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue;
    }

    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        if (resId == 0) {
            return null;
        }
        return ContextCompat.getDrawable(context, resId);
    }
}
