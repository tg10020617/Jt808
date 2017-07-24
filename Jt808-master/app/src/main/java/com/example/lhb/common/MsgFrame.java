package com.example.lhb.common;

import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class MsgFrame {
    public MsgHeader msgHeader;
    public MsgHeader getHearder()
    {
        return msgHeader;
    }
    public  MsgFrame()
    {

    }
    public  MsgFrame(byte[]bytes)
    {
        msgHeader=new MsgHeader();
        BitOperator bitOperator=new BitOperator();
        BCD8421Operater bcd8421Operater=new BCD8421Operater();

        byte[]msgid_bytes=bitOperator.splitBytes(bytes,1,2);
        byte[]msgbodyprops_bytes=bitOperator.splitBytes(bytes,3,4);


        byte[]phone_bytes=bitOperator.splitBytes(bytes,5,10);
        byte[]flowid_ybtes=bitOperator.splitBytes(bytes,11,12);

        int msgid=bitOperator.byteToInteger(msgid_bytes);
        int msgbodyprops=bitOperator.byteToInteger(msgbodyprops_bytes);

        int reserver=bitOperator.getBitRange(msgbodyprops,14,15);
        int sub=bitOperator.getBitAt(msgbodyprops,13);
        int encryp=bitOperator.getBitRange(msgbodyprops,10,12);
        int length=bitOperator.getBitRange(msgbodyprops,0,9);

        String phone= bcd8421Operater.bcd2String(phone_bytes);
        int flowid=bitOperator.byteToInteger(flowid_ybtes);
        this.msgHeader.setMsgId(msgid);
        this.msgHeader.setMsgBodyPropsField(msgbodyprops);
        this.msgHeader.setReservedBit(reserver);
        boolean is_sub=false;
        if(sub!=0)
        {
            is_sub=true;
            int total=bitOperator.byteToInteger(bitOperator.splitBytes(bytes,13,14));
            int seq=bitOperator.byteToInteger(bitOperator.splitBytes(bytes,15,16));
            this.msgHeader.setTotalSubPackage(total);
            this.msgHeader.setSubPackageSeq(seq);
        }
        this.msgHeader.setTerminalPhone(phone);
        this.msgHeader.setHasSubPackage(is_sub);
        this.msgHeader.setEncryptionType(encryp);
        this.msgHeader.setMsgBodyLength(length);
        this.msgHeader.setFlowId(flowid);
    }
    public byte[]getBodyBytes(byte[]bytes)
    {
        BitOperator bitOperator=new BitOperator();
        if (msgHeader.isHasSubPackage())
        {
            return bitOperator.splitBytes(bytes,17,bytes.length-3);
        }
        else
        {
            return bitOperator.splitBytes(bytes,13,bytes.length-3);
        }
    }
    public  void  setMsgHeader(MsgHeader msgHeader)
    {
        this.msgHeader=msgHeader;
    }
}
