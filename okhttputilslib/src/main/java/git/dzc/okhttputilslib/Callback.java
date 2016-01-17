package git.dzc.okhttputilslib;



import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dzc on 15/12/11.
 */
public abstract class Callback implements okhttp3.Callback {
    public void onStart(){

    }
    public void onFinish(){

    }

    public abstract void onFailure(Request request, IOException e);

    public abstract void onResponse(Response response) throws IOException;
}
