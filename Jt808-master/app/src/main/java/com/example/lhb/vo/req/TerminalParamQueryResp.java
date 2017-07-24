package com.example.lhb.vo.req;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class TerminalParamQueryResp extends MsgFrame {



    //应答流水号 WORD
    protected int flowId;
    //应答参数个数    BYTE
    protected int respParamCount;

    //参数总数  BYTE
    protected int paramCount;



    public TerminalParamQueryResp(int msgbodylength, int encryptiontype, boolean issubpackage, String terminalphone,
                                  int flowed, int totalsubpackage, int seq)
    //消息体长度，加密方式，分包，电话，流水号，包总数，包序号
    {
        this.msgHeader=new MsgHeader();
        msgHeader.setMsgId(TPMSConsts.msg_id_terminal_param_query_resp);
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
}
