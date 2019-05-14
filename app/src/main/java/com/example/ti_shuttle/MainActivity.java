package com.example.ti_shuttle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Physicaloid mPhysicaloid; // initialising library
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhysicaloid = new Physicaloid(this);
        mPhysicaloid.setBaudrate(115200);
        mPhysicaloid.setDataBits(8);
        mPhysicaloid.setParity(0);
        mPhysicaloid.setStopBits(1);
        t = findViewById(R.id.t);
    }

    public void onOpenClicked(View v) {
        if (mPhysicaloid.open()) {

            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf, size);
                    t.setText(Arrays.toString(buf));
                }
            });
        } else {
            Toast.makeText(this, "Cannot open", Toast.LENGTH_LONG).show();
        }
    }

    public void onCloseClicked(View v) {
        if (mPhysicaloid.close()) {
            mPhysicaloid.clearReadListener();
        }
    }

}
