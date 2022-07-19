package com.printurth.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.printurth.R;
import com.printurth.adapter.MyListViewAdapter;
import com.printurth.adapter.PRListViewAdapter;
import com.printurth.database.DbOpenHelper;
import com.printurth.model.RollingBannerModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Menu3Fragment extends BaseFragment {
    private static Menu3Fragment instance;
    List<RollingBannerModel> rollingBannerModelList;
    private Parcelable recyclerViewState;
    @BindView(R.id.listview)
    RecyclerView listView;

    @BindView(R.id.fincount)
    TextView tv_fincount;
    private DbOpenHelper mDbOpenHelper;
    private MyListViewAdapter adapter;
    private List<RollingBannerModel> list;
    public boolean isRefresh = false;

    int finCount = 0;

    public static Menu3Fragment getInstance() {
        return instance;
    }

    public void setRefreshAdapter() {
        //Log.e("~!", "새로고침");
// Save state

        recyclerViewState = listView.getLayoutManager().onSaveInstanceState();


        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (session.isSetDB()) {
            finCount = 0;
            mDbOpenHelper = new DbOpenHelper(getContext());
            mDbOpenHelper.open();
            mDbOpenHelper.create();

            rollingBannerModelList = new ArrayList<>();
            list = new ArrayList<>();
            Cursor iCursor = mDbOpenHelper.selectColumns();
            while (iCursor.moveToNext()) {

                int isfin = iCursor.getInt(iCursor.getColumnIndex("isfin"));



                if(isfin == 1) {

                    finCount++;
                }

            }
            tv_fincount.setText("내가 완성한 작품 " + String.valueOf(finCount) + " 건");

        }
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
        View view = inflater.inflate(R.layout.fragment_menu3, container, false);
        ButterKnife.bind(this, view);
        instance = this;
        listView.setHasFixedSize(true);



        listView.setNestedScrollingEnabled(false);
        listView.setFocusable(false);

        if (session.isSetDB()) {
            finCount = 0;
            mDbOpenHelper = new DbOpenHelper(getContext());
            mDbOpenHelper.open();
            mDbOpenHelper.create();

            rollingBannerModelList = new ArrayList<>();
            list = new ArrayList<>();
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
                int isfin = iCursor.getInt(iCursor.getColumnIndex("isfin"));
                int like = iCursor.getInt(iCursor.getColumnIndex("like"));
                String lastDate = iCursor.getString(iCursor.getColumnIndex("lastdate"));


                if(like == 1) {
                    RollingBannerModel rollingBannerModel = new RollingBannerModel(id, fileName, title, thumbImgName, makerName, uploadAt, level, time, filament, description, isfin, like, lastDate);
                    rollingBannerModelList.add(rollingBannerModel);

                }
                if(isfin == 1) {

                    finCount++;
                }

            }
        }

            list = new ArrayList<RollingBannerModel>();
            adapter = new MyListViewAdapter(getContext(), rollingBannerModelList);
            listView.setLayoutManager(new LinearLayoutManager(getContext()));

            listView.setAdapter(adapter);
        tv_fincount.setText("내가 완성한 작품 " + String.valueOf(finCount) + " 건");
            return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        tv_fincount.setText("내가 완성한 작품 " + String.valueOf(finCount) + " 건");

    }
}
