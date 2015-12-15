# Keyboardtoggle 关于输入框中,键盘和表情栏之间切换的问题(类似微信)
keyboard and face bar toggle logic

因为表情栏和键盘的高度不同的缘故,导致在键盘和表情栏切换的过程中,出现不流畅的效果.但是微信把表情栏的高度,做得和键盘高度一致,就解决了这一问题.

问题1:如何获取键盘的高度.  
android没有提供这样的api,所以只能通过layout的高度的变化,计算出键盘的高度.

通过[参考](http://blog.csdn.net/xcookies/article/details/43024911)这篇文章,使用作者的SoftKeyboardStateHelper类,可以监听键盘的打开和关闭事件.因为onGlobalLayout这个方法在打开键盘的时候会被调用多次,所以影响我想要只有在打开和关闭切换的时候调用接口.

原代码:  

```
@Override  
public void onGlobalLayout() {  
        final Rect r = new Rect();  
        //r will be populated with the coordinates of your view that area still visible.  
        activityRootView.getWindowVisibleDisplayFrame(r);  
        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);  
        if (!isSoftKeyboardOpened && heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...  
            isSoftKeyboardOpened = true;  
            notifyOnSoftKeyboardOpened(heightDiff);  
        } else if (isSoftKeyboardOpened && heightDiff < 100) {  
            isSoftKeyboardOpened = false;  
            notifyOnSoftKeyboardClosed();  
        }  
    }  
```

修改后的代码:

```

@Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        detectHeight(r.height());
    }
    
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
    
```

另外一个问题,键盘的弹出方式:  

根据[参考](http://segmentfault.com/q/1010000003046282)该问题中,**thyee用户的回答**.

需求需要时刻保持输入的EditText处于页面的最底部,那么我们一般将inputMode设置为SOFT_INPUT_ADJUST_RESIZE,但是如果当表情栏也现实的情况下,如果让inputMode还处于SOFT_INPUT_ADJUST_RESIZE的模式下,就会把表情栏也顶上去,所以这个时候应该切换成SOFT_INPUT_ADJUST_PAN,所以就需要判断当表情栏的情况下,切换两种模式.

[具体我github上的工程](https://github.com/Kevinsu917/Keyboardtoggle)
