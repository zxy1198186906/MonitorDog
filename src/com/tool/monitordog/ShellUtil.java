package com.tool.monitordog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtil {
	private static ShellUtil sUtil = null;
	
	private ShellUtil() {}
	
	public static ShellUtil getInstance() {
		if(sUtil == null) {
			synchronized(ShellUtil.class) {
				if(sUtil == null)
					sUtil = new ShellUtil();
			}
		}
		return sUtil;
	}
	
	public static String execCmd(String command, File dir) {
		StringBuilder sBuilder = new StringBuilder();
		
		Process mProcess = null;
		BufferedReader bReaderIn = null;
		BufferedReader bReaderErr = null;
		
		try {
			String[] commands = {"/bin/sh","-c",command};
			mProcess = Runtime.getRuntime().exec(commands, null, dir);
			mProcess.waitFor();
			bReaderIn = new BufferedReader(new InputStreamReader(mProcess.getInputStream(), "UTF-8"));
			bReaderErr = new BufferedReader(new InputStreamReader(mProcess.getErrorStream(), "UTF-8"));
			
			String line = null;
			while((line = bReaderIn.readLine()) != null) {
				sBuilder.append(line).append("\n");
			}
			while((line = bReaderErr.readLine()) != null) {
				sBuilder.append(line).append("\n");
			}
		}catch(Exception e) {
			sBuilder.append(e.getMessage()).append("\n");
		}finally {
			try {
				if(bReaderIn != null)
					bReaderIn.close();
				if(bReaderErr != null)
					bReaderErr.close();
				if(mProcess != null)
					mProcess.destroy();
			}catch(IOException e) {
				sBuilder.append(e.getMessage()).append("\n");
			}
		}
		return sBuilder.toString();
	}
}
