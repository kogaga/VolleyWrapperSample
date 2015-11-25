package com.weathernews.volley;

import android.graphics.Bitmap;
import com.android.volley.VolleyError;

/**
 * VolleyのListener2個指定がめんどくさいのでまとめてみた
 * @author koga
 */
public interface VolleyImageListener {
	void onResponse(Bitmap response, String url);

	void onErrorResponse(VolleyError volleyError, String url);
}
