package com.clsa.util;

import java.io.File;

/**
 * This is a file utility
 */
public class FileUtil {

	public static boolean isFileExist(String filePath) {
		
		File f = new File(filePath);
		return f.exists() && f.isFile();
		
	}
}
