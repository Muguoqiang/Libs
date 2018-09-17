package com.mgq.http;


import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

public class QueueSingle {
    private static QueueSingle queueSingle;
    private RequestQueue queue;

    private DownloadQueue mDownloadQueue;

    private QueueSingle() {
        queue = NoHttp.newRequestQueue(5);
        mDownloadQueue = NoHttp.newDownloadQueue(3);
    }

    public synchronized static QueueSingle getInstance() {
        if (queueSingle == null) {
            synchronized (QueueSingle.class) {
                if (queueSingle == null) {
                    queueSingle = new QueueSingle();
                }
            }
        }
        return queueSingle;
    }

    public void add(int what, Request<String> request, OnResponseListener onResponseListener) {
        queue.add(what, request, onResponseListener);

    }

    public void download(int what, DownloadRequest request, DownloadListener listener) {
        mDownloadQueue.add(what, request, listener);
    }

    public void cancelAll() {
        queue.cancelAll();
    }
}
