package com.cib.main.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 层级比较
 * @author Lou
 */
public class LevelCompareUtil {
	private static final Map<String,Integer> levelMap = new HashMap<String, Integer>();
	static{
		levelMap.put("小客户", 1);
		levelMap.put("普通客户", 2);
		levelMap.put("主要客户", 3);
		levelMap.put("重要客户", 4);
	}
	
	/**
	 * @param level1
	 * @param level2
	 * @return 1:上升  -1：下降  0：留存
	 */
	public int compareLevel(String level1,String level2){
		int c1 = levelMap.get(level1);
		int c2 = levelMap.get(level2);
		if(c1 > c2){
			return 1;
		}else if(c1 < c2){
			return -1;
		}else
			return 0;
	}
}
