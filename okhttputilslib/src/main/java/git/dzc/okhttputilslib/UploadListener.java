package git.dzc.okhttputilslib;

import com.squareup.okhttp.*;

/**
 * Created by dzc on 15/12/13.
 */
public interface UploadListener extends com.squareup.okhttp.Callback{
    void onProgress(long totalBytes, long remainingBytes);
}
