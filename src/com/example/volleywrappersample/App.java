package com.example.volleywrappersample;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.app.Application;

/**
 * VolleyのRequestQueueをSingletonで扱うためにApplicationクラス内で管理する
 * @author koga
 */
public class App extends Application {
	private static App mInstance;
	private RequestQueue mRequestQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	/**
	 * インスタンス取得
	 * @return
	 */
	public static synchronized App getInstance() {
		return mInstance;
	}

	/**
	 * QueueにJSONリクエストを追加
	 * @param request
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> request) {
		getRequestQueue().add(request);
	}

	/**
	 * RequestQueueを取得
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	/**
	 * リクエストをキャンセル
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
