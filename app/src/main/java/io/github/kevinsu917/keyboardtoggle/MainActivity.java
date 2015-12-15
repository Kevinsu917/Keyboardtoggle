package io.github.kevinsu917.keyboardtoggle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag = this.getClass().getSimpleName();
    private String heightKey = "keyboard_height";
    TextView tvFace;
    EditText etInput;
    RelativeLayout rlContainer;
    SharedPreferences sharedPreferences;

    private int keyboardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(tag, Activity.MODE_PRIVATE);

        initView();
        afterView();
    }

    private void initView() {
        tvFace = (TextView) findViewById(R.id.tvFace);
        tvFace.setOnClickListener(this);
        etInput = (EditText) findViewById(R.id.etInput);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);

        getSharedPreferencesHeight();
        if(keyboardHeight > 0){
            setContainerHeight();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functiions.hideInputMethod(this, etInput);
    }

    void afterView() {
        etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rlContainer.getVisibility() == View.GONE) {
                    setInputMode(true);
                } else {
                    if (keyboardHeight == 0) {
                        setInputMode(true);
                        rlContainer.setVisibility(View.GONE);
                    } else {
                        setInputMode(false);
                        rlContainer.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        final SoftKeyboardStateHelper helper = new SoftKeyboardStateHelper(findViewById(R.id.llInputBar));
        helper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                Log.e(tag, "onSoftKeyboardOpened==" + keyboardHeightInPx);
                if (keyboardHeight == 0 && keyboardHeight != keyboardHeightInPx) {
                    keyboardHeight = keyboardHeightInPx;
                    setContainerHeight();
                    saveSharedPreferencesHeight();
                }
                if (rlContainer.getVisibility() == View.VISIBLE) {
                    rlContainer.setVisibility(View.INVISIBLE);
                    setInputMode(false);
                }
            }

            @Override
            public void onSoftKeyboardClosed() {
                Log.e(tag, "onSoftKeyboardClosed");
                if (rlContainer.getVisibility() == View.INVISIBLE) {
                    rlContainer.setVisibility(View.GONE);
                    setInputMode(true);
                }
            }
        });
    }

    void faceClick() {
        if (rlContainer.getVisibility() == View.VISIBLE) {
            if (keyboardHeight == 0) {
                setInputMode(true);
                rlContainer.setVisibility(View.GONE);
            } else {
                setInputMode(false);
                rlContainer.setVisibility(View.INVISIBLE);
            }
            //如果EditText没有获取焦点,需要获取焦点先,否则不会弹出键盘
            if(!etInput.hasFocus()){
                etInput.requestFocus();
            }
            Functiions.showInputMethodForQuery(MainActivity.this, etInput);
        } else {
            if (keyboardHeight == 0) {
                setInputMode(true);
            } else {
                setInputMode(false);
            }
            rlContainer.setVisibility(View.VISIBLE);
            Functiions.hideInputMethod(MainActivity.this, etInput);
        }
    }

    private void setInputMode(boolean resize) {
        if (resize) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(tvFace)){
            faceClick();
        }
    }

    /**
     * 设置Container的高度
     */
    private void setContainerHeight(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.llInputBar);
        rlContainer.setLayoutParams(layoutParams);
        rlContainer.setMinimumHeight(keyboardHeight);
    }

    /**
     * 保存键盘的高度
     */
    private void getSharedPreferencesHeight(){
        keyboardHeight = sharedPreferences.getInt(heightKey, 0);
    }


    /**
     * 保存键盘的高度
     */
    private void saveSharedPreferencesHeight(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(heightKey, keyboardHeight);
        editor.commit();
    }
}
