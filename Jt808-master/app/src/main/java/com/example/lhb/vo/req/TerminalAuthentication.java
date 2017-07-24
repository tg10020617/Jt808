package com.example.lhb.vo.req;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;
import com.example.lhb.util.HexStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class TerminalAuthentication extends MsgFrame{

    private String authenticationMsg;

    public TerminalAuthentication(MsgHeader header,String authenticationmsg)
    {
        setMsgHeader(header);
        setAuthenticationMsg(authenticationmsg);
    }
    public TerminalAuthentication(int encryptiontype,boolean issubpackage,String terminalphone,
                                  int flowed,int totalsubpackage,int seq,String authenticationmsg)
                            //加密方式，分包，电话，流水号，包总数，包序号，鉴权码
    {
        this.msgHeader=new MsgHeader();

        msgHeader.setMsgId(TPMSConsts.msg_id_terminal_authentication);
        int msgbodylength=authenticationmsg.getBytes().length/2;
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

        setAuthenticationMsg(authenticationmsg);
    }

    public String getAuthenticationMsg()
    {
        return authenticationMsg;
    }
    public void setAuthenticationMsg(String msg)
    {
        this.authenticationMsg=msg;
    }

    public byte[] getMessageBodybytes() {
        byte[] aumsg= HexStringUtils.decodeHex2(this.getAuthenticationMsg().toCharArray());//string->byte[]
        return aumsg;
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
