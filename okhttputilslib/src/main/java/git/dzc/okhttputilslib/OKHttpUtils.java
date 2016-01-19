package git.dzc.okhttputilslib;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import okio.Source;

/**
 * Created by dzc on 16/1/20.
 * 更新okhttp3.0版本  由于3.0一些改动较大 所以去掉了之前重载的很多函数  只有request和requestAsync这两个方法
 * 去掉了在拦截器添加缓存时间的设置  可在Request方法中设置单个请求
 * ONLY_CACHED和CACHED_ELSE_NETWORK会直接查询缓存，无论设置的max-age是多长
 */
public class OKHttpUtils{
    private boolean DEBUG = true;
    private OkHttpClient client = null;
    private Gson gson;
    private int cacheType = CacheType.NETWORK_ELSE_CACHED;
    private MediaType mediaType = MultipartBody.FORM;

    public  OkHttpClient getClient(){
        return client;
    }
    private GzipRequestInterceptor gzipRequestInterceptor = new GzipRequestInterceptor();

    private OKHttpUtils() {
    }
    private OKHttpUtils(Context context, int maxCacheSize, File cachedDir, @CacheType int cacheType , List<Interceptor> netWorkinterceptors, List<Interceptor> interceptors, boolean isGzip, long timeOut, boolean debug){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        this.DEBUG = debug;
        this.cacheType = cacheType;
        this.gson = new Gson();
        if(cachedDir!=null){
            clientBuilder.cache(new Cache(cachedDir,maxCacheSize));
        }else{
            clientBuilder.cache(new Cache(context.getCacheDir(),maxCacheSize));
        }
//        Interceptor cacheInterceptor = new Interceptor() {
//            @Override public Response intercept(Chain chain) throws IOException {
//                Response originalResponse = chain.proceed(chain.request());
//                return originalResponse.newBuilder()
//                        .removeHeader("Pragma")
//                        .header("Cache-Control", String.format("max-age=%d", maxCacheAge))
//                        .build();
//            }
//        };
        if(isGzip){
            if(!clientBuilder.interceptors().contains(gzipRequestInterceptor)){
                clientBuilder.addInterceptor(new GzipRequestInterceptor());
            }
        }
//        clientBuilder.addNetworkInterceptor(cacheInterceptor);
        if(netWorkinterceptors!=null && !netWorkinterceptors.isEmpty()){
            clientBuilder.networkInterceptors().addAll(netWorkinterceptors);
        }
        if(interceptors!=null && !interceptors.isEmpty()){
            clientBuilder.interceptors().addAll(interceptors);
        }
        clientBuilder.connectTimeout(timeOut, TimeUnit.MILLISECONDS);
        client = clientBuilder.build();
    }

    public OKHttpUtils initDefault(Context context){
        return  new Builder(context).build();
    }

    public RequestBody createRequestBody(Map<String,String> params){
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(mediaType);
        if(params!=null&&!params.isEmpty()){
            Set<String> keys = params.keySet();
            for(String key:keys){
                multipartBuilder.addFormDataPart(key,params.get(key));
            }
        }
        return multipartBuilder.build();
    }


    public  RequestBody createFormRequestBody(Map<String,String> params,String encodedKey,String encodedValue){
        FormBody.Builder builder = new FormBody.Builder();
        if(params!=null&&!params.isEmpty()){
            Set<String> keys = params.keySet();
            for(String key:keys){
                builder.add(key,params.get(key));
            }
        }
        if(!TextUtils.isEmpty(encodedKey) && !TextUtils.isEmpty(encodedValue)){
            builder.addEncoded(encodedKey,encodedValue);
        }
        return builder.build();
    }
    public  RequestBody createFormRequestBody(Map<String,String> params){
        return createFormRequestBody(params);
    }

    /**
     * Callback Call always is null
     * @param request
     * @param callback
     */
    private void requestFromCache(Request request,Callback callback)  {
        Response response = client.cache().get(request);
        if(callback!=null){
            callback.onStart();
            try {
                callback.onResponse(null,response);
            } catch (IOException e) {
//                e.printStackTrace();
                callback.onFailure(null,e);
            }
            callback.onFinish();
        }

    }
    private void requestFromNetwork(Request request, final Callback callback){
        if(callback!=null){
            callback.onStart();
            Call call = client.newCall(request);
            call.enqueue(callback);
        }
    }

    public  void request(final Request request, @CacheType final int cacheType, final JsonCallback callback){
        request(request, cacheType, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(callback!=null){
                    callback.onFailure(call,e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(callback!=null){
                    callback.onResponse(call,  gson.fromJson(response.body().charStream(),callback.getType()));
                }
            }
        });
    }


