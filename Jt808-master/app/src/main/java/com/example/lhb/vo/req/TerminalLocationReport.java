package com.example.lhb.vo.req;

import android.util.Log;

import com.example.lhb.common.MsgFrame;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;
import com.example.lhb.util.BCD8421Operater;
import com.example.lhb.util.BitOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhb on 2017/5/24.
 */

public class TerminalLocationReport extends MsgFrame{
    public  TerminalLocationReport()
    {
    }
    /**
     *
     *
    位置基本信息
     */
    long sign;//报警标志
    long state;//状态
    long lat;//维度
    long lon;//经度
    long height;//海拔
    long speed;//速度
    long direction;//方向
    String time;//YY-MM-DD-hh-mm-ss(GMT+8时间)
    /**************************************************/


    /**
     *
     * 位置附加信息项
     */
    int attachId;//附加信息ID
    int attachLength;//附加信息长度
    long attachMsg;//附加信息

    /**
     附加信息ID说明
     */
    public  final  int mileage_record=0x01;//里程
    public   final  int oil_record=0x02;//油量
    public    final int speed_record=0x03;//行驶记录功能获取的速度
    public   final int man_record=0x04;//需要人工确认的报警时间ID
    /*0x05-0x10保留*/
    public   final  int out_speed=0x11;//超速
    public  final  int ioarea_routealarm=0x12;//进出区域/路线报警
    public  final  int route_time=0x13;//路段行驶时间不足/过长
    /*0x14-0x24保留*/
    public   final  int extend_state=0x25;//扩展车辆信号状态位
    public   final  int io_state=0x2a;//IO状态位
    public    final  int analog=0x2b;//模拟量
    public    final  int net_stength=0x30;//网络强度
    public    final int gnss_num=0x31;//gnss卫星数
    public   final  int follow_lenth=0xe0;//后续信息长度
    public    final  int custom_area=0xe1;//自定义区域

    /**/


/***************************************************************/


    public TerminalLocationReport(byte[]bytes)
    {
        super(bytes);
        BitOperator bitOperator =new BitOperator();
        BCD8421Operater bcd8421Operater=new BCD8421Operater();
        byte[]body=getBodyBytes(bytes);

        /**
         *
         * 位置基本信息
         */
        byte[]sign_bytes=bitOperator.splitBytes(body,0,3);
        byte[]state_bytes=bitOperator.splitBytes(body,4,7);
        byte[]lat_bytes=bitOperator.splitBytes(body,8,11);
        byte[]lon_bytes=bitOperator.splitBytes(body,12,15);
        byte[]height_bytes=bitOperator.splitBytes(body,16,17);
        byte[]speed_bytes=bitOperator.splitBytes(body,18,19);
        byte[]direction_bytes=bitOperator.splitBytes(body,20,21);
        byte[]time_bytes=bitOperator.splitBytes(body,22,27);
        this.setSign(bitOperator.bytes2Long(sign_bytes));
        this.setState(bitOperator.bytes2Long(state_bytes));
        this.setLat(bitOperator.bytes2Long(lat_bytes));
        this.setLon(bitOperator.bytes2Long(lon_bytes));
        this.setHeight(bitOperator.byteToInteger(height_bytes));
        this.setSpeed(bitOperator.byteToInteger(speed_bytes));
        this.setDirection(bitOperator.byteToInteger(direction_bytes));
        this.setTime(bcd8421Operater.bcd2String(time_bytes));
        /**
         * 位置附加信息项
         */
        byte[]attachMsg_bytes=bitOperator.splitBytes(body,30,body.length-1);
        this.setAttachId(body[28]);
        this.setAttachLength(body[29]);
        this.setAttachMsg(bitOperator.bytes2Long(attachMsg_bytes));
    }

