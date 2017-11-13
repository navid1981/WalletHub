package com.ef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
/**
 * This class read logFile from access.log file which should be located in the same folder of executable jar file.
 * @author Navid Vaziri
 *
 */
public class LogFileReader {
	private Path logFile;
	
	
	public LogFileReader(){
		logFile=Paths.get("access.log");
	}
	
	public List<LogModel> fileReader(){
		List<String> list =null;
		try {
			list=Files.readAllLines(logFile);
		} catch (IOException e) {
			System.out.println("Cannot extract Log file into ArrayList" + e.getMessage());
		}
		
		List<LogModel> arrayList = list.stream()
			    .map(s -> {
			    	LogModel logModel=new LogModel();
			    	String[] array=s.split("\\|");
			    	logModel.setDate(array[0]);
					logModel.setIp(array[1]);
					logModel.setRequest(array[2]);
					logModel.setStatus(Integer.parseInt(array[3]));
					logModel.setDescription(array[4]);
					return logModel;
			    })
			    .collect(Collectors.toList());
		
		return arrayList;
	}
	
	public boolean logfileExist(){
		return Files.exists(logFile);
	}

}
