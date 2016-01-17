package git.dzc.okhttputilslib;

import okhttp3.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dzc on 16/1/10.
 */
public class OkHttpRequest {

    private String url;
    private Headers headers;
    private Map<String,String> params;
    private CacheType cacheType;
    private Object tag;
    private long maxCacheAge;


    private OkHttpRequest(CacheType cacheType, Headers headers, long maxCacheAge, Map<String, String> params, Object tag, String url) {
        this.cacheType = cacheType;
        this.headers = headers;
        this.maxCacheAge = maxCacheAge;
        this.params = params;
        this.tag = tag;
        this.url = url;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public long getMaxCacheAge() {
        return maxCacheAge;
    }

    public void setMaxCacheAge(long maxCacheAge) {
        this.maxCacheAge = maxCacheAge;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Builder{
        private long maxCacheAgeDefault = 3600 * 12;

        private String url;
        private Map<String,String> headers = new HashMap<>();
        private Map<String,String> params = new HashMap<>();
        private CacheType cacheType = CacheType.ONLY_NETWORK;
        private Object tag;
        private long maxCacheAge = maxCacheAgeDefault;
        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder tag(Object tag){
            this.tag = tag;
            return this;
        }

        public Builder maxCacheAge(long maxCacheAge){
            this.maxCacheAge = maxCacheAge;
            this.addHeader("Cache-Control-temp",String.format("max-age=%d", maxCacheAge));
            return this;
        }

        public Builder cacheType(CacheType cacheType){
            this.cacheType = cacheType;
            return this;
        }

        public Builder addParams(String key,String value){
            params.put(key,value);
            return this;
        }

        public Builder addParams(Map<String,String> params){
            this.params.putAll(params);
            return this;
        }
        public Builder addHeader(String key,String value){
            headers.put(key,value);
            return this;
        }

        public Builder addHeader(Map<String,String> headers){
            this.headers.putAll(headers);
            return this;
        }

        public OkHttpRequest build(){
            return new OkHttpRequest(cacheType,Headers.of(headers),maxCacheAge,params,tag,url);
        }
    }
}
