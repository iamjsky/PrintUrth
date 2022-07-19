package com.printurth.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.printurth.R;
import com.printurth.ui.activity.InfoActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Menu4Fragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu4, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick(R.id.notice_layer)
    public void notice_layerClicked() {
        Intent intent = new Intent(getContext(), InfoActivity.class);
        intent.putExtra("type", "notice");
        startActivity(intent);
    }
    @OnClick(R.id.version_layer)
    public void version_layerClicked() {
        Intent intent = new Intent(getContext(), InfoActivity.class);
        intent.putExtra("type", "version");
        startActivity(intent);
    }
    @OnClick(R.id.ct_email_layer)
    public void ct_email_layerClicked() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        // email setting 배열로 해놔서 복수 발송 가능
        String[] address = {"printurth@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT,"[고객문의] 제목 입력");
        email.putExtra(Intent.EXTRA_TEXT,"문의하실 내용을 입력해 주세요.\n");
        startActivity(email);

    }
    @OnClick(R.id.ad_email_layer)
    public void ad_email_layerClicked() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        // email setting 배열로 해놔서 복수 발송 가능
        String[] address = {"printurth@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT,"[광고문의] 제목 입력\"");
        email.putExtra(Intent.EXTRA_TEXT,"문의하실 내용을 입력해 주세요.\n");
        startActivity(email);

    }
}
