package com.weathernews.volley;

import static android.text.TextUtils.isEmpty;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import com.android.volley.toolbox.StringRequest;

/**
 * VolleyのStringRequestを簡素に記述できるBuilderクラス
 * @author koga
 * @see <a href="https://github.com/weathernews/Android-Sunnycomb/wiki/VolleyRequest">wiki:VolleyRequest</a>
 */
public class VolleyRequest extends VolleyRequestBase {
	/**
	 * コンストラクタ
	 * @param builder
	 */
	private VolleyRequest(Builder builder) {
		super(builder.url, builder.params, builder.listener, builder.bundleListener, builder.tag);
	}

	public static class Builder extends VolleyRequestBuilderBase<Builder> {
		private Map<String, String> params = null;

		public Builder(String url, Object tag) {
			super(url, tag);
		}

		@Override
		public Builder addParam(String key, String value) {
			if (isEmpty(key) || isEmpty(value)) {
				return this;
			}

			if (params == null) {
				params = new HashMap<String, String>();
			}
			params.put(key, value);
			return this;
		}

		@Override
		public Builder addTimestamp() {
			return addParam("tm", System.currentTimeMillis());
		}

		@Override
		protected Builder getInstance() {
			return this;
		}

		/** GETリクエスト作成 */
		public StringRequest buildGetRequest() {
			return new VolleyRequest(this).create(GET);
		}

		/** POSTリクエスト作成 */
		public StringRequest buildPostRequest() {
			return new VolleyRequest(this).create(POST);
		}
	}

	/**
	 * Request作成
	 * @param method
	 * @return
	 */
	public StringRequest create(int method) {
		CustomRequest request = null;
		
		// GETの場合はパラメータ付きURL作成
		if (method == GET) {
			String url = createGetUrl(mUrl);
			request = new CustomRequest(GET, url, this, this);
			mUrl = url;
		}
		// POSTの場合はパラメータを渡す
		else if (method == POST) {
			request = new CustomRequest(POST, mUrl, this, this);
			request.setParams(mStringParams);
		}

		// タイムアウト、リトライ数、タグ設定
		initRequest(request);

		return request;
	}

	/**
	 * GET用のパラメータ付きURL作成
	 * @param url
	 * @return
	 */
	private String createGetUrl(String url) {
		if (mStringParams == null || mStringParams.size() == 0) {
			return url;
		}

		Uri.Builder ub = Uri.parse(url).buildUpon();
		for (String key : mStringParams.keySet()) {
			ub.appendQueryParameter(key, mStringParams.get(key));
		}
		return ub.toString();
	}
}
