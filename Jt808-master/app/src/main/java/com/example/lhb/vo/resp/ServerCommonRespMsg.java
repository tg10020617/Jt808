package com.example.lhb.vo.resp;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.util.BitOperator;
/*

平台通用应答
 */
public class ServerCommonRespMsg extends MsgFrame{

	public static final byte success = 0;
	public static final byte failure = 1;
	public static final byte msg_error = 2;
	public static final byte unsupported = 3;
	public static final byte warnning_msg_ack = 4;

	// byte[0-1] 应答流水号 对应的终端消息的流水号
	private int replyFlowId;
	// byte[2-3] 应答ID 对应的终端消息的ID
	private int replyId;
	/**
	 * 0：成功∕确认<br>
	 * 1：失败<br>
	 * 2：消息有误<br>
	 * 3：不支持<br>
	 * 4：报警处理确认<br>
	 */
	private byte replyCode;

	public ServerCommonRespMsg() {
	}
	public ServerCommonRespMsg(byte[]bytes) {
		super(bytes);
		BitOperator bitOperator=new BitOperator();
		byte[]body=getBodyBytes(bytes);
		byte[]flowId_bytes=bitOperator.splitBytes(body,0,1);
		byte[]replyId_bytes=bitOperator.splitBytes(body,2,3);

		this.setReplyFlowId(bitOperator.byteToInteger(flowId_bytes));
		this.setReplyId(bitOperator.byteToInteger(replyId_bytes));
		this.setReplyCode(body[4]);

	}

	public ServerCommonRespMsg(int replyFlowId, int replyId, byte replyCode) {
		super();
		this.replyFlowId = replyFlowId;
		this.replyId = replyId;
		this.replyCode = replyCode;
	}

	public int getReplyFlowId() {
		return replyFlowId;
	}

	public void setReplyFlowId(int flowId) {
		this.replyFlowId = flowId;
	}

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int msgId) {
		this.replyId = msgId;
	}

	public byte getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(byte code) {
		this.replyCode = code;
	}

	@Override
	public String toString() {
		return msgHeader.toString()+"消息体 [应答流水号=" + replyFlowId + ", 应答id=" + replyId + ", 结果=" + replyCode
				+ "]";
	}

}
