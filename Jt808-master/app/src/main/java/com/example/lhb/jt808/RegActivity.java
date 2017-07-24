package com.example.lhb.jt808;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.lhb.common.MsgHeader;
import com.example.lhb.common.TPMSConsts;

import com.example.lhb.tool.Jt808Client;
import com.example.lhb.util.HexStringUtils;
import com.example.lhb.vo.req.TerminalAuthentication;
import com.example.lhb.vo.req.TerminalCommonResp;
import com.example.lhb.vo.req.TerminalHeartBeat;
import com.example.lhb.vo.req.TerminalLocationReport;
import com.example.lhb.vo.req.TerminalLogOut;
import com.example.lhb.vo.req.TerminalRegisterMsg;
import com.example.lhb.vo.resp.CmdRegisterRespMsg;
import com.example.lhb.vo.resp.ServerCommonRespMsg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegActivity extends AppCompatActivity  implements SensorEventListener {

    public static  RegActivity regActivity;
    AMapLocationClient mLocationClient;
    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private Sensor mag_sensor;
    //加速度传感器数据
    float accValues[]=new float[3];
    //地磁传感器数据
    float magValues[]=new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[]=new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float values[]=new float[3];

    String phoneNum="";
    String auth="";

    Button open;Button select;static JT808SQLite jtsqlite=null;
    Button btn_con;
    Button btn_reg;
    Button btn_heart;
    Button btn_auth;
    Button btn_resp;
    Button btn_logout;
    Button btn_paramresp;
    Button btn_location;
    EditText edit_ip;
    EditText edit_port;
    EditText edit_phone;
    EditText edit_flow;
    EditText edit_captal_id;
    EditText edit_city_id;
    EditText edit_fc_id;
    EditText edit_ter_model;
    EditText edit_ter_id;
    EditText edit_color_id;
    EditText edit_car_num;

    EditText edit_sign;
    EditText edit_state;
    EditText edit_lat;
    EditText edit_lon;
    EditText edit_height;
    EditText edit_speed;
    EditText edit_direction;
    EditText edit_time;
    EditText edit_attachId;
    EditText edit_attachLength;
    EditText edit_attachMsg;
   // private String rebackAuthMsg="313131313131";//可能会出项空指针异常
    private String rebackAuthMsg="";//可能会出项空指针异常
    MsgHeader header;
    double lon;
    double lat;

    MsgHeader reg_header;
    int reg_provinceid=0;
    int reg_cityid=0;
    String reg_fcid="";
    String reg_terpe="";
    String reg_terid="";
    int reg_color=0;
    String reg_carnum="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        edit_phone = (EditText) findViewById(R.id.edit_phone_id);
        edit_flow = (EditText) findViewById(R.id.edit_flow_id);
        edit_captal_id = (EditText) findViewById(R.id.edit_cp_id);
        edit_city_id = (EditText) findViewById(R.id.edit_city_id);
        edit_fc_id = (EditText) findViewById(R.id.edit_fc_id);
        edit_ter_model = (EditText) findViewById(R.id.edit_ter_model);
        edit_ter_id = (EditText) findViewById(R.id.edit_ter_id);
        edit_color_id = (EditText) findViewById(R.id.edit_color_id);
        edit_car_num = (EditText) findViewById(R.id.edit_car_num);
        btn_con=(Button)findViewById(R.id.btn_con);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_heart=(Button)findViewById(R.id.btn_heart);
        btn_auth=(Button)findViewById(R.id.btn_auth);
        btn_resp=(Button)findViewById(R.id.btn_resp);
        btn_location=(Button)findViewById(R.id.btn_location);
        btn_logout=(Button)findViewById(R.id.btn_logout);
        btn_paramresp=(Button)findViewById(R.id.btn_paramresp);
        open=(Button)findViewById(R.id.opensql);select=(Button)findViewById(R.id.selectsql);

        edit_ip=(EditText)findViewById(R.id.edit_ip);
        edit_port=(EditText)findViewById(R.id.edit_port);
        edit_sign=(EditText)findViewById(R.id.edit_sign);
        edit_state=(EditText)findViewById(R.id.edit_state);
        edit_lat=(EditText)findViewById(R.id.edit_lat);
        edit_lon=(EditText)findViewById(R.id.edit_lon);
        edit_height=(EditText)findViewById(R.id.edit_height);
        edit_speed=(EditText)findViewById(R.id.edit_speed);
        edit_direction=(EditText)findViewById(R.id.edit_direction);

        edit_time=(EditText)findViewById(R.id.edit_time);
        edit_attachId=(EditText)findViewById(R.id.edit_attachId);
        edit_attachLength=(EditText)findViewById(R.id.edit_attachLength);
        edit_attachMsg=(EditText)findViewById(R.id.edit_attachMsg);

        getLonLat();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        final Intent intent = new Intent(RegActivity.this, Jt808Service.class);
        byte[]b=null;
        intent.putExtra("data",b);
        startService(intent);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //给传感器注册监听：
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mag_sensor,SensorManager.SENSOR_DELAY_GAME);
        header=new MsgHeader(0,false,edit_phone.getText().toString(),Integer.parseInt(edit_flow.getText().toString()));

        regActivity=this;

        btn_con.setOnClickListener(new View.OnClickListener() {
            String ip=edit_ip.getText().toString();
            int port=Integer.parseInt(edit_port.getText().toString());

            @Override
            public void onClick(View v) {
                Jt808Service.client.close();
                if(Jt808Service.client.isClosed()) {

                    new Thread()
                    {
                        public void run() {
                            Jt808Service.client = new Jt808Client(ip,port);
                            Jt808Service.client.start();
                      //      Toast.makeText(regActivity,"重新连接成功",Toast.LENGTH_SHORT).show();
                        }
                    }.start();
                    Toast.makeText(regActivity,"重新连接成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Jt808Service.client.setHandler(handler);
                header=new MsgHeader(0,false,edit_phone.getText().toString(),Integer.parseInt(edit_flow.getText().toString()));


                int provinceid = Integer.parseInt(edit_captal_id.getText().toString());
                int cityid = Integer.parseInt(edit_city_id.getText().toString());

                final String phone = edit_phone.getText().toString();
                int flow = Integer.parseInt(edit_flow.getText().toString());
                String fcid = edit_fc_id.getText().toString();
                String tertype = edit_ter_model.getText().toString();
                String terid = edit_ter_id.getText().toString();
                int color = Integer.parseInt(edit_color_id.getText().toString());
                String carnum = edit_car_num.getText().toString();
               final TerminalRegisterMsg terminalRegisterMsg = new TerminalRegisterMsg(header,
                        provinceid, cityid, fcid, tertype, terid, color, carnum);
                reg_header=header;reg_provinceid=provinceid;reg_cityid=cityid;reg_fcid=fcid;reg_terpe=tertype;reg_terid=terid;reg_carnum=carnum;
                new Thread()
               {
                   public void run()
                   {
                       try {
                             Jt808Service.client.registerTerminal(terminalRegisterMsg);
                           Log.i("tt","sss");
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }.start();
            }
        });

        btn_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jt808Service.client.setHandler(handler);
                String phone = edit_phone.getText().toString();
                int flow = Integer.parseInt(edit_flow.getText().toString());

                final TerminalHeartBeat terminalHeartBeat=new TerminalHeartBeat(0,0,false,phone,flow,0,0);
                new Thread()
                {
                    public void run()
                    {
                        try{
                            Jt808Service.client.heartbeatTerminal(terminalHeartBeat);
                        }catch (IOException e){

                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jt808Service.client.setHandler(handler);
                final String phone = edit_phone.getText().toString();
                int flow = Integer.parseInt(edit_flow.getText().toString());

                final TerminalAuthentication terminalAuthentication=new TerminalAuthentication(0,false,phone,flow,0,0,rebackAuthMsg);
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            phoneNum=phone;
                            auth=rebackAuthMsg;
                            Jt808Service.client.authenticationTerminal(terminalAuthentication);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Jt808Service.client.setHandler(handler);

                new Thread()
                {
                    public  void run()
                    {
                        while (true)
                        {
                            long sign=Long.parseLong(edit_sign.getText().toString());
                            long state=Long.parseLong(edit_state.getText().toString());
                            long lat=(long) (Double.parseDouble(edit_lat.getText().toString())*1000000);
                            long lon=(long)(Double.parseDouble(edit_lon.getText().toString())*1000000);
                            int height=(int)Float.parseFloat(edit_height.getText().toString());
                            int speed=(int)Float.parseFloat(edit_speed.getText().toString());
                            int direction=(int)Float.parseFloat(edit_direction.getText().toString());
                            String time=edit_time.getText().toString();
                            int attachId=Integer.parseInt(edit_attachId.getText().toString());
                            int attachLength=Integer.parseInt(edit_attachLength.getText().toString());
                            long attachMsg=Long.parseLong(edit_attachMsg.getText().toString());
                            Jt808Service.client.setHandler(handler);
                            final TerminalLocationReport terminalLocationReport
                                    =new TerminalLocationReport(header,sign,state,lat,lon,height,speed,direction,time,attachId,attachLength,attachMsg);
                            Log.i("locationbytes","gothere");
                            byte[]sendbytes=terminalLocationReport.getAllBytes();
                            Log.i("locationbytes",HexStringUtils.toHexString(sendbytes));
                            Log.i("locationbytes_length",sendbytes.length+"");
                            Log.i("location",terminalLocationReport.toString());
                            try {

                              //  Jt808Service.client.sendBytes(sendbytes);
                                if(Jt808Service.client.isConnected()){
                                    Log.i("location","ok1");
                                    Jt808Service.client.sendBytes(sendbytes);
                                    Log.i("location","ok3");
                                    Thread.sleep(10000);
                                }
//                                else
//                                {
//                                        Log.e("send","ddddddddddddddd");
//                                        jtsqlite.insertData(getApplicationContext(),sendbytes);
//                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                            finally {
                                if(Jt808Service.client.isClosed())
                                {
                                    Log.e("send","ddddddddddddddd");

                                    jtsqlite.insertData(getApplicationContext(),sendbytes);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btn_location.setEnabled(true);
                                        }
                                    });

                                    break;
                                }

                            }

                        }

                    }
                }.start();
                btn_location.setEnabled(false);

            }
        });

        btn_resp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jt808Service.client.setHandler(handler);
                String phone = edit_phone.getText().toString();
                int flow = Integer.parseInt(edit_flow.getText().toString());
                final TerminalCommonResp terminalCommonResp=new TerminalCommonResp(0,false,phone,flow,0,0,258,0);
                new Thread()
                {
                    public void run()
                    {
                        try{
                            Jt808Service.client.commonRespTerminal(terminalCommonResp);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jt808Service.client.setHandler(handler);
                String phone = edit_phone.getText().toString();
                int flow = Integer.parseInt(edit_flow.getText().toString());
                final TerminalLogOut terminalLogOut=new TerminalLogOut(0,0,false,phone,flow,0,0);

                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Jt808Service.client.logOutTerminal(terminalLogOut);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        jtsqlite=new JT808SQLite();
        open.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
             //   jtsqlite.openDatabase(getApplicationContext());
                new Thread()
                {
                    public void run()
                    {
                        Log.e("zhang","san");
                    }
                }.start();
            }
        });
        select.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                List<byte[]>arr=jtsqlite.getList(getApplicationContext());
                for(int i=0;i<arr.size();i++)
                {
                    Log.e("arr"+i,HexStringUtils.toHexString(arr.get(i)));
                    Toast.makeText(regActivity,HexStringUtils.toHexString(arr.get(i)),Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });


    }

    public void getLonLat()
    {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                if (aMapLocation != null) {

                    if (aMapLocation.getErrorCode() == 0) {

                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表


                        aMapLocation.getAccuracy();//获取精度信息

                        lat=(aMapLocation.getLatitude());//获取纬度
                        lon=(aMapLocation.getLongitude());
                        long time =aMapLocation.getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.valueOf(time));
                        SimpleDateFormat df=new SimpleDateFormat("yyMMddHHmmss");
                        String datestring = df.format(calendar.getTime());
                        edit_height.setText(aMapLocation.getAltitude()+"");
                        edit_speed.setText(aMapLocation.getSpeed()+"");
                        edit_time.setText(datestring);
                        edit_lat.setText(lat+"");
                        edit_lon.setText(lon+"");

                    } else {

                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.i("mylog","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());

                        mLocationClient.stopLocation();
                    }
                }
            }
        });
    }
    Handler handler=new Handler()//处理返回消息
    {
        @Override
        public void handleMessage(Message msg) {

            Bundle data=msg.getData();
            byte[]b=data.getByteArray("data");
            String str=data.getString("data");
            String hexString="";
            String hex2Byte="";
            switch (msg.what)
            {

                case TPMSConsts.cmd_terminal_register_resp:// 终端注册应答
                    CmdRegisterRespMsg cmdRegisterRespMsg=new CmdRegisterRespMsg(b);

                    Toast.makeText(RegActivity.this,"终端注册回应："+cmdRegisterRespMsg.toString(),Toast.LENGTH_SHORT).show();
                    rebackAuthMsg=cmdRegisterRespMsg.getAuthCode();
                    break;
                case TPMSConsts.msg_id_terminal_location_info_upload://位置汇报回复
                    Log.e("locaRESP",hexString);
                    break;

                case TPMSConsts.cmd_common_resp:// 平台通用应答

                    ServerCommonRespMsg serverCommonRespMsg=new ServerCommonRespMsg(b);
                    Toast.makeText(RegActivity.this,serverCommonRespMsg.toString(),Toast.LENGTH_LONG).show();
                    hexString=HexStringUtils.toHexString(b);
                    Log.i("cmd_common_resp","平台通用应答："+hexString+"");
                    break;
                default:
                    hexString=HexStringUtils.toHexString(b);
                    Toast.makeText(RegActivity.this,"平台回应"+hexString,Toast.LENGTH_SHORT).show();
            }

        }
    };


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accValues=event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magValues=event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        /**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
         * 填充旋转数组r
         * r：要填充的旋转数组
         * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
         * gravity:加速度传感器数据
         * geomagnetic：地磁传感器数据
         */
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        /**
         * public static float[] getOrientation (float[] R, float[] values)
         * R：旋转数组
         * values ：模拟方向传感器的数据
         */

        SensorManager.getOrientation(r, values);


        //将弧度转化为角度后输出
        StringBuffer buff=new StringBuffer();
       /* for(float value:values){
            value=(float) Math.toDegrees(value);
            buff.append(value+"  ");
        }*/
        edit_direction.setText(values[0]+"");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String getAuth()
    {
        return auth;
    }
    public String getPhoneNum()
    {
        return phoneNum;
    }

    public MsgHeader getHeader(){return header;}
    public int getReg_provinceid(){return reg_provinceid;}
    public int getReg_cityid(){return reg_cityid;}
    public String getReg_fcid(){return reg_fcid;}
    public String getReg_terpe(){return reg_terpe;}
    public String getReg_terid(){return reg_terid;}
    public int getReg_color(){return reg_color;}
    public String getReg_carnum(){return reg_carnum;}


}
