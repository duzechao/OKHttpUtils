package git.dzc.okhttputils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import git.dzc.okhttputilslib.CacheType;
import git.dzc.okhttputilslib.JsonCallback;
import git.dzc.okhttputilslib.OKHttpUtils;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.this.getClass().getSimpleName();

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;

    private OKHttpUtils okHttpUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpUtils = new OKHttpUtils.Builder(this).build();

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.ONLY_NETWORK);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.ONLY_CACHED);

            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.NETWORK_ELSE_CACHED);
            }
        });

        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(CacheType.CACHED_ELSE_NETWORK);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder().url(url).cacheControl(new CacheControl.Builder().maxAge(5, TimeUnit.SECONDS)
                        .maxStale(5,TimeUnit.SECONDS).build()).build();
                okHttpUtils.request(request, CacheType.ONLY_NETWORK, jsonCallback);

            }
        });
    }

    private String url = "http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";


    private void getData(@CacheType int cacheType){
        tv5.setText("");
        Request request = new Request.Builder().url(url).build();
        okHttpUtils.request(request, cacheType, jsonCallback);
    }
    private JsonCallback<DateModule> jsonCallback = new JsonCallback<DateModule>() {
        @Override
        public void onFailure(Call call, Exception e) {
            onFail(e);
        }

        @Override
        public void onResponse(Call call, final DateModule object) throws IOException {
            if(object!=null){
                tv5.post(new Runnable() {
                    @Override
                    public void run() {
                        tv5.setText(object.getResult().getDatetime_1());
                    }

                });
            }
        }
    };

    private void onFail(final Exception e){
        tv5.post(new Runnable() {
            @Override
            public void run() {
                tv5.setText(e.toString());
            }
        });
    }
}
