package com.example.install;

/**
 * Created by Administrator on 2016/1/11.
 */

import com.netease.pomelo.PomeloClient;

public class MyClient {
    private static final String ARG = "MyClient";

    public static MyClient instance = null;
    public static PomeloClient client = null;
    private static String test_ip = "192.168.1.111";
    private static int test_port = 3010;

    public static MyClient GetInstance(){
        if(instance == null)
        {
            instance = new MyClient();
            //InitClient();
        }
        return instance;
    }
    public void setServerAddre(String ip,int port){
        test_ip=ip;
        test_port=port;
    }

    public PomeloClient getClient(){
        if(InitClient()){
            return client;
        }
        return client ;
    }

    public static boolean InitClient(){
        if(client == null){
            client=new PomeloClient(test_ip,test_port);
            client.init();
            return true;
        }
        return false;
    }

}
