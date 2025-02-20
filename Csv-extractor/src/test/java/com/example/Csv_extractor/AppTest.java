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
	public static Connection con;
	
	
	@BeforeClass
	public void setup() throws SQLException {
	    con = DriverManager.getConnection(url, user, password);
	    assertNotNull(con, "The connection is not established");
	    App.setConnection(con); // This sets the connection for the App class
	}

	
	


	@AfterClass
	public void exit() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				con = null; // Ensure the connection is set to null after closing
			}
		}
	}

	@Test
	public void testCreateUser() {
		String accountId = "test123";
		String accountLogin = "testLogin";
		String accountStatus = "active";
		String metaType = "Service Account";

		App.createUser(accountId, accountLogin, accountStatus, metaType);

		Assert.assertTrue(userExists(accountId), "User should exist after creation");
	}

	@Test(dependsOnMethods = { "testCreateUser" })
	public void testDeleteUser() {
		String accountId = "test123";
		App.deleteUser(accountId);

		Assert.assertFalse(userExists(accountId), "user should not exist after deletion ");
	}

	private boolean userExists(String accountId) {
		// TODO Auto-generated method stub
		String query = "select count(*) from demo_acc where account_id = '" + accountId + "'";
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	@Test
	public void testReadUser() {
		String accountId = "testReadUser";
	    String accountLogin = "testLoginRead";
	    String accountStatus = "active";
	    String metaType = "Test Account";

	    App.createUser(accountId, accountLogin, accountStatus, metaType);

	    	    App.readUser();

	    assertTrue(userExists(accountId), "User should exist in the database after creation");

	    App.deleteUser(accountId);
		
	}

	@Test
	public void testQueryExecutionAndCsvWriting() {
		String query = "SELECT account_id AS \"Account Id\", " + "account_login AS \"Account Login\", "
				+ "account_status AS \"Status\", " + "meta_type AS \"$metaType\" " + "FROM demo_acc;";

		try (Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				FileWriter out = new FileWriter(csvFilePath);
				CSVPrinter printer = new CSVPrinter(out,
						CSVFormat.DEFAULT.withHeader("Account Id", "Account Login", "Status", "$metaType"))) {

			while (rs.next()) {
				printer.printRecord(rs.getString("Account Id"), rs.getString("Account Login"), rs.getString("Status"),
						rs.getString("$metaType"));
			}
			System.out.println("Data exported to " + csvFilePath + " successfully.");
		} catch (SQLException | IOException e) {
			Assert.fail("Query execution or CSV writing failed: " + e.getMessage());
		}
	}

}
