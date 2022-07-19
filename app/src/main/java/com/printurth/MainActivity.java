package com.printurth;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.printurth.adapter.TabFragmentPagerAdapter;
import com.printurth.database.DbOpenHelper;
import com.printurth.model.RollingBannerModel;
import com.printurth.ui.fragment.Menu2Fragment;
import com.printurth.ui.fragment.Menu3Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    Context context;

    SessionManager session;


    private static final int READ_PERMISSION_REQUEST = 100;
    private static final int OPEN_DOCUMENT_REQUEST = 101;
//    @BindView(R.id.bottomNavigation)
//    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tv_title)
    TextView ToolbarTitle;

    @BindView(R.id.bottom_tablayout)
    TabLayout bottom_tablayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.back_btn)
    Button back_btn;

    private DbOpenHelper mDbOpenHelper;

    List<RollingBannerModel> rollingBannerModelList;
   // String jsonStr = "[{'id':'1','fileName':eiffel,'title':'에펠탑','thumbImgName':'rolling_eiffel','makerName':'Newcandle','uploadAt':'2019-05-17','level':'2','time':'1시간 20분','filament':'75','description':'4면이 같은 방사형 구조로 미적인 부분 뿐만 아니라 오브젝트의 내구도가 매우 우수합니다. 구조물의 세밀한 디테일도 중요하지만, 4면의 전체 틀의 규격이 잘 맞도록 하는것이 가장 중요합니다. 반복되는 구조물이 다소 지루하게 느껴질수도 있습니다. 일정한 빠르기로 고르게 fill out 해주면 더욱 정교하게 만들수 있습니다.'},"+
   //         "{'id':'2','fileName':whale,'title':'고래','thumbImgName':'rolling_whale','makerName':'ohhmyhead','uploadAt':'2019-05-17','level':'1','time':'30분','filament':'30','description':'측면이 대칭이고, 측면과 바닥면 제작 후 윗면은 입체상태에서 제작하여야 합니다. 이때 유려한 곡선에 유의하여야 하며, 측면의 규격에 따라 내구도가 좌우됩니다. 시간도 길지않고, 실력 쌓기에 좋은 도안 입니다.'},"+
    //        "{'id':'3','fileName':dragon,'title':'아기드래곤','thumbImgName':'rolling_dragon','makerName':'Sebastian','uploadAt':'2019-05-17','level':'3','time':'2시간 30분','filament':'50','description':'쓰이는 필라멘트양에 비해 제작시간이 매우 오래걸리는 도안입니다. 가장 기본적인 뼈대만 바닥면으로 해서 뽑고 나머지는 모두 입체로 작업을 해야합니다. 곡선부가 매우 많고, 살려줘야 할 디테일이 많아 난이도가 매우 높습니다. 필라멘트가 굳기전에 장갑을 낀 손으로 조금씩 터칭해가며 만들어야 더욱 디테일하게 표현할 수 있습니다.'}]";


    public void insertDB(String id, String fileName, String title , String thumbImgName ,
                         String makerName , String uploadAt , String level , String time ,
                         String filament , String description, int isfin, int like, String lastdate) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();


        mDbOpenHelper.insertColumn(id,fileName,title,thumbImgName,makerName,uploadAt,level,time,filament,description,0, 0, lastdate);
