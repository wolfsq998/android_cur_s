package com.example.install;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/24.
 */
public class XmlLocal {
    private ArrayList<String> att0= new ArrayList<>();
    private ArrayList<String> att1 = new ArrayList<>();
    private ArrayList<String> att2= new ArrayList<>();

    public XmlLocal(XmlResourceParser xrp,String tag){
        try {
            while(xrp.getEventType()!=XmlResourceParser.END_DOCUMENT){
                if(xrp.getEventType()==XmlResourceParser.START_TAG){
                    String tagName= xrp.getName();
                    if(tagName.equals(tag)){
                        att0.add(xrp.getAttributeValue(0));
                        att1.add(xrp.getAttributeValue(1));
                        att2.add(xrp.getAttributeValue(2));
                    }
                }
                xrp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getXmlName(){
        return att0;
    }

    public ArrayList<String> getXmlUrl(){
        return att1;
    }

    public ArrayList<String> getXmlStr(){
        return att2;
    }

}
