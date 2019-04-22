
package com.tool.monitordog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
	private static ShellUtil sUtil = ShellUtil.getInstance();
	
	private static String BASE_PATH = "/watchdog/";
	private static String CPU_PATH = BASE_PATH+"cpuInfo.txt";
	private static String MEM_PATH = BASE_PATH+"memInfo.txt";
	
	private static String CPU_FILE_PATH = BASE_PATH+"cpuAnalysis.txt";
	private static String MEM_FILE_PATH = BASE_PATH+"memAnalysis.txt";
	
	private static FileUtil mCat = null;
	
	private ArrayList<String> mCpuList = new ArrayList<>();
	private ArrayList<String> mMemList = new ArrayList<>();
	
	private String generateResult;
	private String readCpuResult;
	private String readMemResult;
	private String writeResult;
	
	
	private FileUtil() {}
	
	public static FileUtil getInstance() {
		if(mCat == null) {
			synchronized(FileUtil.class) {
				if(mCat == null)
					mCat = new FileUtil();
			}
		}
		return mCat;
	}
	
	private boolean cpuFileExists(String path) {
		File mFile = new File(path);
		if(mFile.exists())
			return true;
		return false;
	}
	
	public void generateFile(String packageName) {
		File mFile = new File(BASE_PATH);
		if(!mFile.exists()) {
			mFile.mkdir();
			generateResult = "GenerateFile: watchdog目录不存在，新建中...";
		}
		sUtil.execCmd("adb shell top -n 1 -m 15 >> cpuInfo.txt", mFile);
		sUtil.execCmd("adb shell dumpsys meminfo "+packageName+" >> memInfo.txt", mFile);
	}
	
	public void readCpu(String packageName) {
		File mFile = new File(CPU_PATH);
		if(!mFile.exists()) {
			readCpuResult = "ReadCpu: cpuInfo.txt不存在.";
			return;
		}else {
			try {
				BufferedReader bReader = new BufferedReader(new FileReader(CPU_PATH));
				String line = null;
				while((line = bReader.readLine()) != null) {
					if(line.contains(packageName.substring(0, 10))) {
						String mUsage = line.substring(line.indexOf("S")+1,line.indexOf("S")+6);
						mCpuList.add(mUsage);
					}
				}	
			}catch(IOException e) {
				readCpuResult = "ReadCpu: "+e.getMessage()+".";
			}
		}
	}
	
	public void readMem(String packageName) {
		File mFile = new File(MEM_PATH);
		if(!mFile.exists()) {
			readMemResult = "ReadMem: memInfo.txt不存在.";
			return;
		}else {
			try {
				BufferedReader bReader = new BufferedReader(new FileReader(MEM_PATH));
				String line = null;
				while((line = bReader.readLine()) != null) {
					if(line.contains("TOTAL:")) {
						mMemList.add(line.substring(line.indexOf(":")+1, line.indexOf("S")-6));
					}
				}	
			}catch(IOException e) {
				readMemResult = "ReadMem: "+e.getMessage()+".";
			}
		}
	}
	
	
	public void writeFile() {
		File mCpuFile = new File(CPU_FILE_PATH);
		File mMemFile = new File(MEM_FILE_PATH);
		FileWriter mCpuWriter = null;
		FileWriter mMemWriter = null;
		try {
			if(!mCpuFile.exists()) {
				mCpuFile.createNewFile();
				writeResult = "WriteFile: cpu分析文件不存在，新建中...";
			}
			if(!mMemFile.exists()) {
				mMemFile.createNewFile();
				writeResult = "WriteFile: mem分析文件不存在，新建中...";
			}
			mCpuWriter = new FileWriter(mCpuFile, false);
			mMemWriter = new FileWriter(mMemFile, false);
			int[] arraySize = {mCpuList.size(), mMemList.size()};
			Arrays.sort(arraySize);
			
			for(int i=0; i<arraySize[0]; i++) {
				Double cpuEle = Double.parseDouble(mCpuList.get(i));
				Double memEle = Double.parseDouble(mMemList.get(i));
				mCpuWriter.write(cpuEle/4+"\n");
				mMemWriter.write(memEle/1024+"\n");
			}
		}catch(IOException e) {
			writeResult = "WriteFile: "+e.getMessage()+".";
			return;
		}finally {
			try {
				if(mCpuWriter != null)
					mCpuWriter.close();
				if(mMemWriter != null)
					mMemWriter.close();
			}catch(IOException e) {
				writeResult = "WriteFile: "+e.getMessage()+".";
			}
		}
	}
	

	
	
	public List<String> getCpuList() {
		return this.mCpuList;
	}
	public List<String> getMemList() {
		return this.mMemList;
	}
	
	public String getGenerateResult() {
		return this.generateResult;
	}
	public String getReadCpuResult() {
		return this.readCpuResult;
	}
	public String getReadMemResult() {
		return this.readMemResult;
	}
	public String getWriteResult() {
		return this.writeResult;
	}
}
