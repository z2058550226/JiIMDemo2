package com.suikajy.jiimdemo2.module.viewTest;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.suikajy.jiimdemo2.R;
import com.suikajy.jiimdemo2.common.Global;
import com.suikajy.jiimdemo2.utils.KeybordS;
import com.suikajy.jiimdemo2.widget.ReSizeLayout;

/**
 * @author zjy
 * @date 2017/11/7
 */

public class ViewTestActivity extends AppCompatActivity implements ReSizeLayout.OnSizeChangeListener {

    private RecyclerView mRvData;
    private Button mBtnVoice;
    private EditText mEtInput;
    private Button mBtnSend;
    private Button mBtnPlus;
    private RelativeLayout mLayoutInputMenu;
    public View mDecoView;
    private ReSizeLayout mLayoutResize;
    /**
     * 该变量可以用来判断软键盘是否显示
     */
    private boolean isSoftKeyShowing = false;
    private boolean isVisibleForLast = false;
    //使用情景：软键盘开启，点击加号，让软键盘关闭，输入菜单显示
    private boolean isUsage1 = false;
    private int mOldSoftKeyBoardHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_test);
        initView();
        initListener();
    }

    private void initView() {
        mOldSoftKeyBoardHeight = Global.INSTANCE.getSoftKeyBoardHeight();
        mDecoView = getWindow().getDecorView();
        mRvData = findViewById(R.id.rv_data);
        mBtnVoice = findViewById(R.id.btn_voice);
        mEtInput = findViewById(R.id.etInput);
        mBtnSend = findViewById(R.id.btnSend);
        mBtnPlus = findViewById(R.id.btn_plus);
        mLayoutResize = findViewById(R.id.layout_resize);
        mLayoutInputMenu = findViewById(R.id.layout_input_menu);
        mLayoutResize.setOnSizeChangeListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        TestAdapter mAdapter = new TestAdapter();
        mRvData.setLayoutManager(manager);
        mRvData.setAdapter(mAdapter);
        resetInputMenuHeight();
    }

    private void initListener() {
        mBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoftKeyShowing) {
                    isUsage1 = true;
//                    KeybordS.closeKeybord(mEtInput, ViewTestActivity.this);
                    InputMethodManager imm = (InputMethodManager) ViewTestActivity.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mDecoView.getWindowToken(), 0);
                } else {
                    if (mLayoutInputMenu.getVisibility() == View.VISIBLE) {
                        closeInputMenu();
                        closeInputMenu();
                    } else {
                        showInputMenu();
                        showInputMenu();
                    }
                }
            }
        });
        mRvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                closeInputMenu();
//                closeSoft();
            }
        });
        mDecoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                mDecoView.getWindowVisibleDisplayFrame(rect);
                //计算出可见屏幕的高度
                int displayHight = rect.bottom - rect.top;
                //获得屏幕整体的高度
//                int hight = mDecoView.getHeight();
                int hight = getScreenHeight(ViewTestActivity.this);
                //获得键盘高度
                int keyboardHeight = hight - displayHight - Global.INSTANCE.getStatusBarHeight();
                boolean visible = (double) displayHight / hight < 0.8;
                if (visible != isVisibleForLast) {
                    isSoftKeyShowing = visible;
                    onSoftKeyBoardVisible(visible, keyboardHeight);
                }
                isVisibleForLast = visible;
            }
        });
    }

    private void onSoftKeyBoardVisible(boolean visible, int keyboardHeight) {
        if (visible && mOldSoftKeyBoardHeight != keyboardHeight) {
            Global.INSTANCE.setSoftKeyBoardHeight(keyboardHeight);
            mOldSoftKeyBoardHeight = keyboardHeight;
            resetInputMenuHeight();
        }
        if (!visible) {
            afterSoftClosed();
        }
    }

    private void afterSoftClosed() {
        if (isUsage1) {
            showInputMenu();
            isUsage1 = false;
        }
    }

    private void resetInputMenuHeight() {
        Log.e("**** TAG ****", mOldSoftKeyBoardHeight + "");
        ViewGroup.LayoutParams layoutParams = mLayoutInputMenu.getLayoutParams();
        layoutParams.height = mOldSoftKeyBoardHeight;
        mLayoutInputMenu.setLayoutParams(layoutParams);
    }

    private void closeSoft() {
        KeybordS.closeKeybord(mEtInput, this);
    }

    private void closeInputMenu() {
        mLayoutInputMenu.setVisibility(View.GONE);
    }

    private void showInputMenu() {
        mLayoutInputMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mLayoutInputMenu.getVisibility() == View.VISIBLE) {
            mLayoutInputMenu.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {

    }
}
