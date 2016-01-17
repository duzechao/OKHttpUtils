package git.dzc.okhttputils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.io.IOException;

import git.dzc.okhttputilslib.CacheType;
import git.dzc.okhttputilslib.JsonCallback;
import git.dzc.okhttputilslib.OKHttpUtils;
import git.dzc.okhttputilslib.OkHttpRequest;

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
                getDatByRequest();
            }
        });
    }

    private void fail(final Exception e){
        tv5.post(new Runnable() {
            @Override
            public void run() {
                tv5.setText("onFailure  "+e.toString());
            }
        });
    }
    private void getDatByRequest(){
        OkHttpRequest request = new OkHttpRequest.Builder().maxCacheAge(5).cacheType(CacheType.CACHED_ELSE_NETWORK).url(url).build();
        okHttpUtils.get(request, new JsonCallback<DateModule>() {
            @Override
            public void onFailure(Request request, Exception e) {
                fail(e);
            }

            @Override
            public void onResponse(final DateModule object) throws IOException {
                if(object!=null){
                    tv5.post(new Runnable() {
                        @Override
                        public void run() {
                            tv5.setText(object.getResult().getDatetime_2());
                        }
                    });
                }else{
                    fail(new Exception("object==null"));
                }

            }
        });

    }

    private String url = "http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
    private void getData(final CacheType cacheType){
        okHttpUtils.get(url, cacheType, MainActivity.this,new JsonCallback<DateModule>() {


            @Override
            public void onStart() {
                Log.d(TAG,"onStart");
            }

            @Override
            public void onFinish() {
                Log.d(TAG,"onFinish");
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.d(TAG,"onFailure");
                fail(e);
            }

            @Override
            public void onResponse(final DateModule object) throws IOException {
                Log.d(TAG,"onResponse");
                if(object!=null){
                    tv5.post(new Runnable() {
                        @Override
                        public void run() {
                            tv5.setText(object.getResult().getDatetime_2());
                        }
                    });
                }else{
                    fail(new Exception("object==null"));
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cancel all request
        okHttpUtils.cancel(MainActivity.this);
    }
}
