package com.krashtan.eug.r0b1c;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ekrashtan on 22.02.2018.
 */

public class ProgramFragment extends Fragment {
    public static ProgramFragment newInstance() {
        ProgramFragment fragment = new ProgramFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_program, container, false);
    }

}
