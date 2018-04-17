package com.zhangqie.bluetooth.demo1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangqie.bluetooth.R;

import java.util.Set;

/**
 * Created by zhangqie on 2017/11/28.
 *
 * 蓝牙搜索
 */

public class Demo1Activity extends AppCompatActivity implements View.OnClickListener{


    private static final int REQUEST_ENABLE = 1;

    private static final String TAG = Demo1Activity.class.getName();


    BluetoothAdapter mBluetoothAdapter;

    TextView tvDevices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo1);
        initView();
    }

    private void initView(){

        findViewById(R.id.btn1).setOnClickListener(this);
        tvDevices = (TextView) findViewById(R.id.textblue);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
            //不做提示，直接打开，不建议用下面的方法，有的手机会有问题。
            // mBluetoothAdapter.enable();
         }

        showBluetooth();

    }

    private void startSearthBltDevice() {
        //如果当前在搜索，就先取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //开启搜索
        mBluetoothAdapter.startDiscovery();
    }


    private void showBoradCast(){
        // 设置广播信息过滤
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);//每搜索到一个设备就会发送一个该广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//当全部搜索完后发送该广播
        filter.setPriority(Integer.MAX_VALUE);//设置优先级
        // 注册蓝牙搜索广播接收者，接收并处理搜索结果
        this.registerReceiver(receiver, filter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                showBoradCast();
                startSearthBltDevice();
                break;
        }
    }

    //获取已经配对的蓝牙设备
    private void showBluetooth(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                tvDevices.append(device.getName() + ":" + device.getAddress());
            }
        }
    }


    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.i(TAG, ":"+ device.getAddress());
                    tvDevices.append(device.getName() + ":"+ device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Toast.makeText(Demo1Activity.this,"已搜索完成",Toast.LENGTH_LONG).show();

                //已搜素完成
            }
        }
    };

}
