package com.dn.maptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button btn_flow;
    private Button btn_waterfall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_flow = (Button) findViewById(R.id.btn_flow);
        btn_waterfall = (Button) findViewById(R.id.btn_waterfall);
        btn_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FlowLayoutTestActivity.class));
            }
        });
        btn_waterfall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaterfallLayoutTestActivity.class));
            }
        });

    }

}
