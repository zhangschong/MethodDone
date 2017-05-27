package com.dlz.mthdone.mf;

import java.lang.reflect.Method;

/**
 * 用于保存方法信息
 */
public class MethodInfo {

	/**
	 * 所在的类
	 */
	public Class<?> declareClass;

	/**
	 * 参数数量
	 */
	public int paramsCount;

	/**
	 * 所有参数类型
	 */
	public Class<?>[] paramsTypes;

	/**
	 * 方法对象
	 */
	public Method method;

	/**
	 * 方法名
	 */
	public String methodName;
}
