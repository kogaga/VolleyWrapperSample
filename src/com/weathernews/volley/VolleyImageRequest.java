package com.weathernews.volley;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.widget.ImageView.ScaleType;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * ImageRequestのBuilder
 * @author koga
 */
public class VolleyImageRequest implements Listener<Bitmap>, ErrorListener {
	/** Volleyのタイムアウト時間 */
	private final static int TIMEOUT = 10000;

	/** Volleyの再試行回数 */
	private final static int MAX_RETRY = 3;

	/** URL */
	private String mUrl = null;

	/** TAG */
	private Object mTag = null;

	/** callback */
	private VolleyImageListener mListener = null;

	private int mMaxWidth;
	private int mMaxHeight;
	private ScaleType mScaleType;
	private Config mDecodeConfig;

	/**
	 * コンストラクタ
	 * @param builder
	 */
	private VolleyImageRequest(Builder builder) {
		// 必須項目
		this.mUrl = builder.url;
		this.mTag = builder.tag;
		this.mListener = builder.listener;

		// オプション
		this.mMaxWidth = builder.maxWidth;
		this.mMaxHeight = builder.maxHeight;
		this.mScaleType = builder.scaleType;
		this.mDecodeConfig = builder.decodeConfig;
	}

	/**
	 * ImageRequestを作成し、もろもろ設定
	 * @return
	 */
	public ImageRequest create() {
		// Request作成
		ImageRequest request = new ImageRequest(mUrl, this, mMaxWidth, mMaxHeight, mScaleType, mDecodeConfig, this);

		// タイムアウトとリトライ設定
		request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		// タグ設定
		request.setTag(mTag);

		return request;
	}

	@Override
	public void onResponse(Bitmap response) {
		if (mListener != null) {
			mListener.onResponse(response, mUrl);
		}
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		if (mListener != null) {
			mListener.onErrorResponse(volleyError, mUrl);
		}
	}

	public static class Builder {
		/** 必須項目 */
		private String url = null;
		private Object tag = null;
		private VolleyImageListener listener = null;

		/** オプション */
		private int maxWidth = 0;
		private int maxHeight = 0;
		private ScaleType scaleType = null;
		private Config decodeConfig = Config.ARGB_8888;

		public Builder(String url, Object tag, VolleyImageListener listener) {
			this.url = url;
			this.tag = tag;
			this.listener = listener;
		}

		/**
		 * 最大の幅
		 * 指定無しの場合：0
		 * @param maxWidth
		 */
		public Builder setMaxWidth(int maxWidth) {
			this.maxWidth = maxWidth;
			return this;
		}

		/**
		 * 最大の高さ
		 * 指定無しの場合：0
		 * @param maxHeight
		 */
		public Builder setMaxHeight(int maxHeight) {
			this.maxHeight = maxHeight;
			return this;
		}

		/**
		 * 縦横比率
		 * 指定無しの場合：null
		 * @param scaleType
		 */
		public Builder setScaleType(ScaleType scaleType) {
			this.scaleType = scaleType;
			return this;
		}

		/**
		 * デコードフォーマット
		 * 指定なしの場合：Config.ARGB_8888
		 * @param decodeConfig
		 */
		public Builder setDecodeConfig(Config decodeConfig) {
			this.decodeConfig = decodeConfig;
			return this;
		}

		/**
		 * Timestampを付ける
		 */
		public Builder addTimestamp() {
			Uri.Builder builder = Uri.parse(url).buildUpon();
			builder.appendQueryParameter("tm", String.valueOf(System.currentTimeMillis()));
			url = builder.toString();
			return this;
		}

		/**
		 * build
		 * @return
		 */
		public ImageRequest build() {
			return new VolleyImageRequest(this).create();
		}
	}
}
