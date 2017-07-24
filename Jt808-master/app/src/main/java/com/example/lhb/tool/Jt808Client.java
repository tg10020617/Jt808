package com.example.lhb.tool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.jt808.RegActivity;
import com.example.lhb.util.BitOperator;
import com.example.lhb.util.HexStringUtils;
import com.example.lhb.vo.req.TerminalAuthentication;
import com.example.lhb.vo.req.TerminalCommonResp;
import com.example.lhb.vo.req.TerminalHeartBeat;
import com.example.lhb.vo.req.TerminalLocationReport;
import com.example.lhb.vo.req.TerminalLogOut;
import com.example.lhb.vo.req.TerminalRegisterMsg;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import java.nio.channels.SocketChannel;

/**
 * Created by lhb on 2017/5/5.
 */

public class Jt808Client {
    private String ipaddr;
    private int port;
    ByteBuffer writebuffer;
    ByteBuffer readbuffer;
    SocketChannel channel;
    Handler handler=null;
    public Jt808Client(String ip, int port) {
        this.ipaddr = ip;
        this.port = port;
    }

    public  void setHandler(Handler handler)
    {
        this.handler=handler;
    }
    public void finishConnect()
    {
        try {

                //不断地轮询连接状态，直到完成连接
                while (!channel.finishConnect()){
                    //System.out.print(".");
                    Log.e("connect socket","while");
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    SocketAddress socketAddress;

   public void beginReceive()
    {
        new Thread()
        {

            public void run()
            {
                readbuffer = ByteBuffer.allocate(2048);//JVM级的内存分配
                while (true)
                {

                    try {
                        int len=0;
                        if((len=channel.read(readbuffer))>0&&handler!=null)
                        {
                            readbuffer.flip();
                            byte[]rb=readbuffer.array();

                            BitOperator bitOperator=new BitOperator();
                            byte []bytes= bitOperator.splitBytes(rb,0,len-1);
                            MsgFrame msgFrame=new MsgFrame(bytes);
                            MsgHeader msgHeader=msgFrame.getHearder();

                            Message message=new Message();
                            message.what=msgHeader.getMsgId();
                            Bundle bundle=new Bundle();
                            bundle.putByteArray("data",bytes);
                            message.setData(bundle);
                            handler.sendMessage(message);
                            Log.i("regismsg","rebackmsg");
                        }




                    } catch (IOException e) {
                        Log.i("error","socket is lost connect");
                        Log.i("error",channel.isConnected()+"");
                        Log.i("error",e.toString());
                        e.printStackTrace();
                        break;



                    }
                }

            }
        }.start();
    }
    public void start() {//连接server
        try {

            channel = SocketChannel.open();//连接到TCP网络套接字的通道
            channel.configureBlocking(false);//非阻塞IO
            channel.socket().setSoTimeout(100);
             socketAddress = new InetSocketAddress(ipaddr, port);
            channel.connect(socketAddress);
            finishConnect();

            beginReceive();
//
//            if(!channel.finishConnect()&&handler!=null)
//            {
//                Message message=new Message();
//                message.what=0x1236;
//                Bundle bundle=new Bundle();
//                message.setData(bundle);
//                handler.sendMessage(message);
//            }




        } catch (IOException e) {
            Log.i("socketCon","error2");
            e.printStackTrace();

        }
    }

    public  boolean isConnected()
    {
        return channel.isConnected();
    }
    public void sendBytes(byte[] bytes) throws IOException {//发送数据
        if(bytes==null)
            return;
         writebuffer = ByteBuffer.wrap(bytes);
        // 如果正在连接，则完成连接
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        try{
            Log.i("location","ok2");
            channel.write(writebuffer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void close()
    {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isClosed()
    {
        return  !channel.isConnected();
    }
    public void registerTerminal(TerminalRegisterMsg terminalAuthenticationMsg)throws IOException//注册
    {
        // 如果正在连接，则完成连接
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        byte[]sendbytes=terminalAuthenticationMsg.getAllBytes();
        String str= HexStringUtils.toHexString(sendbytes);
       // Log.i("location",str);
        Log.i("regismsg","ok3");
        sendBytes(sendbytes);//发送消息

    }

    //位置信息汇报
    public void locationReport(TerminalLocationReport terminalLocationReport) throws IOException {
        if(channel.isConnectionPending()){
            channel.finishConnect();
        }
       // byte[]sendbyte=terminalLocationReport.getAllBytes();
    }

    //心跳包
    public void heartbeatTerminal(TerminalHeartBeat terminalHeartBeat)throws IOException
    {
        if(channel.isConnectionPending()){
            channel.finishConnect();
        }
        byte[]sendbyte=terminalHeartBeat.getAllBytes();

        sendBytes(sendbyte);
    }
    public void authenticationTerminal(TerminalAuthentication terminalAuthentication)throws IOException
    {
        if(channel.isConnectionPending()){channel.finishConnect();}
        byte[] sendbyte=terminalAuthentication.getAllBytes();
        Log.i("authmsg","ok3");
        sendBytes(sendbyte);
    }
    public void commonRespTerminal(TerminalCommonResp terminalCommonResp)throws IOException
    {
        if(channel.isConnectionPending()){channel.finishConnect();}
        byte[] sendbyte=terminalCommonResp.getAllBytes();
        sendBytes(sendbyte);
    }
    public void logOutTerminal(TerminalLogOut terminalLogOut) throws IOException {
        if(channel.isConnectionPending()){channel.finishConnect();}
        byte[] sendbyte=terminalLogOut.getAllBytes();
        sendBytes(sendbyte);
    }

    public void logoff() {

    }

    public void checkLocation() {

    }

    public void checkTerminalparams() {

    }
}
