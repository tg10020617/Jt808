package com.example.lhb.common;

import android.util.Log;

import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;

import java.util.ArrayList;
import java.util.List;


public class MsgHeader {

	public MsgHeader()
	{

	}

	/**
	 *
	 *
	 * @encrypttype加密方式
	 * @issubpackage是否分包
	 * @terminalphone终端手机号
	 * @flowid流水号
	 * @totalsubpackage总包数
     * @seq包序号
     */
	public MsgHeader(int encrypttype,boolean issubpackage,
					 String terminalphone,int flowid,int totalsubpackage,int seq)
	{

		setMsgId(msgId);//设置消息头id
		setEncryptionType(encrypttype);
		setHasSubPackage(issubpackage);
		setReservedBit(0);
		setTerminalPhone(terminalphone);
		setFlowId(flowid);
		if(issubpackage)
		{
			setTotalSubPackage(totalsubpackage);
			setSubPackageSeq(seq);
		}
	}

	/**
	 *
	 * @param encrypttype
	 * @param issubpackage
	 * @param terminalphone
     * @param flowid
     */
	public MsgHeader(int encrypttype,boolean issubpackage,
					 String terminalphone,int flowid)
	{

		setMsgId(msgId);//设置消息头id
		setEncryptionType(encrypttype);
		setHasSubPackage(issubpackage);
		setReservedBit(0);
		setTerminalPhone(terminalphone);
		setFlowId(flowid);
	}
	public byte[]getHeaderbytes()
	{
		List<byte[]> listbytes = new ArrayList<>();

		BitOperator bitOperator = new BitOperator();


		listbytes.add(bitOperator.numToByteArray(this.getMsgId(), 2));//消息id
		/***消息属性**/
		int msglength = this.getMsgBodyLength();//消息长度
		//数据加密方式
		int subpackage = 0;
		if (this.isHasSubPackage())
			subpackage = 1;
		int reserve = this.getReservedBit();//保留位
		int msgmodel = (reserve << 14) + (subpackage << 12) + msglength;

		listbytes.add(bitOperator.numToByteArray(msgmodel, 2));//消息属性

		BCD8421Operater bcd8421Operater = new BCD8421Operater();

		byte[] phone = bcd8421Operater.string2Bcd(this.getTerminalPhone());
		listbytes.add(phone);//终端电话

		byte[] flowid = bitOperator.numToByteArray(this.getFlowId(), 2);
		listbytes.add(flowid);//流水号
		//消息包封装项
		if (this.isHasSubPackage()) {
			listbytes.add(bitOperator.numToByteArray(this.getTotalSubPackage(), 2));//消息包总数
			listbytes.add(bitOperator.numToByteArray(this.getTotalSubPackage(), 2));//包序号
		}

		byte[] msghead = bitOperator.concatAll(listbytes);
	//	for(int i=0;i<msghead.length;i++)
	//		Log.i("msghead",msghead[i]+"");
		return msghead;
	}

		// 消息ID
		protected int msgId;

		/////// ========消息体属性
		// byte[2-3]
		protected int msgBodyPropsField;
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

		public int getMsgId() {
			return msgId;
		}

		public void setMsgId(int msgId) {
			this.msgId = msgId;
		}

		public int getMsgBodyLength() {
			return msgBodyLength;
		}

		public void setMsgBodyLength(int msgBodyLength) {
			this.msgBodyLength = msgBodyLength;
		}

		public int getEncryptionType() {
			return encryptionType;
		}

		public void setEncryptionType(int encryptionType) {
			this.encryptionType = encryptionType;
		}

		public String getTerminalPhone() {
			return terminalPhone;
		}

		public void setTerminalPhone(String terminalPhone) {
			this.terminalPhone = terminalPhone;
		}

		public int getFlowId() {
			return flowId;
		}

		public void setFlowId(int flowId) {
			this.flowId = flowId;
		}

		public boolean isHasSubPackage() {
			return hasSubPackage;
		}

		public void setHasSubPackage(boolean hasSubPackage) {

			this.hasSubPackage = hasSubPackage;

		}

		public int getReservedBit() {
			return reservedBit;
		}

		public void setReservedBit(int reservedBit) {
			this.reservedBit = reservedBit;
		}

		public long getTotalSubPackage() {
			return totalSubPackage;
		}

		public void setTotalSubPackage(long totalPackage) {
			this.totalSubPackage = totalPackage;
		}

		public long getSubPackageSeq() {
			return subPackageSeq;
		}

		public void setSubPackageSeq(long packageSeq) {
			this.subPackageSeq = packageSeq;
		}

		public int getMsgBodyPropsField() {
			return msgBodyPropsField;
		}

		public void setMsgBodyPropsField(int msgBodyPropsField) {
			this.msgBodyPropsField =msgBodyPropsField;
		}

		public void setPackageInfoField(int packageInfoField) {
			this.packageInfoField = packageInfoField;
		}

		public int getPackageInfoField() {
			return packageInfoField;
		}

		@Override
		public String toString() {
			return "消息头 [消息ID=" + msgId + ", 消息体长度="
					+ msgBodyLength + ", 加密方式=" + encryptionType + ", 是否分包=" + hasSubPackage
					+ ", 保留位=" + reservedBit + ", 终端手机号=" + terminalPhone + ", 流水号=" + flowId
					+ ", 消息包封装项=" + packageInfoField + ", 包总数=" + totalSubPackage
					+ ", 包序号=" + subPackageSeq + "]\n";
		}


	}


