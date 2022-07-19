package com.printurth.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.printurth.R;
import com.printurth.database.DbOpenHelper;
import com.printurth.model.RollingBannerModel;
import com.printurth.ui.activity.PRDetailActivity;
import com.printurth.ui.fragment.Menu2Fragment;
import com.printurth.ui.fragment.Menu3Fragment;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyListViewAdapter extends RecyclerView.Adapter<MyListViewAdapter.Holder> {
 //   private LayoutInflater inflate;
    //private ViewHolder viewHolder;
    private List<RollingBannerModel> list;
    Context context;

  //  MainActivity mainActivity;

    public MyListViewAdapter(Context context, List<RollingBannerModel> list){
        // MainActivity 에서 데이터 리스트를 넘겨 받는다.
        this.list = list;
      //  this.inflate = LayoutInflater.from(context);
        this.context = context;

    }

    // ViewHolder 생성
    // row layout을 화면에 뿌려주고 holder에 연결
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_mylist_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    /*
     * Todo 만들어진 ViewHolder에 data 삽입 ListView의 getView와 동일
     *
     * */
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        // 각 위치에 문자열 세팅
        int itemposition = position;
        int thumbnailid = getRawResIdByName(list.get(position).getThumbImgName());
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + thumbnailid);
        Glide.with(context).load(uri).into(holder.thumbnail);

        holder.title.setText( list.get(position).getTitle() );
        holder.makername.setText( list.get(position).getMakerName() );
        holder.uploadat.setText( list.get(position).getUploadAt() );

        holder.lastdate.setText( list.get(position).getLastdate() );




        if(list.get(position).getIsfin() == 0) {

            Glide.with(context).load(R.drawable.icn_uncheck).into(holder.isFin);
        } else {

            Glide.with(context).load(R.drawable.icn_setcheck).into(holder.isFin);
        }
        if(list.get(position).getLike() == 0) {
            Glide.with(context).load(R.drawable.icn_unlike).into(holder.like);
        } else {
            Glide.with(context).load(R.drawable.icn_like).into(holder.like);
        }




        holder.like.setOnClickListener(view -> {
            if(list.get(position).getLike() == 0) {
                Glide.with(context).load(R.drawable.icn_like).into(holder.like);
                updateDBLike(list.get(position).getId(), 1);

             //   notifyItemChanged(position);

                Menu3Fragment.getInstance().setRefreshAdapter();


            } else {
                Glide.with(context).load(R.drawable.icn_unlike).into(holder.like);
                updateDBLike(list.get(position).getId(), 0);
             //   notifyItemChanged(position);
                Menu3Fragment.getInstance().setRefreshAdapter();

            }
        });
        holder.isFin.setOnClickListener(view -> {
            if(list.get(position).getIsfin() == 0) {
                Glide.with(context).load(R.drawable.icn_uncheck).into(holder.isFin);
                updateDBFin(list.get(position).getId(), 1);

                //   notifyItemChanged(position);

                Menu3Fragment.getInstance().setRefreshAdapter();


            } else {
                Glide.with(context).load(R.drawable.icn_setcheck).into(holder.isFin);
                updateDBFin(list.get(position).getId(), 0);
                //   notifyItemChanged(position);
                Menu3Fragment.getInstance().setRefreshAdapter();

            }
        });
        holder.item_layer.setOnClickListener(view -> {
            Intent intent = new Intent(context, PRDetailActivity.class);
            intent.putExtra("data_id", list.get(position).getId());
            intent.putExtra("data_thumbnail", list.get(position).getThumbImgName());
            intent.putExtra("data_title", list.get(position).getTitle());
            intent.putExtra("data_makername", list.get(position).getMakerName());
            intent.putExtra("data_uploadat", list.get(position).getUploadAt());
            intent.putExtra("data_like", list.get(position).getLike());
            intent.putExtra("data_level", list.get(position).getLevel());
            intent.putExtra("data_time", list.get(position).getTime());
            intent.putExtra("data_filament", list.get(position).getFilament());
            intent.putExtra("data_filename", list.get(position).getFileName());
            intent.putExtra("data_description", list.get(position).getDescription());

            context.startActivity(intent);
        });



    }

    // 몇개의 데이터를 리스트로 뿌려줘야하는지 반드시 정의해줘야한다
    @Override
    public int getItemCount() {
        return list.size(); // RecyclerView의 size return
    }

    // ViewHolder는 하나의 View를 보존하는 역할을 한다
    public class Holder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView title;
        public TextView makername;
        public TextView uploadat;
        public ImageView like;
        public ImageView isFin;
        public ImageView go_detail_item;
        public LinearLayout item_layer;
        public TextView lastdate;
        public TextView fincount;


        public Holder(View view){
            super(view);


            fincount = (TextView) view.findViewById(R.id.fincount);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            title = (TextView) view.findViewById(R.id.title);
            makername = (TextView) view.findViewById(R.id.makername);
            uploadat = (TextView) view.findViewById(R.id.uploadat);
            like = (ImageView) view.findViewById(R.id.like);
            isFin = (ImageView) view.findViewById(R.id.isFin);
            lastdate = (TextView) view.findViewById(R.id.lastdate);

            item_layer = (LinearLayout) view.findViewById(R.id.item_layer);
            // 각 셀에 넘겨받은 텍스트 데이터를 넣는다.
//        int thumbnailid = getRawResIdByName(list.get(position).getThumbImgName());
//        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + thumbnailid);
//        Glide.with(context).load(uri).into(viewHolder.thumbnail);
//
//        viewHolder.title.setText( list.get(position).getTitle() );
//        viewHolder.makername.setText( list.get(position).getMakerName() );
//        viewHolder.uploadat.setText( list.get(position).getUploadAt() );
//
//
//        if(list.get(position).getIsfin() == 1) {
//            viewHolder.isFin.setVisibility(View.VISIBLE);
//            Glide.with(context).load(R.drawable.icn_check).into(viewHolder.isFin);
//        } else {
//            viewHolder.isFin.setVisibility(View.INVISIBLE);
//        }
//        if(list.get(position).getLike() == 0) {
//            Glide.with(context).load(R.drawable.icn_unlike).into(viewHolder.like);
//        } else {
//            Glide.with(context).load(R.drawable.icn_like).into(viewHolder.like);
//        }
//
//
//
//
//        viewHolder.like.setOnClickListener(view -> {
//            if(list.get(position).getLike() == 0) {
//                Glide.with(context).load(R.drawable.icn_like).into(viewHolder.like);
//                updateDBLike(list.get(position).getId(), 1);
//
//
//               Menu2Fragment.getInstance().setRefreshAdapter();
//
//
//            } else {
//                Glide.with(context).load(R.drawable.icn_unlike).into(viewHolder.like);
//                updateDBLike(list.get(position).getId(), 0);
//
//                Menu2Fragment.getInstance().setRefreshAdapter();
//
//            }
//        });
//        viewHolder.item_layer.setOnClickListener(view -> {
//            Intent intent = new Intent(context, PRDetailActivity.class);
//            intent.putExtra("data_id", list.get(position).getId());
//            intent.putExtra("data_thumbnail", list.get(position).getThumbImgName());
//            intent.putExtra("data_title", list.get(position).getTitle());
//            intent.putExtra("data_makername", list.get(position).getMakerName());
//            intent.putExtra("data_uploadat", list.get(position).getUploadAt());
//            intent.putExtra("data_like", list.get(position).getLike());
//            intent.putExtra("data_level", list.get(position).getLevel());
//            intent.putExtra("data_time", list.get(position).getTime());
//            intent.putExtra("data_filament", list.get(position).getFilament());
//            intent.putExtra("data_filename", list.get(position).getFileName());
//            intent.putExtra("data_description", list.get(position).getDescription());
//
//            context.startActivity(intent);
//        });
//        viewHolder.go_detail_item.setOnClickListener(view -> {
//            Intent intent = new Intent(context, PRDetailActivity.class);
//            intent.putExtra("data_id", list.get(position).getId());
//            intent.putExtra("data_thumbnail", list.get(position).getThumbImgName());
//            intent.putExtra("data_title", list.get(position).getTitle());
//            intent.putExtra("data_makername", list.get(position).getMakerName());
//            intent.putExtra("data_uploadat", list.get(position).getUploadAt());
//            intent.putExtra("data_like", list.get(position).getLike());
//            intent.putExtra("data_level", list.get(position).getLevel());
//            intent.putExtra("data_time", list.get(position).getTime());
//            intent.putExtra("data_filament", list.get(position).getFilament());
//            intent.putExtra("data_filename", list.get(position).getFileName());
//            intent.putExtra("data_description", list.get(position).getDescription());
//
//            context.startActivity(intent);
//        });
        }
    }




