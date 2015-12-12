package io.github.kevinsu917.keyboardtoggle;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Creator: KevinSu kevinsu917@126.com
 * Date 2015-12-12-10:38
 * Description: 用于监测键盘开关
 */
public class SoftKeyboardStateHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private boolean isSoftKeyboardOpened;
    private boolean isFistEnter = true;
    private int screenHeight = 0;//记录屏幕的高度
    private int keyboardHeight = 0;//记录键盘的高度

    public SoftKeyboardStateHelper(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardStateHelper(View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        detectHeight(r.height());
    }

    /**
     * 监测当前屏幕的高度
     * isFirstEnter和isSoftKeyboardOpened来保证每次onGlobalLayout被调用的时候,只调用一次notifyOnSoftKeyboardOpened或者notifyOnSoftKeyboardClosed
     *
     * @param currentHeight
     */
    private void detectHeight(int currentHeight) {
        screenHeight = Math.max(screenHeight, currentHeight);
        int offset = screenHeight - currentHeight;
        if (offset > 0) {
            if (isFistEnter || !isSoftKeyboardOpened()) {
                setIsSoftKeyboardOpened(true);
                notifyOnSoftKeyboardOpened(offset);
                isFistEnter = false;
            }
        } else {
            if (isFistEnter || isSoftKeyboardOpened()) {
                setIsSoftKeyboardOpened(false);
                notifyOnSoftKeyboardClosed();
                isFistEnter = false;
            }
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }


    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        void onSoftKeyboardClosed();
    }
}