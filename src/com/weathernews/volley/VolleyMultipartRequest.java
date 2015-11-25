package com.weathernews.volley;

import static android.text.TextUtils.isEmpty;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import com.android.volley.toolbox.StringRequest;
import com.weathernews.volley.ProgressiveOutputStream.MultipartProgressListener;

/**
 * Multipart形式のStringRequestを作成するBuilderクラス
 * @author koga
 * @see <a href="https://github.com/weathernews/Android-Sunnycomb/wiki/VolleyMultipartRequest">wiki:VolleyMultipartRequest</a>
 */
public class VolleyMultipartRequest extends VolleyRequestBase {
	/** 送信ファイル */
	private Map<String, String> mFilePart;

	/** 進捗お知らせListener */
	private MultipartProgressListener mProgressListener;

	/**
	 * コンストラクタ
	 * @param builder
	 */
	private VolleyMultipartRequest(Builder builder) {
		super(builder.url, builder.stringPart, builder.listener, builder.bundleListener, builder.tag);

		this.mFilePart = builder.filePart;
		this.mProgressListener = builder.progressListener;
	}

	/**
	 * Request作成
	 * @return
	 */
	public StringRequest create() {
		CustomMultipartRequest request = new CustomMultipartRequest(mUrl, mStringParams, mFilePart, this, this, mProgressListener);

		// タイムアウト、リトライ数、タグ設定
		initRequest(request);

		return request;
	}

	public static class Builder extends VolleyRequestBuilderBase<Builder> {
		private Map<String, String> stringPart;
		private Map<String, String> filePart;
		private MultipartProgressListener progressListener = null;

		public Builder(String url, Object tag) {
			super(url, tag);
		}

		@Override
		public Builder addParam(String key, String value) {
			if (isEmpty(key) || isEmpty(value)) {
				return this;
			}

			if (stringPart == null) {
				stringPart = new HashMap<String, String>();
			}
			stringPart.put(key, value);
			return this;
		}

		@Override
		public Builder addTimestamp() {
			Uri.Builder builder = Uri.parse(url).buildUpon();
			builder.appendQueryParameter("tm", String.valueOf(System.currentTimeMillis()));
			url = builder.toString();
			return this;
		}

		@Override
		protected Builder getInstance() {
			return this;
		}

		/**
		 * ファイル追加
		 * @param key
		 * @param filePath
		 * @return
		 */
		public Builder addFile(String key, String filePath) {
			if (filePart == null) {
				filePart = new HashMap<String, String>();
			}
			filePart.put(key, filePath);
			return this;
		}

		/**
		 * 送信の進捗お知らせListener
		 * @param progressListener
		 */
		public Builder setProgressListener(MultipartProgressListener progressListener) {
			this.progressListener = progressListener;
			return this;
		}

		/**
		 * build
		 * @return
		 */
		public StringRequest build() {
			return new VolleyMultipartRequest(this).create();
		}
	}
}
