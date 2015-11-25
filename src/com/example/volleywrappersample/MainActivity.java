package com.example.volleywrappersample;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.weathernews.volley.VolleyBundleListener;
import com.weathernews.volley.VolleyListener;
import com.weathernews.volley.VolleyMultipartRequest;
import com.weathernews.volley.VolleyRequest;
import com.weathernews.volley.ProgressiveOutputStream.MultipartProgressListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Volleyのwrapperの使い方サンプル
 * @author koga
 */
public class MainActivity extends Activity implements OnClickListener {
	private static final String API_SAMPLE = "https://example.com/example.cgi";

	/** Requestをキャンセルする時に使う */
	private final static int REQUEST_TAG = 1000;

	/** ボタンのリソースID */
	private final static int[] BUTTON_RES_ID = {
			R.id.button_get,
			R.id.button_post,
			R.id.button_multi_part };

	/** ボタン */
	private View[] buttons = new View[BUTTON_RES_ID.length];

	/** 結果表示 */
	private TextView text_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	/**
	 * 各Viewの準備
	 */
	private void initViews() {
		text_result = (TextView) findViewById(R.id.text_result);
		for (int i = 0; i < BUTTON_RES_ID.length; i++) {
			buttons[i] = findViewById(BUTTON_RES_ID[i]);
			buttons[i].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_get:
			send(true);
			break;
		case R.id.button_post:
			send(false);
			break;
		case R.id.button_multi_part:
			sendByMultiPart();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 指定TAGのRequestをキャンセルする
		App.getInstance().cancelPendingRequests(REQUEST_TAG);
	}

	/**
	 * GET/POSTリクエスト
	 * @param isGet
	 *            true:GET
	 *            false:POST
	 */
	private void send(boolean isGet) {
		setButtonEnabled(false);
		showResponse("loading...");

		VolleyRequest.Builder builder = new VolleyRequest.Builder(API_SAMPLE, REQUEST_TAG);
		builder.addParam("int_value", 123456789);
		builder.addParam("long_value", 12345678900l);
		builder.addParam("double_value", 123456.789);
		builder.addParam("string_value", "123456789");
		builder.addTimestamp();
		builder.setListener(mVolleyListener);
		// builder.setBundleListener(mVolleyBundleListener); // Bundleに格納して返して欲しい場合

		// このbuildメソッドの違いでGET/POSTリクエストが決まる
		StringRequest request = isGet ? builder.buildGetRequest() : builder.buildPostRequest();

		// RequestQueueに追加
		App.getInstance().addToRequestQueue(request);
	}

	/**
	 * マルチパートPOST
	 */
	private void sendByMultiPart() {
		setButtonEnabled(false);
		showResponse("loading...");

		VolleyMultipartRequest.Builder builder = new VolleyMultipartRequest.Builder(API_SAMPLE, REQUEST_TAG);
		builder.addParam("int_value", 123456789);
		builder.addParam("long_value", 12345678900l);
		builder.addParam("double_value", 123456.789);
		builder.addParam("string_value", "123456789");
		builder.addTimestamp();
		builder.setListener(mVolleyListener);
		// builder.setBundleListener(mVolleyBundleListener); // Bundleに格納して返して欲しい場合

		// builder.addFile("photo", "/dir/dir/photo.png"); // 画像ファイルを送信する場合
		// builder.addFile("movie", "/dir/dir/movie.mp4"); // 動画ファイルを送信する場合

		// 送信の進捗
		builder.setProgressListener(new MultipartProgressListener() {
			@Override
			public void onProgress(long total, long transfered, int progress) {
				log("*** total:%d / transfered:%d / progress:%d", total, transfered, progress);
			}
		});

		// RequestQueueに追加
		App.getInstance().addToRequestQueue(builder.build());
	}

	/**
	 * Stringでresponseを受ける場合
	 */
	private VolleyListener mVolleyListener = new VolleyListener() {
		@Override
		public void onResponse(String response, String url) {
			showResponse("[%s]\n%s", url, response);
			setButtonEnabled(true);
		}

		@Override
		public void onErrorResponse(VolleyError volleyError, String url) {
			showErrorResponse(volleyError, url);
			setButtonEnabled(true);
		}
	};

	/**
	 * Bundleでresponseを受ける場合
	 */
	private VolleyBundleListener mVolleyBundleListener = new VolleyBundleListener() {
		@Override
		public void onResponse(Bundle response, String url) {
			// BundleだとそのままIntentにputして渡せたりする
			showResponse("[%s]\n%s", url, response);
			setButtonEnabled(true);
		}

		@Override
		public void onErrorResponse(VolleyError volleyError, String url) {
			showErrorResponse(volleyError, url);
			setButtonEnabled(true);
		}
	};

	/**
	 * ボタンのロック
	 * @param enabled
	 *            true:有効化
	 *            false:無効化
	 */
	private void setButtonEnabled(boolean enabled) {
		if (buttons == null) {
			return;
		}
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(enabled);
		}
	}

	/**
	 * 結果表示
	 * @param format
	 * @param args
	 */
	private void showResponse(String format, Object... args) {
		if (text_result != null) {
			text_result.setText(String.format(format, args));
		}
	}

	/**
	 * エラー内容表示
	 * @param url
	 * @param volleyError
	 */
	private void showErrorResponse(VolleyError volleyError, String url) {
		String reason = "Unknown";
		if (volleyError instanceof NoConnectionError) {
			reason = "NoConnectionError";
		} else if (volleyError instanceof ServerError) {
			reason = "ServerError";
		} else if (volleyError instanceof NetworkError) {
			reason = "NetworkError";
		}
		showResponse("Error\n[%s]\n%s", url, reason);
	}

	/**
	 * ログ
	 * @param format
	 * @param args
	 */
	private void log(String format, Object... args) {
		Log.d(this.getClass().getSimpleName(), String.format(format, args));
	}
}