//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        if(convertView == null){
//            convertView = inflate.inflate(R.layout.view_prlist_item,null);
//            viewHolder = new ViewHolder();
//            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
//            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
//            viewHolder.makername = (TextView) convertView.findViewById(R.id.makername);
//            viewHolder.uploadat = (TextView) convertView.findViewById(R.id.uploadat);
//            viewHolder.like = (ImageView) convertView.findViewById(R.id.like);
//            viewHolder.isFin = (ImageView) convertView.findViewById(R.id.isFin);
//            viewHolder.go_detail_item = (ImageView) convertView.findViewById(R.id.go_detail_item);
//            viewHolder.item_layer = (LinearLayout) convertView.findViewById(R.id.item_layer);
//
//
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
//
//
//        // 각 셀에 넘겨받은 텍스트 데이터를 넣는다.
//        int thumbnailid = getRawResIdByName(list.get(position).getThumbImgName());
//        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + thumbnailid);
//        Glide.with(context).load(uri).into(viewHolder.thumbnail);
//
//        viewHolder.title.setText( list.get(position).getTitle() );
//        viewHolder.makername.setText( list.get(position).getMakerName() );
//        viewHolder.uploadat.setText( list.get(position).getUploadAt() );
//
//
//        if(list.get(position).getIsfin() == 1) {
//            viewHolder.isFin.setVisibility(View.VISIBLE);
//            Glide.with(context).load(R.drawable.icn_check).into(viewHolder.isFin);
//        } else {
//            viewHolder.isFin.setVisibility(View.INVISIBLE);
//        }
//        if(list.get(position).getLike() == 0) {
//            Glide.with(context).load(R.drawable.icn_unlike).into(viewHolder.like);
//        } else {
//            Glide.with(context).load(R.drawable.icn_like).into(viewHolder.like);
//        }
//
//
//
//
//        viewHolder.like.setOnClickListener(view -> {
//            if(list.get(position).getLike() == 0) {
//                Glide.with(context).load(R.drawable.icn_like).into(viewHolder.like);
//                updateDBLike(list.get(position).getId(), 1);
//
//
//               Menu2Fragment.getInstance().setRefreshAdapter();
//
//
//            } else {
//                Glide.with(context).load(R.drawable.icn_unlike).into(viewHolder.like);
//                updateDBLike(list.get(position).getId(), 0);
//
//                Menu2Fragment.getInstance().setRefreshAdapter();
//
//            }
//        });
//        viewHolder.item_layer.setOnClickListener(view -> {
//            Intent intent = new Intent(context, PRDetailActivity.class);
//            intent.putExtra("data_id", list.get(position).getId());
//            intent.putExtra("data_thumbnail", list.get(position).getThumbImgName());
//            intent.putExtra("data_title", list.get(position).getTitle());
//            intent.putExtra("data_makername", list.get(position).getMakerName());
//            intent.putExtra("data_uploadat", list.get(position).getUploadAt());
//            intent.putExtra("data_like", list.get(position).getLike());
//            intent.putExtra("data_level", list.get(position).getLevel());
//            intent.putExtra("data_time", list.get(position).getTime());
//            intent.putExtra("data_filament", list.get(position).getFilament());
//            intent.putExtra("data_filename", list.get(position).getFileName());
//            intent.putExtra("data_description", list.get(position).getDescription());
//
//            context.startActivity(intent);
//        });
//        viewHolder.go_detail_item.setOnClickListener(view -> {
//            Intent intent = new Intent(context, PRDetailActivity.class);
//            intent.putExtra("data_id", list.get(position).getId());
//            intent.putExtra("data_thumbnail", list.get(position).getThumbImgName());
//            intent.putExtra("data_title", list.get(position).getTitle());
//            intent.putExtra("data_makername", list.get(position).getMakerName());
//            intent.putExtra("data_uploadat", list.get(position).getUploadAt());
//            intent.putExtra("data_like", list.get(position).getLike());
//            intent.putExtra("data_level", list.get(position).getLevel());
//            intent.putExtra("data_time", list.get(position).getTime());
//            intent.putExtra("data_filament", list.get(position).getFilament());
//            intent.putExtra("data_filename", list.get(position).getFileName());
//            intent.putExtra("data_description", list.get(position).getDescription());
//
//            context.startActivity(intent);
//        });
//
//
//        return convertView;
//    }
//
//
//
//    class ViewHolder{
//        public ImageView thumbnail;
//        public TextView title;
//        public TextView makername;
//        public TextView uploadat;
//        public ImageView like;
//        public ImageView isFin;
//        public ImageView go_detail_item;
//        public LinearLayout item_layer;
//    }




    // getRawResIdByName
    public int getRawResIdByName(String resName) {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName, "raw", pkgName);

        return resID;
    }
    public void updateDBFin(String id, int isfin) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(context);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        mDbOpenHelper.updateColumnFin(id, isfin);
        mDbOpenHelper.close();



    }
    public void updateDBLike(String id, int like) {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(context);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        mDbOpenHelper.updateColumnLike(id, like);
        mDbOpenHelper.close();



    }
}
