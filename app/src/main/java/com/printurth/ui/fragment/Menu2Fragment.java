package com.printurth.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.printurth.R;
import com.printurth.adapter.PRListViewAdapter;
import com.printurth.database.DbOpenHelper;
import com.printurth.model.RollingBannerModel;
import com.printurth.ui.activity.PRDetailActivity;
import com.synnapps.carouselview.CarouselView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Menu2Fragment extends BaseFragment{
    private static Menu2Fragment instance;
    @BindView(R.id.carouselView)
    CarouselView carouselView;

    private DbOpenHelper mDbOpenHelper;

    List<RollingBannerModel> rollingBannerModelList;
    private Parcelable recyclerViewState;
    @BindView(R.id.listview)
    RecyclerView listView;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
   // private ListView listView;                      // 리스트뷰
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private List<RollingBannerModel> list;                      // String 데이터를 담고있는 리스트
    private PRListViewAdapter adapter;                // 리스트뷰의 아답터
    private int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 20;                  // 한 페이지마다 로드할 데이터 갯수.
  //  private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private int pagingCount = 0;
    public static Menu2Fragment getInstance() {
        return instance;
    }
    public void setRefreshAdapter() {
        //Log.e("~!", "새로고침");
// Save state

        recyclerViewState = listView.getLayoutManager().onSaveInstanceState();



        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    //    adapter.notifyItemChanged(position);
       FragmentTransaction ft = getFragmentManager().beginTransaction();


        ft.detach(this).attach(this).commitNow();
// Restore state
        listView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

//        listView.scrollToPosition(position);

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu2, container, false);
        ButterKnife.bind(this, view);
        instance = this;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getDate = sdf.format(date);
        //long time = Long.parseLong(getTime);
        //Log.e("~!", getDate);
        listView.setHasFixedSize(true);


        listView.setNestedScrollingEnabled(false);
        listView.setFocusable(false);
       // Log.e("~!", "session.isSetDB()" + session.isSetDB());
if(session.isSetDB()) {
    mDbOpenHelper = new DbOpenHelper(getContext());
    mDbOpenHelper.open();
    mDbOpenHelper.create();


    rollingBannerModelList = new ArrayList<>();
    list = new ArrayList<>();
    Cursor iCursor = mDbOpenHelper.selectColumns();
    while(iCursor.moveToNext()){
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
        int isfin = iCursor.getInt(iCursor.getColumnIndex("isfin"));
        int like = iCursor.getInt(iCursor.getColumnIndex("like"));
        String lastDate = iCursor.getString(iCursor.getColumnIndex("lastdate"));

        RollingBannerModel rollingBannerModel = new RollingBannerModel(id,fileName,title,thumbImgName,makerName,uploadAt,level,time,filament,description,isfin,like,lastDate);

        rollingBannerModelList.add(rollingBannerModel);
    }
    carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    });
    carouselView.setImageListener((position, imageView) -> {

        // GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(MainActivity.this, R.drawable.rounded_8dp);
        // imageView.setBackground(drawable);
        // Glide.with(this).load(savePath).into(imageView);
        int id = getRawResIdByName(rollingBannerModelList.get(position).getThumbImgName());
        Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + id);
        Glide.with(getContext()).load(uri).into(imageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setClipToOutline(true);
        }
    });
    carouselView.setPageCount(5);


}

//        listView = (ListView) findViewById(R.id.listview);
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        list = new ArrayList<RollingBannerModel>();
        adapter = new PRListViewAdapter(getContext(), rollingBannerModelList);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        listView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);





        return view;
    }

    void getRollingBanner(){




    } // end doJSONParser()

    // getRawResIdByName
    public int getRawResIdByName(String resName) {
        String pkgName = getContext().getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);

        return resID;
    }

    @OnClick(R.id.testbtn_doan)
    public void testbtn_doanClicked() {
        Intent intent = new Intent(getActivity(), PRDetailActivity.class);
        startActivity(intent);
    }



}
