package com.weathernews.volley;

/**
 * VolleyRequestのBuilderパターン
 * @author koga
 */
public abstract class VolleyRequestBuilderBase<T extends VolleyRequestBuilderBase<?>> {
	protected String url = null;
	protected Object tag = null;
	protected VolleyListener listener = null;
	protected VolleyBundleListener bundleListener = null;

	/**
	 * コンストラクタ
	 * @param url
	 * @param tag
	 */
	public VolleyRequestBuilderBase(String url, Object tag) {
		this.url = url;
		this.tag = tag;
	}

	/**
	 * Listenerセット
	 * @param listener
	 * @return
	 */
	public T setListener(VolleyListener listener) {
		this.listener = listener;
		return getInstance();
	}

	/**
	 * Bundle版Listenerセット
	 * @param bundleListener
	 * @return
	 */
	public T setBundleListener(VolleyBundleListener bundleListener) {
		this.bundleListener = bundleListener;
		return getInstance();
	}

	/**
	 * intパラメータ追加
	 * @param key
	 * @param value
	 * @return
	 */
	public T addParam(String key, int value) {
		return addParam(key, String.valueOf(value));
	}

	/**
	 * longパラメータ追加
	 * @param key
	 * @param value
	 * @return
	 */
	public T addParam(String key, long value) {
		return addParam(key, String.valueOf(value));
	}

	/**
	 * doubleパラメータ追加
	 * @param key
	 * @param value
	 * @return
	 */
	public T addParam(String key, double value) {
		return addParam(key, String.valueOf(value));
	}

	/**
	 * Stringパラメータ追加
	 * @param key
	 * @param value
	 * @return
	 */
	abstract public T addParam(String key, String value);

	/**
	 * Timestampを付ける
	 * @return
	 */
	abstract public T addTimestamp();

	/**
	 * TのInstance
	 * @return
	 */
	abstract protected T getInstance();
}
