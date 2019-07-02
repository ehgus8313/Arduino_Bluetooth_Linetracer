package com.example.kwon.a2019bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.gson.Gson;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BTconectActivity extends AppCompatActivity{

    private BluetoothSPP bt;
    LinearLayout lay,hidelay1,hidelay2;
    ImageView bluet;
    int mCount2;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView5;

    JoyStickClass js;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconect);
        textView5 = (TextView)findViewById(R.id.textView5);
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        textView5.setText("Direction : Up");
                        bt.send("F", false);
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                        bt.send("D", false);
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                        bt.send("R", false);
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                        bt.send("K", false);
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                        bt.send("B", false);
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                        bt.send("E", false);
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                        bt.send("L", false);
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                        bt.send("V", false);
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Stop");
                        bt.send("S", false);
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView5.setText("Direction :");
                }
                return true;
            }
        });

        final TextView tv = (TextView)findViewById(R.id.status);
        SeekBar sb  = (SeekBar) findViewById(R.id.seekBar1);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tv.setText("속도 단계 : " + progress);
                if(progress==1){
                    bt.send("R", false);
                }if(progress==2){
                    bt.send("N", false);
                }if(progress==3){
                    bt.send("O", false);
                }

            }
        });


        bluet = (ImageView)findViewById(R.id.bluetoothdc);
        bt = new BluetoothSPP(this); //Initializing


        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth를 사용할 수 없습니다"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(BTconectActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "스마트카 " + name + "\n" + address + "에 연결 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                bluet.setImageResource(R.drawable.bluetooth);
                hidelay2.setBackgroundResource(R.drawable.mainback);
                TextView name1 = (TextView)findViewById(R.id.doorlockname);
                name1.setText("차이름 " +name);
                mCount2=1;
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                bluet.setImageResource(R.drawable.bluetoothdc);
                hidelay2.setBackgroundResource(R.drawable.mainback2);
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
                bluet.setImageResource(R.drawable.bluetoothdc);
                hidelay2.setBackgroundResource(R.drawable.mainback2);
            }
        });

        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }




        final ImageButton BTconnect = (ImageButton) findViewById(R.id.BTconnect);
        BTconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

        lay = (LinearLayout)findViewById(R.id.lay);
        hidelay1 = (LinearLayout)findViewById(R.id.hidelay1);
        hidelay2 = (LinearLayout)findViewById(R.id.hidelay2);
        final ImageButton editbutton = (ImageButton) findViewById(R.id.editbutton);
        editbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lay.getVisibility() == View.GONE) {
                    lay.setVisibility(View.VISIBLE); // or GONE
                    hidelay1.setVisibility(View.GONE);
                    hidelay2.setVisibility(View.GONE);
                }
            }
        });
        final Button close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lay.setVisibility(View.GONE);
                hidelay1.setVisibility(View.VISIBLE);
                hidelay2.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}