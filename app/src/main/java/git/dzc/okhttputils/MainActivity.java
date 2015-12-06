package git.dzc.okhttputils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import git.dzc.okhttputilslib.CacheType;
import git.dzc.okhttputilslib.OKHttpUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;

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
    }


    private void getData(CacheType cacheType){
        okHttpUtils.get("http://graph.luoo.net/index/random?platform=android&version=5.0.0&key=75f2e281092382e903101f2fdd04ecea", cacheType ,new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("failed",e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //  tv.setText(response.body().string());
                Log.d("response", response.toString());
                Log.d("response", response.body().string());
            }
        });
    }
}
