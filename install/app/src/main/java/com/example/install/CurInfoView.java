package com.example.install;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/8.
 */
public class CurInfoView extends LinearLayout {

    private Context m_Context;
    public CurInfoView(Context context){
        super(context);
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.cur_info_view, this);
        m_Context=context;
    }
}
