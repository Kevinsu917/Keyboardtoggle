package io.github.kevinsu917.keyboardtoggle;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Creator: KevinSu kevinsu917@126.com
 * Date 2015-12-12-10:41
 * Description:
 */
public class Functiions {

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideInputMethod(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showInputMethodForQuery(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
}
