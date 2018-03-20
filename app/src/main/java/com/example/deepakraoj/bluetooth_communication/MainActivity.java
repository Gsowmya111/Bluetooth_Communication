package com.example.deepakraoj.bluetooth_communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    OutputStream os=null;
    EditText et;
    TextView tv;
    Button conn,sen;
    BluetoothAdapter ba=null;
    BluetoothDevice bd=null;
    BluetoothSocket bs=null;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et= (EditText) findViewById(R.id.editText);
        tv= (TextView) findViewById(R.id.tex);
        conn=(Button) findViewById(R.id.con);
        sen=(Button) findViewById(R.id.send);
        ba=BluetoothAdapter.getDefaultAdapter();
        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread t=new Thread()
                {
                    public void run()
                    {
                        if(!ba.isEnabled()) {
                            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(i, 0);
                        }
                            String macId = "00:06:66:64:B2:00";
                            bd=ba.getRemoteDevice(macId);
                            try {
                                bs=bd.createRfcommSocketToServiceRecord(MY_UUID);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                ba.cancelDiscovery();
                                bs.connect();

                                read r=new read();
                                Thread t=new Thread(r);
                                t.start();


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("Bluetooth connected");
                                        Toast.makeText(MainActivity.this, "bluetooth connected", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                };t.start();



            }
        });

        sen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data=et.getText().toString();
                 data=data+"\r\n";
                try {
                    os=bs.getOutputStream();
                    os.write(data.getBytes(),0,data.length());
                    os.flush();

                    final String finalData = data;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText("data sent is "+ finalData);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    class read implements Runnable
    {
        InputStream is=null;
        BufferedReader br=null;

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                try {
                    br = new BufferedReader(new InputStreamReader(bs.getInputStream()));
                    final String incomedata = br.readLine();
                    Log.d("TAG", "inc data " + incomedata);
                    if (incomedata != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(incomedata);
                            }
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
