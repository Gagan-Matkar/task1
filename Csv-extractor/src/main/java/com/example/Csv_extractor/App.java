package com.example.Csv_extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static final String url = "jdbc:postgresql://10.1.27.41:5432/postgres";
	private static final String user = "dummyuser";
	private static final String password = "password1";
	private static Connection connection;

	public static void main(String[] args) throws IOException {
		try {
			logger.info("application started");
			connection = DriverManager.getConnection(url, user, password);
			Scanner sc = new Scanner(System.in);

			while (true) {
				System.out.println("choose an operation to do - ");
				System.out.println("1. Create user");
				System.out.println("2. read user");
				System.out.println("3. generate csv");
				System.out.println("4. delete user");
				System.out.println("5. Exit");

				int choice = sc.nextInt();
				sc.nextLine();

				switch (choice) {
				case 1:
					System.out.println("Enter the Account Id :");
					String account_id = sc.nextLine();
					System.out.println("Enter the Account Login :");
					String account_login = sc.nextLine();
					System.out.println("Enter the Account status :");
					String account_status = sc.nextLine();
					System.out.println("Enter the Account type :");
					String meta_type = sc.nextLine();
					createUser(account_id, account_login, account_status, meta_type);
					break;

				case 2:
					readUser();
					break;

				case 3:
					System.out.println("enter file name");
					String fileName = sc.nextLine();
					writeUsersToCsv(fileName);

					break;

				case 4:
					System.out.println("enter data to delete: ");
					String idToDelete = sc.nextLine();
					deleteUser(idToDelete);
					break;

				case 5:
					System.out.println("exiting the system");
					return;

				default:
					System.out.println("Invalid choice . please try again");
				}

			}

		} catch (SQLException e) {
			// TODO: handle exception
			logger.error("an error occoured : {}", e.getMessage());
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
					logger.info("closing the database connection");
				}
			} catch (SQLException e2) {
				// TODO: handle exception
				logger.error("error closing connection : {}", e2.getMessage());
			}
		}

	}

	// wrte to a csv file method

	public static void writeUsersToCsv(String fileName) throws IOException {

		if (!fileName.endsWith(".csv")) {
			fileName += ".csv";
		}
		// TODO Auto-generated method stub

		String query = "SELECT account_id AS \"Account Id\", account_login AS \"Account Login\", account_status AS \"Status\", meta_type AS \"$metaType\" FROM demo_acc;";

		logger.debug("executig the query {}", query);

		File csvFile = new File(fileName);

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(rs))) {
			logger.info("writing to the csv file : {}", csvFile.getAbsolutePath());

			while (rs.next()) {

				while (rs.next()) {
					int columnCount = rs.getMetaData().getColumnCount();
					String[] row = new String[columnCount];

					for (int i = 1; i <= columnCount; i++) {
						row[i - 1] = rs.getString(i);
					}

					csvPrinter.printRecord((Object[]) row);
				}

			}

			System.out.println("file is stored - " + csvFile.getAbsolutePath());

			csvPrinter.flush();
			logger.info("user data written to csv file successfully.");

		}

		catch (SQLException e) {
			logger.error("error reading data {}", e.getMessage());
		} catch (IOException e) {
			logger.error("error writing to the file {}", e.getMessage());
		}

	}

	public static void deleteUser(String idToDelete) {
		// TODO Auto-generated method stub
		String deleteSQL = "DELETE FROM demo_acc WHERE account_id ='" + idToDelete + "'";
		try (Statement stmt = connection.createStatement()) {

			int rowsAffected = stmt.executeUpdate(deleteSQL);

			if (rowsAffected > 0) {
				logger.info("account ID {} deleted.", idToDelete);

			} else {
				logger.warn("no account found with ID {}.", idToDelete);
			}

		} catch (SQLException e) {

			// TODO: handle exception
			logger.error("error deleting account: {}", e.getMessage());

		}
	}

	public static void createUser(String account_id, String account_login, String account_status, String meta_type) {
		// TODO Auto-generated method stub
		String insertSQL = "INSERT INTO demo_acc (account_id , account_login, account_status, meta_type) VALUES ('"
				+ account_id + "','" + account_login + "','" + account_status + "','" + meta_type + "')";
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(insertSQL);
			logger.info(" {},{},{},{}account  created successfully");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error("Error creating account: {}", e.getMessage());
		}

	}

	private static void updateUser(int idToUpdate, String newName) {
		// TODO Auto-generated method stub

	}

	public static void readUser() {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM demo_acc";
		logger.debug("executing query {}", query);

		try (Statement stmt = connection.createStatement()) {

			ResultSet rs = stmt.executeQuery(query);
			logger.info("reading user");
			while (rs.next()) {
				String account_id = rs.getString("account_id");
				String account_login = rs.getString("account_login");
				String account_status = rs.getString("account_status");
				String meta_type = rs.getString("meta_type");

				System.out.printf("Account ID: %s | Account Login: %s | Account Status: %s | Meta Type: %s%n",
						account_id, account_login, account_status, meta_type);

				logger.info("Account ID: {}, Account Login: {}}", account_id, account_login);

			}

		} catch (SQLException e) {
			// TODO: handle exception
			logger.error("Error reading data: {}", e.getMessage());

		}

	}

	public static void setConnection(Connection testConnection) {
		// TODO Auto-generated method stub
		App.connection = testConnection;

	}

}
