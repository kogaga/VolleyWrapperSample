package com.weathernews.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

/**
 * StringRequestにタイムアウトとPOST対応とUTF8エンコードを追加
 * @author koga
 */
public class CustomRequest extends StringRequest {
	/** POSTするパラメータ格納 */
	private Map<String, String> mParams = null;

	/**
	 * コンストラクタ
	 * @param method
	 * @param url
	 * @param listener
	 * @param errorListener
	 */
	public CustomRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	/**
	 * POSTするパラメータをセット
	 * @param params
	 */
	public void setParams(Map<String, String> params) {
		this.mParams = params;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if (mParams != null) {
			return mParams;
		}
		return super.getParams();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, "utf-8"); // utf-8指定
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}
