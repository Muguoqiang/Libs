package com.mgq.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class HttpUtil {
    private static String TAG = "HttpUtil";
    private static Request<String> request;
    private static RequestMethod requestMethod = RequestMethod.GET;
    private static DownloadRequest download;
    public static final String BASE_URL = "http://www.iffia.com/mobile/index.php?";

    //public static final String BASE_URL="http://192.168.31.135/mobile/index.php?";
    private static <T> void startHttp(String tempUrl, HashMap<String, Object> map, final HttpBack httpBack, final Class<T> t) {

        request = NoHttp.createStringRequest(tempUrl, requestMethod);

        request.set(map);

        request.setCacheKey(tempUrl + map);

        request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);

        QueueSingle.getInstance().add(getWhat(), request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {

                try {
                    if (t == null) {

                        try {
                            JSONObject json = new JSONObject(response.get().toString());

                            JSONObject status = json.optJSONObject("status");

                            int succeed = status.optInt("succeed");

                            int error_code = status.optInt("error_code");

                            if (succeed == 1) {

                                httpBack.onSucceed(response.get());

                            } else if (error_code == 400) {

                                String error = status.optString("error_desc");

                                httpBack.onFailed(error);
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                            httpBack.onFailed("操作失败");
                        }

                    } else {
                        T data = null;

                        String result = null;

                        Gson gson = new Gson();

                        result = response.get();

                        data = gson.fromJson(result, t);

                        if (result == null || result.equals("")) {

                            httpBack.onFailed("操作失败");

                        } else if (data == null || data.equals("")) {

                            httpBack.onFailed("操作失败");

                        } else {

                            httpBack.onSucceed(data);
                        }
                    }

                } catch (Exception e) {

                    httpBack.onFailed("操作失败");
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {

                httpBack.onFailed("操作失败");

            }

            @Override
            public void onFinish(int what) {

            }
        });
        Log.e(TAG, "完整请求URL=" + tempUrl + getRequestData(map, "UTF-8").toString());

        Log.e(TAG, "请求Url= " + tempUrl + "\n" + "参数= " + map + "\n" + "请求模式= " + requestMethod);
    }

    public static <T> void sendGetRequest(String tempUrl, Class<T> t, HttpBack httpBack) {
        requestMethod = RequestMethod.GET;
        HashMap<String, Object> params = new HashMap<>();
        startHttp(tempUrl, params, httpBack, t);

    }


    public static <T> void sendGetRequest(String tempUrl, HashMap<String, Object> map, Class<T> t, HttpBack httpBack) {
        requestMethod = RequestMethod.GET;
        startHttp(tempUrl, map, httpBack, t);

    }


    public static <T> void sendPostRequest(String tempUrl, Class<T> t, HttpBack httpBack) {
        requestMethod = RequestMethod.POST;
        HashMap<String, Object> params = new HashMap<>();
        startHttp(tempUrl, params, httpBack, t);

    }


    public static <T> void sendPostRequest(String tempUrl, HashMap<String, Object> map, Class<T> t, HttpBack httpBack) {
        requestMethod = RequestMethod.POST;
        startHttp(tempUrl, map, httpBack, t);

    }


    public interface HttpBack<T> {


        void onSucceed(T result);

        void onFailed(String error);


    }


    private static StringBuffer getRequestData(Map<String, Object> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode((String) entry.getValue(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    private static int getWhat() {
        Random random = new Random();
        int a = random.nextInt(10000);
        return a;
    }


    /**
     * 下载
     *
     * @param context
     * @param tempUrl
     * @param filePath ／／http://blog.csdn.net/chindroid/article/details/7735832/
     */
    public static void Download(Context context, String tempUrl, String filePath, final OnDownBack onDownBack) {
        download = new DownloadRequest(tempUrl, RequestMethod.GET, FileUtils.getRootPath(context) + File.pathSeparator + filePath, true, true);
        QueueSingle.getInstance().download(getWhat(), download, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                onDownBack.error(exception.toString());
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {

            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                onDownBack.progress(progress, speed);
            }

            @Override
            public void onFinish(int what, String filePath) {
                onDownBack.finish(filePath);
            }

            @Override
            public void onCancel(int what) {

            }
        });

    }


    public interface OnDownBack {
        void error(String error);

        void finish(String pathInPhone);

        void progress(int progress, long speed);
    }


}
