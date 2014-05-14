package com.ccb.util;

import java.math.BigDecimal;
import java.text.*;

public class StringUtil {

    public static String getSubstrBetweenStrs(String fromStr, String startStr, String endStr) {
		int length = startStr.length();
		int start = fromStr.indexOf(startStr)+length;
		int end = fromStr.indexOf(endStr);
		return fromStr.substring(start, end);
	}

	public static String parseStr(String orString) {
		String aString = "";
		String reg = new String("\n");
		aString = orString.replaceAll(reg, "<br>");
		// aString=aString.replaceAll("0D","<br>");
		// aString=aString.replaceAll("0A","<br>");
		return aString;
	}

	public static String reversStr(String orString) {
		String aString = "";

		aString = orString.replaceAll("<br>", "\n");

		return aString;
	}

	public static double turnFloat(double orgValue) {
		NumberFormat f = new DecimalFormat();

		return orgValue;
	}

	/**
	 * 
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param val
	 *            需要四舍五入的数字
	 * 
	 * @param scale
	 *            小数点后保留几位
	 * 
	 * @return 四舍五入后的结果 商业计算用string来构造bigdecimal Returns a BigDecimal whose
	 *         value is (this / val), and whose scale is as specified.
	 */

	public static double round(double val, int scale) {
		
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		
	//	if (val.isNaN()) {
			BigDecimal b = new BigDecimal(Double.toString(val));
			BigDecimal one = new BigDecimal("1");
			return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	//	}
	}

	public static void main(String[] para) {
		System.out.println(round(78.63633, 2));
	}

}
