package com.ef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Data Access class to create schema and tables in db, insert log file into it's table, retrieve threshold ip and save them in block ip table.
 * @author Navid Vaziri
 *
 */
public class DAO {
	private static Connection conn = MyConnection.getConnection();
	/**
	 * This method create "java" schema, LogTable and BlockIp table in db.
	 */
	public void createTables(){
		try(Statement stmt = conn.createStatement();){
				stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS java DEFAULT CHARACTER SET utf8");
				stmt.executeUpdate("USE java");
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS java.LogTable (date DATETIME(3) NOT NULL, ip VARCHAR(45) NOT NULL, request VARCHAR(100) NULL, status INT NOT NULL, description VARCHAR(225) NULL,PRIMARY KEY (date, ip))");
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS java.blockIP (ip VARCHAR(45) NOT NULL, description VARCHAR(168) NULL, PRIMARY KEY (ip))");
		} catch (SQLException e) {
			System.out.println("Could not create table "+e.getMessage());
		}
	}
	
	/**
	 * This method save log file into db by list parameter.
	 * For performance, it insert log file into db if the current logfile table in db has less than 10000 row.
	 * @param list
	 */
	public void batchLogSave(List<LogModel> list) {
		LogModel logModel = null;
		try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO logtable values (?, ?, ?, ?, ?)"); Statement stmt = conn.createStatement();) {
			
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM logtable");
			int rowNum = 0;
			if (rs.next()) rowNum = rs.getInt(1);

			if (rowNum < 10000) {
				stmt.executeUpdate("TRUNCATE TABLE logtable");
				int i = 1;
				for (Iterator<LogModel> iterator = list.iterator(); iterator.hasNext();) {
					logModel = iterator.next();
					pstmt.setString(1, logModel.getDate());
					pstmt.setString(2, logModel.getIp());
					pstmt.setString(3, logModel.getRequest());
					pstmt.setInt(4, logModel.getStatus());
					pstmt.setString(5, logModel.getDescription());
					pstmt.addBatch();

					if (i % 1000 == 0)
						pstmt.executeBatch();

				}
				pstmt.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * This method get ip's which pass the threshold between two dates and put them into map.
	 * The key of map is ip and the value is number of ip requests between two dates and dates(description column).
	 * @param startDate
	 * @param endDate
	 * @param threshold
	 * @return
	 */
	public Map<String, String> thresholdIP(Date startDate, Date endDate, int threshold){
		Map<String, String> map=new HashMap<>();
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT ip, count(ip) FROM logtable  WHERE date BETWEEN ? AND ? GROUP BY ip having count(ip)>?;")){
			pstmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
			pstmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
			pstmt.setInt(3, threshold);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				System.out.println(rs.getString(1));
				map.put(rs.getString(1), "Blocked: "+rs.getInt(2)+" number of request between "+startDate.toString()+" and "+endDate);
			}
		} catch (SQLException e) {
			System.out.println("Cannot call thresholdIp "+e.getMessage());
		}
		return map;
	}
	
	/**
	 * This method put blocked ip into its table in db.
	 * Before inserting an ip, it checks the ip in db table, so if the ip was in the db table, it updates ip with new description info, else it insert ip into table.
	 * @param map
	 */
	public void blockIpSave(Map<String, String> map){
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT ip from blockip where ip=?"); PreparedStatement pstmtI = conn.prepareStatement("INSERT INTO blockip values (?, ?)"); PreparedStatement pstmtU = conn.prepareStatement("UPDATE blockip SET description=? WHERE ip=?");){
			String ip;
			ResultSet rs;
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				ip = iterator.next();
				pstmt.setString(1, ip);
				rs=pstmt.executeQuery();
				if(!rs.next()){
					pstmtI.setString(1, ip);
					pstmtI.setString(2, map.get(ip));
					pstmtI.executeUpdate();
				}else{
					pstmtU.setString(1, map.get(ip));
					pstmtU.setString(2, ip);
					pstmtU.executeUpdate();
				}
			}
		} catch (SQLException e) {
			System.out.println("Cannot call blockIpSave "+e.getMessage());
		}
	}
}
