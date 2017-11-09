package com.lck.rox.utils;

import org.json.JSONObject;

interface JsonInterface {
	JSONObject buildJson();
	//解析json
	void parseJson(JSONObject json);
	//获取键值对
	String getShortName();
}
