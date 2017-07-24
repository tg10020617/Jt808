package com.example.lhb.jt808;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.lhb.common.TPMSConsts;
import com.example.lhb.tool.Jt808Client;
import com.example.lhb.util.HexStringUtils;
import com.example.lhb.util.NetworkUtils;
import com.example.lhb.vo.req.TerminalAuthentication;
import com.example.lhb.vo.req.TerminalRegisterMsg;
import com.example.lhb.vo.resp.CmdRegisterRespMsg;
import com.example.lhb.vo.resp.ServerCommonRespMsg;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/6/4 0004.
 * <p>
 * <p>
 * 网络改变广播接收
 */

public class NetworkReceiver extends BootReceiver {
    static boolean hasInternet = true;

    static JT808SQLite JTsqLite = null;
    static RegActivity reg = null;

    String rebackAuthMsg = "";

    @Override
    public void onReceive(final Context context, Intent intent)//关闭打开，接收两次
    {
        JTsqLite = new JT808SQLite();

        reg = RegActivity.regActivity;
        Toast.makeText(reg, intent.getAction().toString(), Toast.LENGTH_LONG).show();
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))//网络状态变化
        {
            Toast.makeText(reg, intent.getAction().toString(), Toast.LENGTH_LONG).show();
            if (!NetworkUtils.isNetworkConnected(context)) {//不可用
                Toast.makeText(reg, "bukeyong", Toast.LENGTH_LONG).show();
                hasInternet = false;
                if (!Jt808Service.client.isClosed()) {
                    Jt808Service.client.close();
                    Toast.makeText(reg, "网络关闭成功", Toast.LENGTH_LONG).show();
                }
            } else {
                if(Jt808Service.client.isClosed()) {


                    Toast.makeText(reg, "keyong", Toast.LENGTH_LONG).show();

                    new Thread()//重新连接socket
                    {
                        public void run() {

                            Jt808Service.client.start();
                            Jt808Service.client.setHandler(handler);
                            final TerminalRegisterMsg terminalRegisterMsg = new TerminalRegisterMsg(
                                    reg.getHeader(),reg.getReg_provinceid(),reg.getReg_cityid(),reg.getReg_fcid(),reg.getReg_terpe(),
                                    reg.getReg_terid(),
                                    reg.getReg_color(),
                                    reg.getReg_carnum()
                                    );
                            Jt808Service.client.setHandler(handler);
                            try {
                                Jt808Service.client.registerTerminal(terminalRegisterMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                        try
//                        {
//                            Log.i("headerMsg",reg.getReg_carnum());
//                            final TerminalRegisterMsg terminalRegisterMsg = new TerminalRegisterMsg(
//                                    reg.getHeader(),reg.getReg_provinceid(),reg.getReg_cityid(),reg.getReg_fcid(),reg.getReg_terpe(),
//                                    reg.getReg_terid(),
//                                    reg.getReg_color(),
//                                    reg.getReg_carnum()
//                                    );
//                            Jt808Service.client.setHandler(handler);
//                            Jt808Service.client.registerTerminal(terminalRegisterMsg);
//
//                            //final TerminalAuthentication terminalAuthentication=new TerminalAuthentication(0,false,"13843030549",0,0,0,"313131313131");
//                         //   final TerminalAuthentication terminalAuthentication=new TerminalAuthentication(0,false,reg.getPhoneNum(),0,0,0,rebackAuthMsg);
//
//                          //  Jt808Service.client.authenticationTerminal(terminalAuthentication);
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }

                        }
                    }.start();
                    hasInternet = true;

                }
            }

        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            reg = RegActivity.regActivity;
            Bundle data = msg.getData();
            byte[] b = data.getByteArray("data");
            switch (msg.what) {
                case TPMSConsts.cmd_common_resp:
                    ServerCommonRespMsg serverCommonRespMsg=new ServerCommonRespMsg(b);
                    Toast.makeText(reg,"平台通用应答回复"+serverCommonRespMsg.toString(),Toast.LENGTH_LONG).show();
//                    String str="7E0200002201384303054900000000000000000002029CCAC20778279900000000000317060612111101040000000A497E";
//
//                    byte[]bys=HexStringUtils.decodeHex2(str.toCharArray());
//                    try {
//                        Jt808Service.client.sendBytes(bys);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    new Thread()
                    {
                        public void run()
                        {
                            final List<byte[]> arr = JTsqLite.getList(reg);
                            for (int i = 0; i < arr.size(); i++) {
                                //Log.e("arr" + i, HexStringUtils.toHexString(arr.get(i)));
                                final byte[] b = arr.get(i);

                                try {
                                    Jt808Service.client.setHandler(handler2);
                                    Log.i("sqlmsg","ok1");
                                    Jt808Service.client.sendBytes(b);
                                    sleep(3000);
                                    Log.i("sqlmsg",HexStringUtils.toHexString(b));
                                    Log.i("sqlmsg","ok2");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                               //  break;
                            }
                            JTsqLite.drop(reg);
                        }
                    }.start();
                    break;
                case TPMSConsts.cmd_terminal_register_resp:

                    Log.e("register_resp", "here");

                    CmdRegisterRespMsg cmdRegisterRespMsg = new CmdRegisterRespMsg(b);
                    Toast.makeText(reg,"重新发送注册包回复"+cmdRegisterRespMsg.toString(),Toast.LENGTH_SHORT).show();
                    rebackAuthMsg=cmdRegisterRespMsg.getAuthCode();
                    Jt808Service.client.setHandler(handler);
                    final TerminalAuthentication terminalAuthentication=new TerminalAuthentication(0,false,reg.getPhoneNum(),Integer.parseInt(reg.getReg_fcid()),0,0,rebackAuthMsg);
                    new Thread() {
                        public void run() {
                            try {
                                Jt808Service.client.authenticationTerminal(terminalAuthentication);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                default:
                    Log.i("socket create", "?");
                    Toast.makeText(reg, "socket连接未知", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    Handler handler2=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            reg = RegActivity.regActivity;
            Bundle data = msg.getData();
            byte[] b = data.getByteArray("data");

            ServerCommonRespMsg serverCommonRespMsg=new ServerCommonRespMsg(b);
            Toast.makeText(reg,"sql发送回复"+serverCommonRespMsg.toString(),Toast.LENGTH_LONG).show();
        }
    };

}
