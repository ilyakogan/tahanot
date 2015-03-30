package com.tahanot.utils;

import java.util.*;

public class CollectionUtils {

	public static String join(String delim, Iterable<String> data) {
		ArrayList<String> params = new ArrayList<String>();
		for (String str : data)
			params.add(str);
		return join(delim, params);
	}

	public static String join(String delim, ArrayList<String> data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			sb.append(data.get(i));
			if (i >= data.size() - 1) {
				break;
			}
			sb.append(delim);
		}
		return sb.toString();
	}

	public static ArrayList<String> convertToStrings(Iterable<Integer> list) {
		ArrayList<String> newList = new ArrayList<String>();
		for (Integer myInt : list) {
			newList.add(String.valueOf(myInt));
		}
		return newList;
	}
	
	public static Integer[] toIntegerArray(int[] oldArray)
	{
		Integer[] newArray = new Integer[oldArray.length];
		int i = 0;
		for (int value : oldArray) {
		    newArray[i++] = Integer.valueOf(value);
		}
		return newArray;
	}
}
