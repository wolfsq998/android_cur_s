package com.example.install;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by Administrator on 2016/5/2.
 */
public class CurView extends LinearLayout {

    private Context m_Context;
    public CurView(Context context){
        super(context);
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.cur_view, this);
        m_Context=context;
    }

    private void DelMe(){
        Button delBut = (Button)this.findViewById(R.id.del_but);
        delBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllViews();
            }
        });
    }

    public void CurInsPos(ArrayAdapter<String> adapter){
        Spinner cipsp = (Spinner)this.findViewById(R.id.cur_Ins_pos_sp);
        ArrayAdapter<String> adapterCIP =adapter;
        adapterCIP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//下来样式
        cipsp.setAdapter(adapterCIP);
    }
    public void CurRemark0(ArrayAdapter<String> adapter){
        Spinner crsp0 = (Spinner)this.findViewById(R.id.cur_remark0_sp);
        ArrayAdapter<String> adapterCR0 =adapter;
        adapterCR0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//下来样式
        crsp0.setAdapter(adapterCR0);
    }
    public void CurRemark1(ArrayAdapter<String> adapter){
        Spinner crsp1 = (Spinner)this.findViewById(R.id.cur_remark1_sp);
        ArrayAdapter<String> adapterCR1 =adapter;
        adapterCR1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//下来样式
        crsp1.setAdapter(adapterCR1);
    }
    public void CurRemark2(ArrayAdapter<String> adapter){
        Spinner crsp2 = (Spinner)this.findViewById(R.id.cur_remark2_sp);
        ArrayAdapter<String> adapterCR2 =adapter;
        adapterCR2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//下来样式
        crsp2.setAdapter(adapterCR2);
    }
    public void CurTrainsWay(ArrayAdapter<String> adapter){
        Spinner ctwsp = (Spinner)this.findViewById(R.id.cur_trains_way_sp);
        ArrayAdapter<String> adapterCTW =adapter;
        adapterCTW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//下来样式
        ctwsp.setAdapter(adapterCTW);
    }
}
