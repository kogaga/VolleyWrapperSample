package com.weathernews.volley;

import android.os.Bundle;
import com.android.volley.VolleyError;

/**
 * VolleyのListener2個指定がめんどくさいのでまとめてみた
 * @author koga
 */
public interface VolleyBundleListener {
	void onResponse(Bundle response, String url);

	void onErrorResponse(VolleyError volleyError, String url);
}
