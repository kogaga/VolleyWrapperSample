package com.weathernews.volley;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * データ送信量をモニター
 * @author koga
 */
public class ProgressiveOutputStream extends FilterOutputStream {
	public interface MultipartProgressListener {
		void onProgress(long total, long transfered, int progress);
	}

	/** 進捗お知らせListener */
	private final MultipartProgressListener mListener;

	/** 送信済み */
	private long mTransferred;

	/** ファイルサイズ */
	private long mFileLength;

	/** 進捗(%) */
	private int mProgress;

	public ProgressiveOutputStream(final OutputStream out, long fileLength, final MultipartProgressListener listener) {
		super(out);
		this.mFileLength = fileLength;
		this.mListener = listener;
		this.mTransferred = 0;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		mTransferred += len;
		callback();
	}

	public void write(int b) throws IOException {
		out.write(b);
		mTransferred++;
		callback();
	}

	/**
	 * 進捗をお知らせ
	 */
	private void callback() {
		if (mListener == null) {
			return;
		}

		int progress = (int) ((double) mTransferred / (double) mFileLength * 100);

		// 前回と同じならreturn
		if (mProgress == progress) {
			return;
		}
		mProgress = progress;

		this.mListener.onProgress(mFileLength, mTransferred, progress);
	}
}
