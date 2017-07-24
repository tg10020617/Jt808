package com.example.lhb.vo.req;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BitOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class TerminalHeartBeat extends MsgFrame {


        /*
    // 消息ID
    protected int msgId;

    /////// ========消息体属性
    // byte[2-3]
    protected byte msgBodyPropsField;
    // 消息体长度
    protected int msgBodyLength;
    // 数据加密方式
    protected int encryptionType;
    // 是否分包,true==>有消息包封装项
    protected boolean hasSubPackage;
    // 保留位[14-15]
    protected int reservedBit;
    /////// ========消息体属性

    // 终端手机号
    protected String terminalPhone;
    // 流水号
    protected int flowId;

    //////// =====消息包封装项
    // byte[12-15]
    protected int packageInfoField;
    // 消息包总数(word(16))
    protected long totalSubPackage;
    // 包序号(word(16))这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
    protected long subPackageSeq;
    //////// =====消息包封装项
    */

    public TerminalHeartBeat(int msgbodylength,int encryptiontype,boolean issubpackage,String terminalphone,
                                int flowed,int totalsubpackage,int seq)
            //消息体长度，加密方式，分包，电话，流水号，包总数，包序号
    {
        this.msgHeader=new MsgHeader();
        msgHeader.setMsgId(TPMSConsts.msg_id_terminal_heart_beat);
        msgHeader.setMsgBodyLength(msgbodylength);
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
    }

    public byte[] getAllBytes() {//所有数据报字节
        BitOperator bitOperator = new BitOperator();
        byte[] headbytes = this.msgHeader.getHeaderbytes();
    //    byte[] bodybytes = getMessageBodybytes();

        byte[] check = new byte[]{(byte) (bitOperator.getCheckSum4JT808(headbytes, 0, headbytes.length))};
        byte[] flag = new byte[]{TPMSConsts.pkg_delimiter};
        byte[] sendbytes = bitOperator.concatAll(flag, headbytes, check, flag);
        return sendbytes;
    }
}