    public void request(final Request request, @CacheType final int cacheType, final Callback callback){
        switch (cacheType){
            case CacheType.ONLY_NETWORK:
                requestFromNetwork(request,callback);
                break;
            case CacheType.ONLY_CACHED:
                requestFromCache(request,callback);
                break;
            case CacheType.NETWORK_ELSE_CACHED:
                if(callback!=null){
                    callback.onStart();
                }
                requestFromNetwork(request, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        requestFromCache(request,callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if(response.code()==200){
                            if(callback!=null){
                                callback.onResponse(call,response);
                                callback.onFinish();
                            }
                        }else{
                            requestFromCache(request,callback);
                        }
                    }
                });
                break;
            case CacheType.CACHED_ELSE_NETWORK:
                requestFromCache(request, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        requestFromNetwork(request,callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.code()==200){
                            if(callback!=null){
                                callback.onResponse(call,response);
                                callback.onFinish();
                            }
                        }else{
                            requestFromNetwork(request,callback);
                        }
                    }
                });
                break;
        }
    }

    public <T extends Class> T requestAsync(Request request, @CacheType final int cacheType,Class<T> classes) {
            switch (cacheType){
                case CacheType.ONLY_NETWORK:
                    try {
                        return requestFromNetworkAsync(request,classes);
                    } catch (IOException e) {
                        return null;
                    }
                case CacheType.ONLY_CACHED:
                    try {
                        return  requestFromCacheAsync(request,classes);
                    } catch (IOException e) {
                        return null;
                    }
                case CacheType.NETWORK_ELSE_CACHED:
                    try {
                        T result = requestFromNetworkAsync(request,classes);
                        if(request==null){
                            return requestFromCacheAsync(request,classes);
                        }else{
                            return result;
                        }
                    } catch (IOException e) {
                        return null;
                    }

                case CacheType.CACHED_ELSE_NETWORK:
                    try {
                        T result = requestFromCacheAsync(request,classes);
                        if(request==null){
                            return requestFromNetworkAsync(request,classes);
                        }else{
                            return result;
                        }
                    } catch (IOException e) {
                        return null;
                    }
            }
        return null;
    }

    private Response requestFromCacheAsync(Request request)  {
        return client.cache().get(request);
    }
    private Response requestFromNetworkAsync(Request request) throws IOException {
        Call call = client.newCall(request);
        return call.execute();
    }
    private  <T extends Class> T requestFromCacheAsync(Request request,Class<T> classes) throws IOException {
        Response response = client.cache().get(request);
        if(response!=null&&response.code()==200){
            return gson.fromJson(response.body().charStream(),classes);
        }
        return null;
    }
    private   <T extends Class> T requestFromNetworkAsync(Request request, Class<T> classes) throws IOException {
        Response response = client.newCall(request).execute();
        if(response!=null&&response.code()==200){
            return gson.fromJson(response.body().charStream(),classes);
        }
        return null;
    }






    public static class Builder{
        private int maxCachedSize = 5 * 1024 *1024;
        private File cachedDir;
        private Context context;
        private List<Interceptor> networkInterceptors;
        private List<Interceptor> interceptors;
        private int cacheType = CacheType.NETWORK_ELSE_CACHED;
        private boolean isGzip = false;
        private long timeOut = 5000;
        private boolean debug = false;


        public Builder(Context context) {
            this.context = context;
        }

        private Builder() {
        }

        public OKHttpUtils build(){
            return new OKHttpUtils(context,maxCachedSize,cachedDir,cacheType,networkInterceptors,interceptors,isGzip,timeOut,debug);
        }


        public Builder timeOut(long timeOut){
            this.timeOut = timeOut;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }
        public Builder gzip(boolean openGzip) {
            this.isGzip = openGzip;
            return this;
        }

        public Builder cacheType(@CacheType int cacheType){
            this.cacheType = cacheType;
            return this;
        }

        public Builder cachedDir(File cachedDir) {
            this.cachedDir = cachedDir;
            return this;
        }


        /**
         * 拦截器使用可参考这篇文章  <a href="http://www.tuicool.com/articles/Uf6bAnz">http://www.tuicool.com/articles/Uf6bAnz</a>
         * @param interceptors
         */
        public Builder interceptors(List<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public Builder maxCachedSize(int maxCachedSize) {
            this.maxCachedSize = maxCachedSize;
            return this;
        }

        /**
         * 拦截器使用可参考这篇文章  <a href="http://www.tuicool.com/articles/Uf6bAnz">http://www.tuicool.com/articles/Uf6bAnz</a>
         * @param networkInterceptors
         */
        public Builder networkInterceptors(List<Interceptor> networkInterceptors) {
            this.networkInterceptors = networkInterceptors;
            return this;
        }

    }











    public Call uploadFile(String url, File file, Headers headers, UploadListener uploadListener){

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();

        multipartBuilder.addPart(headers,createUploadRequestBody(MultipartBody.FORM,file,uploadListener));
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBuilder.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(uploadListener);
        return call;

    }

    public RequestBody createUploadRequestBody(final MediaType contentType, final File file, final UploadListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        if(listener!=null){
                            listener.onProgress(contentLength(), remaining -= readCount);
                        }
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        };
    }

    static class GzipRequestInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Accept-Encoding","gzip")
//                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), gzip(originalRequest.body()))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override public MediaType contentType() {
                    return body.contentType();
                }

                @Override public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }


    /**
     * okhttp内部默认启用了gzip,此选项是针对需要对post数据进行gzip后发送给服务器的,如服务器不支持,请勿开启
     * @param open
     */
    public void gzip(boolean open){
        if(open){
            if(!client.interceptors().contains(gzipRequestInterceptor)){
                client.interceptors().add(gzipRequestInterceptor);
            }
        }else{
            if(client.interceptors().contains(gzipRequestInterceptor)){
                client.interceptors().remove(gzipRequestInterceptor);
            }
        }
    }

    public void clearCached(){
        try {
            client.cache().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
