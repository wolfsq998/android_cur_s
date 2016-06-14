package com.example.install;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/3.
 */
public class XmlStrManager {
    private ArrayList<String> mList = new ArrayList<>();
    public static XmlStrManager instance = null;
    public static XmlStrManager GetInstance(){
        if(instance == null)
        {
            instance = new XmlStrManager();
        }
        return instance;
    }

    //写
    public String WriteXmlStr( ArrayList<String> addressList,ArrayList<String> areaList ) {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", "address");
            for (int i=0;i<addressList.size();i++)
                addItem(xmlSerializer,addressList.get(i),areaList.get(i));
            xmlSerializer.endTag("","address");
            xmlSerializer.endDocument();
            return writer.toString();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    private  void addItem(XmlSerializer xsl,String mAddr,String index){
        try {
            xsl.startTag(null, "addr");

            xsl.startTag(null,"addritem");
            xsl.text(mAddr);
            xsl.endTag(null, "addritem");

            xsl.startTag(null, "areaid");
            xsl.text(String.valueOf(index));
            xsl.endTag(null,"areaid");

            xsl.endTag(null, "addr");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读
    public ArrayList<String> ReadXmlStr(File file){
        String str,areaNu;
        try {
            InputStream is = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlResourceParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlResourceParser.START_DOCUMENT:
                        break;
                    case XmlResourceParser.START_TAG:
                        if (parser.getName().equals("addritem")) {
                            str = parser.nextText();
                            Log.i("@@@", "str值"+str);
                            mList.add(str);
                        }
                        if (parser.getName().equals("areaid")){
                            areaNu = parser.nextText();
                            Log.i("@@@", "id值"+areaNu);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return mList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mList = null;
    }

}
