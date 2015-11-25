package com.weathernews.volley;

import static android.text.TextUtils.isEmpty;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

/**
 * JSON文字列/JSONObject/JSONArrayをBundleに変換するユーティリティ
 * @author koga
 */
public class JsonBundle {
	/**
	 * JSON文字列をBundleに変換
	 * @param jsonString
	 * @return
	 */
	public static Bundle toBundle(final String jsonString) {
		if (isEmpty(jsonString)) {
			return null;
		}
		try {
			return toBundle(new JSONObject(jsonString));
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * JSONObjectをBundleに変換
	 * @param jsonObject
	 * @return
	 */
	public static Bundle toBundle(final JSONObject jsonObject) {
		final Bundle bundle = new Bundle();
		@SuppressWarnings("unchecked")
		final Iterator<String> iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			final String key = iterator.next();
			if (jsonObject.isNull(key)) {
				bundle.putString(key, null);
				continue;
			}
			final Object value = jsonObject.opt(key);
			if (value instanceof JSONObject) {
				bundle.putBundle(key, toBundle((JSONObject) value));
			} else if (value instanceof JSONArray) {
				bundle.putParcelableArrayList(key, toBundle((JSONArray) value));
			} else if (value instanceof Boolean) {
				bundle.putBoolean(key, (Boolean) value);
			} else if (value instanceof String) {
				bundle.putString(key, (String) value);
			} else if (value instanceof Integer) {
				bundle.putInt(key, (Integer) value);
			} else if (value instanceof Long) {
				bundle.putLong(key, (Long) value);
			} else if (value instanceof Double) {
				bundle.putDouble(key, (Double) value);
			}
		}
		return bundle;
	}

	/**
	 * JSONArrayをBundleに変換
	 * @param array
	 * @return
	 */
	public static ArrayList<Bundle> toBundle(final JSONArray array) {
		final ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		for (int i = 0, size = array.length(); i < size; i++) {
			bundles.add(toBundle(array.optJSONObject(i)));
		}
		return bundles;
	}
}
