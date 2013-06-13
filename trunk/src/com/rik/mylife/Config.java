package com.rik.mylife;

public class Config {

	public final static String MYLIFE_SERVER = "http://192.168.101.15:8080/CTR/Login";

	public static boolean RECORD_ALREADY_RUN = false;

	public static String getRootPath() {
		String path = "/storage/extSdCard/com.rik.mylife";
		return path;
	}

}
