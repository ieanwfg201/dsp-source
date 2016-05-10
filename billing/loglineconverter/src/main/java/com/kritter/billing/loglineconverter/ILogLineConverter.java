package com.kritter.billing.loglineconverter;


public interface ILogLineConverter<T> {
	public T convert(String logline) throws Exception;	
}