    /**
     *
     * @header消息头
     * @param sign
     * @param state
     * @param lat
     * @param lon
     * @param height
     * @param speed
     * @param direction
     * @param time
     * @param attachId
     * @param attachLength
     * @param attachMsg
     */
    public TerminalLocationReport(MsgHeader header,long sign, long state,
                                  long lat, long lon, int height,
                                  int speed, int direction, String time,
                                  int attachId, int attachLength,
                                  long attachMsg)
    {
        header.setMsgId(TPMSConsts.msg_id_terminal_location_info_upload);
        setSign(sign);
        setState(state);
        setLat(lat);
        setLon(lon);
        setHeight(height);
        setSpeed(speed);
        setDirection(direction);
        setTime(time);
        setAttachId(attachId);
        setAttachLength(attachLength);
        setAttachMsg(attachMsg);
        int bodylenth=28+attachLength+2;

        header.setMsgBodyLength(bodylenth);
        this.setMsgHeader(header);
    }
    public  byte[]getMessageBodybytes()
    {
        BitOperator bitOperator=new BitOperator();
        BCD8421Operater bcd8421Operater=new BCD8421Operater();
        List<byte[]>body_list=new ArrayList<>();
        body_list.add(bitOperator.numToByteArray(sign,4));
        body_list.add(bitOperator.numToByteArray(state,4));
        body_list.add(bitOperator.numToByteArray(lat,4));
        body_list.add(bitOperator.numToByteArray(lon,4));
        body_list.add(bitOperator.numToByteArray(height,2));
        body_list.add(bitOperator.numToByteArray(speed,2));
        body_list.add(bitOperator.numToByteArray(direction,2));
        body_list.add(bcd8421Operater.string2Bcd(time));
        Log.i("timelength",bcd8421Operater.string2Bcd(time).length+"");
        body_list.add(new byte[]{(byte)attachId});
        body_list.add(new byte[]{(byte)attachLength});
        body_list.add(bitOperator.numToByteArray(attachMsg,attachLength));
        return bitOperator.concatAll(body_list);
    }
    public byte[]getAllBytes()
    {
        BitOperator bitOperator=new BitOperator();
        byte[]headbytes=this.msgHeader.getHeaderbytes();
        byte[]bodybytes=this.getMessageBodybytes();

        byte[]flag={(byte)TPMSConsts.pkg_delimiter};
        Log.i("bodybytesLength",bodybytes.length+"");
        byte[]srcbytes=bitOperator.concatAll(headbytes,bodybytes);
        byte []check=new byte[]{(byte) bitOperator.getCheckSum4JT808(srcbytes,0,srcbytes.length)};
        byte []sendbytes=bitOperator.concatAll(flag,srcbytes,check,flag);
        return sendbytes;
    }

    public void setSign(long sign) {
        this.sign = sign;
    }

    public void setState(long state) {
        this.state = state;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public void setDirection(long direction) {
        this.direction = direction;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAttachId(int attachId) {
        this.attachId = attachId;
    }

    public void setAttachLength(int attachLength) {
        this.attachLength = attachLength;
    }

    public void setAttachMsg(long attachMsg) {
        this.attachMsg = attachMsg;
    }
    public long getSign() {
        return sign;
    }

    public long getState() {
        return state;
    }

    public long getLat() {
        return lat;
    }

    public long getLon() {
        return lon;
    }

    public long getHeight() {
        return height;
    }

    public long getSpeed() {
        return speed;
    }

    public long getDirection() {
        return direction;
    }

    public String getTime() {
        return time;
    }

    public int getAttachId() {
        return attachId;
    }

    public int getAttachLength() {
        return attachLength;
    }

    public long getAttachMsg() {
        return attachMsg;
    }

    public  String toString()
    {
        return msgHeader.toString()+"body[sign:"+sign+",state:"+state+",lat:"
                +lat+",lon:"+lon+",height:"+height+",speed:"+speed+",direction:"+direction+",time"+time
                +",attachId:"+attachId+",attachLength:"+attachLength+",attachMsg:"+attachMsg;
    }

}
