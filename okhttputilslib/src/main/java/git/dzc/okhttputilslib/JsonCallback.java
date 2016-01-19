package git.dzc.okhttputilslib;

import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by dzc on 15/12/10.
 */
public abstract class JsonCallback<T> {
    public abstract void onFailure(Call call, Exception e);
    public abstract void onResponse(Call call,T object) throws IOException;

    public void onStart(){

    }
    public void onFinish(){

    }

    Type getType(){
        Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if(type instanceof Class){
            return type;
        }else{
            return new TypeToken<T>(){}.getType();
        }
    }
}
