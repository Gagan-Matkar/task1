package com.example.Csv_extractor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.testng.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AppTest {
	
	private String url = "jdbc:postgresql://10.1.27.41:5432/postgres";
    private String user = "dummyuser";
    private String password = "password1";
    private String csvFilePath = "accounts-3.csv";
    public static Connection con ;
    
    
    
    @BeforeClass
    public void setup() throws SQLException {
    	 con =DriverManager.getConnection(url, user,password);
    	
    	assertNotNull(con, "the connection is not established ");
    	
    	
    	
    	
    }
    
    @AfterClass
    public void exit() {
    	try {
			if(con != null) {
				con.close();
			}
            assertNull(con.isClosed(), "Connection to database should  be closed.");

		} catch (SQLException e) {
			// TODO: handle exception
			fail(e.getMessage());
		}
    }
    
    
    

    @Test
    public void testDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Assert.assertNotNull(conn, "Connection to database should not be null.");
        } catch (Exception e) {
            Assert.fail("Failed to connect to the database: " + e.getMessage());
        }
    }

    @Test
    public void testQueryExecutionAndCsvWriting() {
        String query = "SELECT account_id AS \"Account Id\", " +
                       "account_login AS \"Account Login\", " +
                       "account_status AS \"Status\", " +
                       "meta_type AS \"$metaType\" " +
                       "FROM demo_acc;";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter out = new FileWriter(csvFilePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Account Id", "Account Login", "Status", "$metaType"))) {

            while (rs.next()) {
                printer.printRecord(rs.getString("Account Id"), 
                                    rs.getString("Account Login"), 
                                    rs.getString("Status"), 
                                    rs.getString("$metaType"));
            }
            System.out.println("Data exported to " + csvFilePath + " successfully.");
        } catch (SQLException | IOException e) {
            Assert.fail("Query execution or CSV writing failed: " + e.getMessage());
        }
    }
	
	
	
	
	
	
	
	

	
	
	

	


}
