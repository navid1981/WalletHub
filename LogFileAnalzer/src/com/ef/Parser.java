package com.ef;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Main class which is design to convert input arguments to specific type and call to DAO class.
 * It converts startDate and duration String to Date object to be usable in SQL query.
 * Also in main method we call LogFileReader and DAO class method to read log from file, save it into db and retreive it.
 * @author Navid Vaziri
 *
 */
public class Parser {
	
private static Date startDate;
private static Date endDate;
private static int threshold;

	public static void main(String[] args) {

		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
		try {
			startDate=sdf.parse(args[0].substring(12));
		} catch (ParseException e) {
			System.out.println("cannot convert to date");
		}
		
		String duration=args[1].substring(11);
		Calendar cal = Calendar.getInstance(); 
	    cal.setTime(startDate); 
	    if(duration.equals("hourly")) cal.add(Calendar.HOUR_OF_DAY, 1);
	    else cal.add(Calendar.DAY_OF_YEAR, 1);
	    endDate=cal.getTime(); 
		
		threshold=Integer.parseInt(args[2].substring(12));
		

		LogFileReader lfr=new LogFileReader();
		List<LogModel> list=lfr.fileReader();

		DAO dao=new DAO();
		dao.createTables();
		
		dao.batchLogSave(list);
		
		Map<String, String> map=dao.thresholdIP(startDate, endDate, threshold);
		
		dao.blockIpSave(map);
	}
	
}
