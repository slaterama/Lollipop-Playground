package com.citymaps.mobile.android.model.vo;

public interface ResultWrapper<T> {

	public int getCode();
	public int getVersion();
	public String getBuild();
	public String getMessage();
	public String getReason();
	public long getTime();
	public T getResult();

}
