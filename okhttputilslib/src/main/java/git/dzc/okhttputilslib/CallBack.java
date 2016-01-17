package git.dzc.okhttputilslib;


import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by dzc on 15/12/11.
 */
public abstract class Callback implements com.squareup.okhttp.Callback {
    public void onStart(){

    }
    public void onFinish(){

    }

    public abstract void onFailure(Request request, IOException e);

    public abstract void onResponse(Response response) throws IOException;
}
