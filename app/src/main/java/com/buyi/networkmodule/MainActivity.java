package com.buyi.networkmodule;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.buyi.network.core.NetRequest;
import com.buyi.network.core.URLConnectionImpl;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetRequest request = new NetRequest();
                request.url = "http://api.jikexueyuan.com/v3/activity/banner?";
                Map<String, String> params = new HashMap<>();
                params.put("type", "2");
                params.put("debug", "ADubaC");
                request.getParams = params;
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", String.format("%s/%s (Linux; Android %s; %s Build/%s)", "buyi_network", "1.0", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
//                headers.put("Cache-control", "");
                request.headers = headers;
                URLConnectionImpl impl = new URLConnectionImpl();
                impl.httpGet(request);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
