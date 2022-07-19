package com.printurth.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.printurth.R;
import com.printurth.STLViewActivity;
import com.printurth.database.DbOpenHelper;
import com.printurth.model.RollingBannerModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PRDetailActivity extends BaseActivity {
    private List<RollingBannerModel> list;
    Intent intent;
    String data_id;
    String data_thumbnail;
    String data_title;
    String data_makername;
    String data_uploadat;
    String data_like;
    String data_level;
    String data_time;
    String data_filament;
    String data_filename;
    String data_description;

    @BindView(R.id.prdetail_thumbnail)
    ImageView iv_prdetail_thumbnail;
    @BindView(R.id.prdetail_like)
    ImageView iv_prdetail_like;
    @BindView(R.id.prdetail_title)
    TextView tv_prdetail_title;
    @BindView(R.id.prdetail_makername)
    TextView tv_prdetail_makername;
    @BindView(R.id.prdetail_uploadat)
    TextView tv_prdetail_uploadat;
    @BindView(R.id.prdetail_level)
    TextView tv_prdetail_level;
    @BindView(R.id.prdetail_time)
    TextView tv_prdetail_time;
    @BindView(R.id.prdetail_filament)
    TextView tv_prdetail_filament;
    @BindView(R.id.prdetail_description)
    TextView tv_prdetail_description;
    private DbOpenHelper mDbOpenHelper;
    String itemid = "";
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
        setContentView(R.layout.activity_prdetail);
        ButterKnife.bind(this);
        back_btn.setVisibility(View.VISIBLE);

        intent = getIntent();
        data_id = intent.getStringExtra("data_id");
        data_thumbnail = intent.getStringExtra("data_thumbnail");
        data_title = intent.getStringExtra("data_title");
        data_makername =  intent.getStringExtra("data_makername");
        data_uploadat = intent.getStringExtra("data_uploadat");
        data_like = intent.getStringExtra("data_like");
        data_level =  intent.getStringExtra("data_level");
        data_time = intent.getStringExtra("data_time");
        data_filament =  intent.getStringExtra("data_filament");
        //data_filename =  intent.getStringExtra("data_filename");
        data_description =  intent.getStringExtra("data_description");


        if(session.isSetDB()) {
            mDbOpenHelper = new DbOpenHelper(this);
            mDbOpenHelper.open();
            mDbOpenHelper.create();


            //  rollingBannerModelList = new ArrayList<>();
            // list = new ArrayList<>();
            Cursor iCursor = mDbOpenHelper.selectColumns();
            while (iCursor.moveToNext()) {
                String id = iCursor.getString(iCursor.getColumnIndex("id"));

                String fileName = iCursor.getString(iCursor.getColumnIndex("fileName"));
                String title = iCursor.getString(iCursor.getColumnIndex("title"));
                String thumbImgName = iCursor.getString(iCursor.getColumnIndex("thumbImgName"));
                String makerName = iCursor.getString(iCursor.getColumnIndex("makerName"));
                String uploadAt = iCursor.getString(iCursor.getColumnIndex("uploadAt"));
                String level = iCursor.getString(iCursor.getColumnIndex("level"));
                String time = iCursor.getString(iCursor.getColumnIndex("time"));
                String filament = iCursor.getString(iCursor.getColumnIndex("filament"));
                String description = iCursor.getString(iCursor.getColumnIndex("description"));
                int like = iCursor.getInt(iCursor.getColumnIndex("like"));
            //    String lastDate = iCursor.getString(iCursor.getColumnIndex("lastdate"));
                if(id.equals(data_id)) {
                    itemid = id;
                    tv_prdetail_title.setText(title);
                    int thumbnailid = getRawResIdByName(thumbImgName);
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + thumbnailid);
                    Glide.with(this).load(uri).into(iv_prdetail_thumbnail);

                    tv_prdetail_makername.setText(makerName);
                    tv_prdetail_uploadat.setText(uploadAt);
                    tv_prdetail_level.setText(level);
                    tv_prdetail_time.setText(time);
                    tv_prdetail_filament.setText(filament);
                    tv_prdetail_description.setText(description);
                    data_filename = fileName;
                    if(like == 0) {
                        Glide.with(this).load(R.drawable.icn_unlike).into(iv_prdetail_like);
                    } else {
                        Glide.with(this).load(R.drawable.icn_like).into(iv_prdetail_like);
                    }
                }

//                RollingBannerModel rollingBannerModel = new RollingBannerModel(id,fileName,title,thumbImgName,makerName,uploadAt,level,time,filament,description);
//
//                rollingBannerModelList.add(rollingBannerModel);
            }

        }

    }

    @OnClick(R.id.prdetail_drawbtn)
    public void prdetail_drawbtnClicked() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getDate = sdf.format(date);
        //long time = Long.parseLong(getTime);
      //  Log.e("~!", getDate);
        int id = getRawResIdByName(data_filename);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + id);
        Intent intent = new Intent(this, STLViewActivity.class);
        intent.putExtra("uri", uri.toString());
        updateDBLastdate(itemid ,getDate);
        startActivity(intent);
        // checkReadPermissionThenOpen();

    }
    // getRawResIdByName
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);

        return resID;
    }

    public void updateDBLastdate(String id, String lastdate) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        mDbOpenHelper.updateColumnLastdate(id, lastdate);



    }
}
