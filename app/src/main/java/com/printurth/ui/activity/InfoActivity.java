package com.printurth.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.printurth.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InfoActivity extends BaseActivity {
Intent intent;
    final static String noticeFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog/logfile.txt";
    final static String versionFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog/logfile.txt";
    InputStream inputStream;
    @BindView(R.id.tv)
    TextView tv;

    @BindView(R.id.back_btn)
    Button back_btn;

    @OnClick(R.id.back_btn)
    public void back_btnClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back_btn.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        back_btn.setVisibility(View.VISIBLE);

        intent = getIntent();
        tv.setText(readTxt(intent.getStringExtra("type")));
    }


    private String readTxt(String type) {
        //Log.e("~!", type);
        String data = null;
        if(type.equals("notice")) {
             inputStream = getResources().openRawResource(R.raw.notice);
        } else if(type.equals("version")){
             inputStream = getResources().openRawResource(R.raw.version);
        } else {
             inputStream = getResources().openRawResource(R.raw.notice);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = new String(byteArrayOutputStream.toByteArray(),"UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
