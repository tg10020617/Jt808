package com.example.lhb.vo.req;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BitOperator;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class TerminalLogOut extends MsgFrame {


    public TerminalLogOut(int msgbodylength,int encryptiontype,boolean issubpackage,String terminalphone,
                          int flowed,int totalsubpackage,int seq)
    //消息体长度，加密方式，分包，电话，流水号，包总数，包序号
    {
        this.msgHeader=new MsgHeader();
        msgHeader.setMsgId(TPMSConsts.msg_id_terminal_log_out);
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
