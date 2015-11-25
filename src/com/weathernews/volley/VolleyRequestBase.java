package com.weathernews.volley;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * VolleyRequest発行クラスの基底クラス
 * @author koga
 */
public class VolleyRequestBase implements Response.Listener<String>, Response.ErrorListener {
	/** Volleyのタイムアウト時間 */
	private final static int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

	/** Volleyの再試行回数 */
	private final static int MAX_RETRY = 3;

	/** URL */
	protected String mUrl = null;

	/** callback */
	private VolleyListener mListener = null;
	private VolleyBundleListener mBundleListener = null;

	/** TAG */
	private Object mTag = null;

	/** 文字列パラメータ */
	protected Map<String, String> mStringParams;

	/**
	 * コンストラクタ
	 * @param url
	 * @param listener
	 * @param bundleListener
	 * @param tag
	 */
	public VolleyRequestBase(
			String url,
			Map<String, String> stringParams,
			VolleyListener listener,
			VolleyBundleListener bundleListener,
			Object tag)
	{
		this.mUrl = url;
		this.mStringParams = stringParams;
		this.mListener = listener;
		this.mBundleListener = bundleListener;
		this.mTag = tag;
	}

	/**
	 * タイムアウトとリトライ設定 & タグ設定
	 * @param request
	 */
	protected <T extends StringRequest> void initRequest(T request) {
		if (request == null) {
			return;
		}
		// タイムアウトとリトライ設定
		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		// タグ設定
		request.setTag(mTag);
	}

	@Override
	public void onResponse(String response) {
		if (mListener != null) {
			mListener.onResponse(response, mUrl);
		}
		if (mBundleListener != null) {
			mBundleListener.onResponse(JsonBundle.toBundle(response), mUrl);
		}
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		if (mListener != null) {
			mListener.onErrorResponse(volleyError, mUrl);
		}
	}
}
