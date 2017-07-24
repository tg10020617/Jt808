package com.example.lhb.vo.req;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;
import com.example.lhb.util.HexStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 终端通用应答
 * Created by Administrator on 2017/5/9 0009.
 */

public class TerminalCommonResp extends MsgFrame {



    //应答流水号WORD
    protected int flowId;
    //应答ID  WORD
    protected int msgId;
    //结果    BYTE
    protected int result;

    public TerminalCommonResp(int encryptiontype,boolean issubpackage,String terminalphone,
                              int flowed,int totalsubpackage,int seq,int msg,int ret)
    //加密方式，分包，电话，流水号，包总数，包序号，平台消息ID，结果
    {
        this.msgHeader=new MsgHeader();
        msgHeader.setMsgId(TPMSConsts.msg_id_terminal_common_resp);
        msgHeader.setMsgBodyLength(5);
        msgHeader.setEncryptionType(encryptiontype);
        msgHeader.setHasSubPackage(issubpackage);
        msgHeader.setReservedBit(0);
        msgHeader.setTerminalPhone(terminalphone);
        msgHeader.setFlowId(flowed);
        if(issubpackage)
        {
            msgHeader.setTotalSubPackage(totalsubpackage);
            msgHeader.setSubPackageSeq(seq);
        }

        setFlowId(flowed);
        setMsgId(msg);
        setResult(ret);
    }

    public int getFlowId(){return flowId;}

    public void setFlowId(int id) {this.flowId=id;}

    public int getMsgId(){return msgId;}
    public void setMsgId(int mid){this.msgId=mid;}

    public int getResult(){return result;}
    public void setResult(int ret){this.result=ret;}



    public byte[] getMessageBodybytes() {
        BitOperator bitOperator = new BitOperator();
        List<byte[]> listbyte = new ArrayList<>();
        byte[] flowid=bitOperator.numToByteArray(this.getFlowId(),2);
        byte[] msgid=bitOperator.numToByteArray(this.getMsgId(),2);
        byte[] ret = new byte[]{(byte) (this.getResult())};

        listbyte.add(flowid);
        listbyte.add(msgid);
        listbyte.add(ret);

        byte[]bodybytes=bitOperator.concatAll(listbyte);
        return bodybytes ;
    }

    public byte[] getAllBytes() {//所有数据报字节
        BitOperator bitOperator = new BitOperator();
        byte[] headbytes = this.msgHeader.getHeaderbytes();
        byte[] bodybytes = getMessageBodybytes();
        List<byte[]> listbytes = new ArrayList<>();
        listbytes.add(headbytes);
        listbytes.add(bodybytes);
        byte[] srcbytes = bitOperator.concatAll(listbytes);
        byte[] check = new byte[]{(byte) (bitOperator.getCheckSum4JT808(srcbytes, 0, srcbytes.length))};
        byte[] flag = new byte[]{TPMSConsts.pkg_delimiter};
        byte[] sendbytes = bitOperator.concatAll(flag, srcbytes, check, flag);
        return sendbytes;
    }
}
