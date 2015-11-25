package com.weathernews.volley;

import static android.text.TextUtils.isEmpty;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.volleywrappersample.BuildConfig;
import com.weathernews.volley.ProgressiveOutputStream.MultipartProgressListener;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

/**
 * Multipart形式のStringRequest
 * @author koga
 */
public class CustomMultipartRequest extends StringRequest {
	private MultipartEntityBuilder entity;
	private HttpEntity httpEntity;
	private final Map<String, String> mStringPart;
	private final Map<String, String> mFilePart;
	private final MultipartProgressListener mProgressListener;
	private long fileLength = 0L;

	/**
	 * コンストラクタ
	 * @param url
	 *            送信するAPI
	 * @param stringPart
	 *            文字列パラメータ
	 * @param filePart
	 *            送信ファイル
	 * @param listener
	 *            成功Listener
	 * @param errorListener
	 *            失敗Listener
	 * @param progLitener
	 *            進捗Listener
	 */
	public CustomMultipartRequest(
			String url,
			Map<String, String> stringPart,
			Map<String, String> filePart,
			Response.Listener<String> listener,
			Response.ErrorListener errorListener,
			MultipartProgressListener progLitener)
	{
		super(Method.POST, url, listener, errorListener);
		this.mProgressListener = progLitener;
		this.mStringPart = stringPart;
		this.mFilePart = filePart;

		buildMultipartEntity();
	}

	/**
	 * MultipartEntity作成
	 */
	private void buildMultipartEntity() {
		entity = MultipartEntityBuilder.create();
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.setCharset(Consts.UTF_8);

		// 文字列パラメータ
		if (mStringPart != null) {
			// MIME指定
			ContentType contentType = ContentType.create("text/plain", "UTF-8");

			for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
				log("[%s][%s][%s]", contentType.toString(), entry.getKey(), entry.getValue());
				entity.addTextBody(entry.getKey(), entry.getValue(), contentType);
			}
		}

		// 送信ファイル
		if (mFilePart != null) {
			for (Map.Entry<String, String> entry : mFilePart.entrySet()) {
				String key = entry.getKey();
				String filePath = entry.getValue();

				// MIME判定
				String mime = getMimeType(filePath);
				if (mime == null) {
					continue;
				}

				// File化
				File file = new File(filePath);
				
				log("[%s][%s][%s]", mime.toString(), key, filePath);
				entity.addPart(key, new FileBody(file, ContentType.create(mime), file.getName()));

				// ファイルサイズ
				this.fileLength += file.length();
			}
		}

		httpEntity = entity.build();
	}

	/**
	 * 拡張子からMIMEを作成
	 * @param url
	 * @return
	 */
	private String getMimeType(String url) {
		if (isEmpty(url)) {
			return null;
		}

		// 拡張子取得
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension == null) {
			return null;
		}

		// 小文字に変換
		extension = extension.toLowerCase();
		
		// MIME取得
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}

	@Override
	public String getBodyContentType() {
		return httpEntity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			// ProgressiveOutputStreamをかませる
			httpEntity.writeTo(new ProgressiveOutputStream(bos, fileLength, mProgressListener));
		} catch (IOException e) {

		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, "utf-8"); // utf-8指定
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, getCacheEntry());
	}

	private void log(String format, Object... args) {
		if (BuildConfig.DEBUG) {
			Log.e("CustomMultipartRequest", String.format(format, args));
		}
	}
}
