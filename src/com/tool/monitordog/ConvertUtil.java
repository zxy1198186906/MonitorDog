package com.tool.monitordog;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {
	public static ConvertUtil cUtil = null;
	
	private String aveResult;
	
	private ConvertUtil() {}
	
	public static ConvertUtil getInstance() {
		if(cUtil == null) {
			synchronized(ConvertUtil.class) {
				if(cUtil == null)
					cUtil = new ConvertUtil();
			}
		}
		return cUtil;
	}
	
	public List<Double> convertToDouble(List<String> str) {
		List NumList = new ArrayList();
		for(String s : str) {
			NumList.add(Double.parseDouble(s.trim()));
		}
		return NumList;
	}
	
	public double convertToAverage(List<Double> list) {
		double sum = 0;
		double average = 0;
		if(list.size() == 0) 
			aveResult = "AverageChanger: CPU info is empty.";
		else {
			for(Double i : list) {
				sum+=i;
			}
			average = sum/list.size();
			aveResult = "AverageChanger: process average success.";
		}
		return average;
	}
	
	public String getAveResult() {
		return this.aveResult;
	}
}
