# ＃ OKHttpUtils
依赖于okhttp，修改了小部分的okhttp源码，将Cache的部分方法改成public来查询缓存
# 如果需要文件下载，请看这个库[DownloadManager](https://github.com/duzechao/DownloadManager)

对OkHttp进行封装，实现了只查询缓存，网络请求失败自动查询本地缓存等功能,结果用Gson解析
支持文件上传进度回调
<br/>支持gzip,可通过gzip(isOpen)来开启或移除，也可通过在Builder自定义的时候开启(由于okhttp默认开启了gzip,
<br/>所以此选项是对发送到服务器的数据进行gzip,如果服务器不支持,请勿开启)
支持4种不同的查询方式

*ONLY_NETWORK  只查询网络数据

*ONLY_CACHED   只查询本地缓存

*CACHED_ELSE_NETWORK  先查询本地缓存，如果本地没有，再查询网络数据

*NETWORK_ELSE_CACHED  先查询网络数据，如果没有，再查询本地缓存


# 简单使用方法：
 1.

    okHttpUtils = new OKHttpUtils.Builder(this).build();
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
    Request request = new Request.Builder().url(url).build();
    okHttpUtils.request(request, cacheType, jsonCallback);
            
            
 2.上传文件
    
    uploadFile(String url, File file, Headers headers, UploadListener uploadListener)//heads如果没有 可传null
    
 3.自定义client
    
    okHttpUtils = new OKHttpUtils.Builder(this).cachedDir(cacheDir).cacheType(CacheType.ONLY_NETWORK).gzip(true).maxCachedSize(1024*10).build();

 4.同步请求
 
  `
  requestAsync()方法，同步请求
  `

# 可添加拦截器
通过interceptors()和networkInterceptors()可添加拦截器

# 添加回调
    调用的时候传入CallBack或JsonCallBack,JsonCallBack使用了Gson解析,JsonCallBack<DateModule>或JsonCallBack<List<DateModule>>来解析当个module或一个list，支持List<Map<Object,Object>等

# more
如果所提供的功能不满足需求，可通过getClient()获取到OKHttpClient来使用原生okhttp的功能

