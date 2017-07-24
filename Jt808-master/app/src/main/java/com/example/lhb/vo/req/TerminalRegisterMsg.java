package com.example.lhb.vo.req;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;


import java.util.ArrayList;

import java.util.List;


/**
 * 终端注册消息
 *

 */
public class TerminalRegisterMsg extends MsgFrame {//封装终端注册数据报






    public TerminalRegisterMsg(MsgHeader header) {
        this.msgHeader = header;
    }
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

    /**
     *
     * @param header
     * @param provinceId
     * @param cityId
     * @param fcid
     * @param tertype
     * @param terid
     * @param color
     * @param carnum
     */
    public TerminalRegisterMsg(MsgHeader header,
                               int provinceId,int cityId,String fcid,String tertype,String terid,
                               int color,String carnum)
    {

        header.setMsgId(TPMSConsts.msg_id_terminal_register);//设置消息头id
        int msgbodylenth=37+carnum.getBytes().length-1;
        header.setMsgBodyLength(msgbodylenth);
        setMsgHeader(header);

        this.setProvinceId(provinceId);
        this.setCityId(cityId);
        this.setManufacturerId(fcid);
        this.setTerminalType(tertype);
        this.setTerminalId(terid);
        this.setLicensePlateColor(color);
        this.setLicensePlate(carnum);

    }




    // 省域ID(WORD),设备安装车辆所在的省域，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
    // 0保留，由平台取默认值
    private int provinceId;
    // 市县域ID(WORD) 设备安装车辆所在的市域或县域,市县域ID采用GB/T2260中规定的行 政区划代码6位中后四位
    // 0保留，由平台取默认值
    private int cityId;
    // 制造商ID(BYTE[5]) 5 个字节，终端制造商编码
    private String manufacturerId;
    // 终端型号(BYTE[8]) 八个字节， 此终端型号 由制造商自行定义 位数不足八位的，补空格。
    private String terminalType;//BCD
    // 终端ID(BYTE[7]) 七个字节， 由大写字母 和数字组成， 此终端 ID由制造 商自行定义
    private String terminalId;
    /**
     * 车牌颜色(BYTE) 车牌颜色，按照 JT/T415-2006 的 5.4.12 未上牌时，取值为0<br>
     * 0===未上车牌<br>
     * 1===蓝色<br>
     * 2===黄色<br>
     * 3===黑色<br>
     * 4===白色<br>
     * 9===其他
     */
    private int licensePlateColor;
    // 车牌(STRING) 公安交 通管理部门颁 发的机动车号牌
    private String licensePlate;


    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public int getLicensePlateColor() {
        return licensePlateColor;
    }

    public void setLicensePlateColor(int licensePlate) {
        this.licensePlateColor = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }


    public byte[] getMessageBodybytes() {
        BitOperator bitOperator = new BitOperator();
        List<byte[]> listbyte = new ArrayList<>();
        byte[] provinceid = bitOperator.numToByteArray(this.getProvinceId(), 2);
        byte[] cityid = bitOperator.numToByteArray(this.getCityId(), 2);
        byte[] temp=this.getManufacturerId().getBytes(TPMSConsts.string_charset);
        byte[] fcid =new byte[5];
        int fclen=temp.length;
        if(fclen>5)
            fclen=5;
        System.arraycopy(temp,0,fcid,0,fclen);


        BCD8421Operater bcdoperat=new BCD8421Operater();
        byte[] temp2 = this.getTerminalType().getBytes(TPMSConsts.string_charset);
        int typelen=temp2.length;
        if(typelen>8)typelen=8;
        byte[] terminaltype = new byte[20];
        System.arraycopy(temp2, 0, terminaltype, 0, typelen);
        byte[] terminalid = new byte[7];
        byte[] temp3=this.getTerminalId().getBytes();
        int idlen=temp3.length;
        if(idlen>7)
        {
            idlen=7;
        }

        System.arraycopy(temp3, 0, terminalid, 0, idlen);
        byte[] color = new byte[]{(byte) (this.getLicensePlateColor())};
        byte[] carnum = this.getLicensePlate().getBytes(TPMSConsts.string_charset);

        listbyte.add(provinceid);
        listbyte.add(cityid);
        listbyte.add(fcid);
        listbyte.add(terminaltype);
        listbyte.add(terminalid);
        listbyte.add(color);
        listbyte.add(carnum);

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
