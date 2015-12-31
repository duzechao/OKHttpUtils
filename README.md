#＃ OKHttpUtils

#如果需要文件下载，请看这个库[DownloadManager](https://github.com/duzechao/DownloadManager)

对OkHttp进行封装，实现了只查询缓存，网络请求失败自动查询本地缓存等功能,结果用Gson解析
支持文件上传进度回调
<br/>支持gzip,可通过gzip(isOpen)来开启或移除，也可通过在Builder自定义的时候开启(由于okhttp默认开启了gzip,
<br/>所以此选项是对发送到服务器的数据进行gzip,如果服务器不支持,请勿开启)
支持4种不同的查询方式

*ONLY_NETWORK  只查询网络数据

*ONLY_CACHED   只查询本地缓存

*CACHED_ELSE_NETWORK  先查询本地缓存，如果本地没有，再查询网络数据

*NETWORK_ELSE_CACHED  先查询网络数据，如果没有，再查询本地缓存

支持get和post请求，默认查询方式为NETWORK_ELSE_CACHED，可通过Builder来指定默认查询方式

#Android Studio
compile('git.dzc:okhttputilslib:1.0.4')
如果找不到,在build.gradle加入
repositories {
    mavenCentral()
    jcenter()
}


#简单使用方法：
 1.get请求，post请求同理

    okHttpUtils = new OKHttpUtils.Builder(this).build();
    okHttpUtils.get("http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json", cacheType, MainActivity.this,new JsonCallback<DateModule>() {
    
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
                }
    
                @Override
                public void onResponse(final DateModule object) throws IOException {
                    Log.d(TAG,"onResponse");
                    tv5.post(new Runnable() {
                        @Override
                        public void run() {
                            tv5.setText(object.getResult().getDatetime_2());
                        }
                    });
                }
            });
 2.上传文件
    
    uploadFile(String url, File file, Headers headers, UploadListener uploadListener)//heads如果没有 可传null
    
 3.自定义client
    
    okHttpUtils = new OKHttpUtils.Builder(this).cachedDir(cacheDir).cacheType(CacheType.ONLY_NETWORK).gzip(true).maxCacheAge(60 *60).maxCachedSize(1024*10).build();

    
#高级使用方法
如果提供的get和post方法不满足需求，可调用request方法来实现需求

#通过Builder初始化的方法
    okHttpUtils = new OKHttpUtils.Builder(this).cachedDir(getCacheDir()).maxCachedSize(5 * 1024 * 1024).cacheType(CacheType.CACHED_ELSE_NETWORK).maxCacheAge(60).build();
来配置默认的配置


#可添加拦截器
通过interceptors()和networkInterceptors()可添加拦截器
拦截器的使用说明请参考这篇文章 [http://www.tuicool.com/articles/Uf6bAnz](http://www.tuicool.com/articles/Uf6bAnz)

#取消请求
*取消单个请求 cancel(url)

*通过将Activity作为tag传进方法,可在Activity的onDestroy方法取消所有请求
<br/><br/>(注意,一旦传入tag,通过url取消请求将会无效)

#添加回调
    调用的时候传入CallBack或JsonCallBack,JsonCallBack使用了Gson解析,JsonCallBack<DateModule>或JsonCallBack<List<DateModule>>来解析当个module或一个list，支持List<Map<Object,Object>等

#more
如果所提供的功能不满足需求，可通过getClient()获取到OKHttpClient来使用原生okhttp的功能

