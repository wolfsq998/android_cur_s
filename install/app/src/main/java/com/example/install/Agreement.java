package com.example.install;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/10.
 */
public class Agreement extends AppCompatActivity implements View.OnClickListener{

    public static Agreement instance = null;
    private static final String ARG = "Agreement";

    private Button m_YesBut,m_OutBut;
    private ArrayList<Drawable> m_Img = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_agreement);
        instance = this;

        init();
    }
    private void init(){
        m_YesBut = (Button)findViewById(R.id.yes_but);
        m_YesBut.setOnClickListener(this);
        m_OutBut = (Button)findViewById(R.id.out_but);
        m_OutBut.setOnClickListener(this);
        m_Img.add(getResources().getDrawable(R.drawable.icon_checkmark1));
        m_Img.add(getResources().getDrawable(R.drawable.icon_checkmark3));

        for (int i = 0; i < m_Img.size(); i++) {
            m_Img.get(i).setBounds(1, 1, m_Img.get(i).getIntrinsicWidth() / 3 * 2, m_Img.get(i).getIntrinsicHeight() / 3 * 2);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.yes_but:
                RegWindow.instance.m_CheckBoxXY.setChecked(true);
                RegWindow.instance.m_CheckBoxXY.setCompoundDrawables(m_Img.get(0),null,null,null);
                finish();
                break;
            case R.id.out_but:
                RegWindow.instance.m_CheckBoxXY.setChecked(false);
                RegWindow.instance.m_CheckBoxXY.setCompoundDrawables(m_Img.get(1), null, null, null);
                finish();
                break;
            default:
                break;
        }
    }
}
