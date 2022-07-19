package com.printurth.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.printurth.SessionManager;

/**
 * Created by JSky on 2019-02-22.
 */
public class BaseFragment extends Fragment {

    SessionManager session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity());
    }

    public SessionManager getSession() {
        return session;
    }


}
