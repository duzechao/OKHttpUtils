#＃ OKHttpUtils
对OkHttp进行封装，实现了只查询缓存，网络请求失败自动查询本地缓存等功能,结果用Gson解析
支持4种不同的查询方式

*ONLY_NETWORK  只查询网络数据

*ONLY_CACHED   只查询本地缓存

*CACHED_ELSE_NETWORK  先查询本地缓存，如果本地没有，再查询网络数据

*NETWORK_ELSE_CACHED  先查询网络数据，如果没有，再查询本地缓存

支持get和post请求，默认查询方式为NETWORK_ELSE_CACHED，可通过Builder来指定默认查询方式

#简单使用方法：
    okHttpUtils = new OKHttpUtils.Builder(this).build();
    okHttpUtils.get("http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json", cacheType,null, new JsonCallback<DateModule>() {
    
                //请求开始回调
                @Override
                public void onStart() {
                    Log.d(TAG,"onStart");
                }
    
                //请求结束回调  在onFailure或onRsponse后回调
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
                            //tv5.setText(cacheType.name()+"  "+str);
                            tv5.setText(object.getResult().getDatetime_2());
                        }
                    });
                }
            });

#高级使用方法
如果提供的get和post方法不满足需求，可调用request方法来实现需求
#通过Builder初始化的方法
    okHttpUtils = new OKHttpUtils.Builder(this).cachedDir(getCacheDir()).maxCachedSize(5 * 1024 * 1024).cacheType(CacheType.CACHED_ELSE_NETWORK).maxCacheAge(60).build();
来配置默认的配置


#可添加拦截器
通过interceptors()和networkInterceptors()可添加拦截器
拦截器的使用说明请参考这篇文章 [http://www.tuicool.com/articles/Uf6bAnz](http://www.tuicool.com/articles/Uf6bAnz)

#取消请求
cancel(url)

#添加回调
    调用的时候传入CallBack或JsonCallBack,JsonCallBack使用了Gson解析,JsonCallBack<DateModule>或JsonCallBack<List<DateModule>>来解析当个module或一个list，支持List<Map<Object,Object>等

#more
如果所提供的功能不满足需求，可通过getClient()获取到OKHttpClient来使用原生okhttp的功能

