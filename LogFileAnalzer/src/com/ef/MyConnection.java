package com.ef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
/**
 * This class makes connection to db by using db.properties file which should be located in the same folder of executable jar file.
 * @author Navid Vaziri
 *
 */
public class MyConnection {
	private static Connection conSingleton=null;
	private String url;
	private String username;
	private String password;
	
	private MyConnection(){
		Connection conn =null;
		dbConfig();
		try {
			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Successfully connected to database");
		} catch (SQLException e) {
			System.out.println("Couldn't connect to database " + e.getMessage());
		}
		conSingleton=conn;
	}
	
	public void dbConfig(){
		Path dbConfig=Paths.get("db.properties");
		List<String> list;
		try {
			list=Files.readAllLines(dbConfig);
			url=list.get(0).split("url=")[1];
			username=list.get(1).split("username=")[1];
			password=list.get(2).split("password=")[1];
		} catch (IOException e) {
			System.out.println("Cannot read db.properties");
		}
	}
	
	public static Connection getConnection(){
		if(conSingleton==null) new MyConnection();
		return conSingleton;
	}
}
