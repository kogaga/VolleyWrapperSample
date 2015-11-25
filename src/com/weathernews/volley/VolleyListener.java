package com.weathernews.volley;

import com.android.volley.VolleyError;

/**
 * VolleyのListener2個指定がめんどくさいのでまとめてみた
 * @author koga
 */
public interface VolleyListener {
	void onResponse(String response, String url);

	void onErrorResponse(VolleyError volleyError, String url);
}