//        while(iCursor.moveToNext()){
//
//
//
//            String id = iCursor.getString(iCursor.getColumnIndex("id"));
//            String fileName = iCursor.getString(iCursor.getColumnIndex("fileName"));
//            String title = iCursor.getString(iCursor.getColumnIndex("title"));
//            String thumbImgName = iCursor.getString(iCursor.getColumnIndex("thumbImgName"));
//            String makerName = iCursor.getString(iCursor.getColumnIndex("makerName"));
//            String uploadAt = iCursor.getString(iCursor.getColumnIndex("uploadAt"));
//            String level = iCursor.getString(iCursor.getColumnIndex("level"));
//            String time = iCursor.getString(iCursor.getColumnIndex("time"));
//            String filament = iCursor.getString(iCursor.getColumnIndex("filament"));
//            String description = iCursor.getString(iCursor.getColumnIndex("description"));
//
////            if(tempName.equals("John"){
////                String Result = tempID + "," +tempName + "," + tempAge + "," + tempGender;
////            }
//        }
    }


    // 내부저장소 디렉토리생성
    public void getDir(Context context, String name) {
        File dir = new File(context.getFilesDir(), name);

        if(!dir.exists())

            dir.mkdirs();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        session = new SessionManager(this);

        back_btn.setVisibility(View.INVISIBLE);
        ToolbarTitle.setText("메인");
        if(!session.isSetDB()) {
            mDbOpenHelper = new DbOpenHelper(this);
            mDbOpenHelper.open();
            mDbOpenHelper.create();
            rollingBannerModelList = new ArrayList<>();

            AssetManager manager = getResources().getAssets();



            try {
                AssetManager.AssetInputStream ais = (AssetManager.AssetInputStream) manager.open("example.json");

                BufferedReader br = new BufferedReader(new InputStreamReader(ais));



                StringBuilder sb = new StringBuilder();



                //json파일의 내용이 용량이 클경우 Stirng 의 허용점인 4096 byte 를 넘어가면 오류발생

                int bufferSize = 1024 * 1024;

                //char 로 버프 싸이즈 만큼 담기위해 선언

                char readBuf[] = new char[bufferSize];

                int resultSize = 0;
                //파일의 전체 내용 읽어오기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String getDate = sdf.format(date);
                //long time = Long.parseLong(getTime);
                //Log.e("~!", getDate);
                while ((resultSize = br.read(readBuf)) != -1)

                {

                    if (resultSize == bufferSize)

                    {

                        sb.append(readBuf);

                    }

                    else

                    {

                        for (int i = 0; i < resultSize; i++)

                        {

                            sb.append(readBuf[i]);

                        }

                    }

                }

                String jString = sb.toString();

                JSONObject obj = new JSONObject(jString);
                JSONArray jarray = new JSONArray();

                if (obj.has("list"))
                    jarray = obj.getJSONArray("list");


                //JSONArray jarray = new JSONArray(jsonStr);   // JSONArray 생성

                for(int i=0; i < jarray.length(); i++){

                    JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                    String id = jObject.getString("id");
                    String fileName = jObject.getString("fileName");
                    String title = jObject.getString("title");
                    String thumbImgName = jObject.getString("thumbImgName");
                    String makerName = jObject.getString("makerName");
                    String uploadAt = jObject.getString("uploadAt");
                    String level = jObject.getString("level");
                    String time = jObject.getString("time");
                    String filament = jObject.getString("filament");
                    String description = jObject.getString("description");
                //    int isfin = jObject.getInt("isfin");
                 //   int like = jObject.getInt("like");

                    //Log.e("~!", thumbImgName);
                    RollingBannerModel rollingBannerModel = new RollingBannerModel(id,fileName,title,thumbImgName,makerName,uploadAt,level,time,filament,description,0,0,"-");

                    rollingBannerModelList.add(rollingBannerModel);
                    insertDB(id,fileName,title,thumbImgName,makerName,uploadAt,level,time,filament,description, 0, 0, "-");

                }
                session.setDB();


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


//        for(int i=0; i<rollingBannerModelList.size(); i++) {
//            Log.e("~!", rollingBannerModelList.get(i).getThumbImgName());
//
//        }

        getDir(this, "PrintUrth");

        TabFragmentPagerAdapter pagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bottom_tablayout));

        bottom_tablayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewpager) {
                                                      @Override
                                                      public void onTabSelected(TabLayout.Tab tab) {
                                                          super.onTabSelected(tab);

                                                          switch (tab.getPosition()) {

                                                              case 0:
                                                                  back_btn.setVisibility(View.INVISIBLE);
                                                                  ToolbarTitle.setText("메인");
                                                                  break;
                                                              case 1:
                                                                  back_btn.setVisibility(View.INVISIBLE);
                                                                  Menu2Fragment.getInstance().setRefreshAdapter();
                                                                  ToolbarTitle.setText("리스트");
                                                                  break;
                                                              case 2:
                                                                  back_btn.setVisibility(View.INVISIBLE);
                                                                  Menu3Fragment.getInstance().setRefreshAdapter();
                                                                  ToolbarTitle.setText("My 리스트");
                                                                  break;
                                                              case 3:
                                                                  back_btn.setVisibility(View.INVISIBLE);
                                                                  ToolbarTitle.setText("설정");
                                                                  break;
                                                          }
                                                      }
                                                  });

//        bottom_tablayout.addTab(bottom_tablayout.newTab().setText("메인"));
//        bottom_tablayout.addTab(bottom_tablayout.newTab().setText("메인"));
//        bottom_tablayout.addTab(bottom_tablayout.newTab().setText("메인"));
//        bottom_tablayout.addTab(bottom_tablayout.newTab().setText("메인"));
//        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
//        // 첫 화면 지정
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_body, menu1Fragment).commit();
//        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                switch (item.getItemId()) {
//                    case R.id.navigation_menu1: {
//                        ToolbarTitle.setText("메인");
//                        transaction.replace(R.id.fragment_body, menu1Fragment).commitAllowingStateLoss();
//                        break;
//                    }
//                    case R.id.navigation_menu2: {
//                        ToolbarTitle.setText("리스트");
//                        transaction.replace(R.id.fragment_body, menu2Fragment).commitAllowingStateLoss();
//                        break;
//                    }
//                    case R.id.navigation_menu3: {
//                        ToolbarTitle.setText("My 리스트");
//                        transaction.replace(R.id.fragment_body, menu3Fragment).commitAllowingStateLoss();
//                        break;
//                    }
//                    case R.id.navigation_menu4: {
//                        ToolbarTitle.setText("설정");
//                        transaction.replace(R.id.fragment_body, menu4Fragment).commitAllowingStateLoss();
//                        break;
//                    }
//                }
//
//                return true;
//            }
//        });



    }   // onCreate

    public void getRollingBanners() {


    }
    @OnClick(R.id.testbtn)
    public void testbtnClicked() {
        int id = getRawResIdByName("whale");
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + id);
        Intent intent = new Intent(this, STLViewActivity.class);
        intent.putExtra("uri", uri.toString());
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beginOpenModel();
                } else {
                    Toast.makeText(this, R.string.read_permission_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == RESULT_OK && resultData.getData() != null) {
            Uri uri = resultData.getData();
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent = new Intent(this, STLViewActivity.class);
                    intent.putExtra("uri", uri.toString());
        startActivity(intent);
           // beginLoadModel(uri);
        }
    }

    private void checkReadPermissionThenOpen() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION_REQUEST);
        } else {
            beginOpenModel();
        }
    }

    private void beginOpenModel() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
    }
    @Override
    public void onBackPressed() {
         super.onBackPressed();

    }


    @OnClick(R.id.back_btn)
    public void back_btnClicked() {
onBackPressed();
    }





}   // MainActivity
