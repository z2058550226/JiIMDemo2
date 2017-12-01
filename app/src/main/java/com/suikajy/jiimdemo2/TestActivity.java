package com.suikajy.jiimdemo2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * @author zjy
 * @date 2017/11/30
 */

public class TestActivity extends AppCompatActivity {

    private TextView mTvTestMsg;
    private Button mBtnCreateFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mTvTestMsg = findViewById(R.id.tv_test_msg);
        mBtnCreateFile = findViewById(R.id.btn_create_file);

        mBtnCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("aaa.txt");
                try {
                    boolean newFile = file.createNewFile();
                    if (newFile) {
                        mTvTestMsg.setText("true");
                    } else {
                        mTvTestMsg.setText("false");
                    }
                } catch (IOException e) {
                    mTvTestMsg.setText("error");
                }
            }
        });
    }
}
