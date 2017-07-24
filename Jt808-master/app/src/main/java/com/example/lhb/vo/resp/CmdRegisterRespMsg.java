package com.example.lhb.vo.resp;

import android.util.Log;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BitOperator;
import com.example.lhb.util.HexStringUtils;

/**
 * Created by lhb on 2017/5/7.
 */

public class CmdRegisterRespMsg extends MsgFrame{
    private  int flowId;//流水号

    /*结果
    *0：成功
    * 1：车辆已被注册
    * 2：数据库中无该车辆
    * 3：终端已被注册
    * 4：数据库中无该终端
    * */
    private  int result;

    private  String authCode;//鉴权码（注册成功才有）
    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }


    public int getFlowId() {
        return flowId;
    }

    public int getResult() {
        return result;
    }

    public String getAuthCode() {
        return authCode;
    }


    public CmdRegisterRespMsg(byte[]bytes)
    {
        super(bytes);
        BitOperator bitOperator=new BitOperator();
        byte[]body=getBodyBytes(bytes);

        byte []flowId_bytes=bitOperator.splitBytes(body,0,1);
        byte []authCode_bytes=bitOperator.splitBytes(body,3,body.length-1);

        this.setFlowId(bitOperator.byteToInteger(flowId_bytes));
        this.setResult(body[2]);
        if(result==0)
        {

            this.setAuthCode(HexStringUtils.toHexString(authCode_bytes));
        }
        for(int i=0;i<body.length;i++)
        {
            Log.d("regbody"+i,body[i]+"");
        }
        for(int i=0;i<bytes.length;i++)
        {
            Log.d("bytes"+i,bytes[i]+"");
        }
        Log.d("reghead",this.msgHeader.toString());
    }
    public String toString()
    {
        return msgHeader.toString()+"消息体[流水号:"+this.flowId+",结果:"+result+",鉴权码："+authCode+"]";
    };
}
