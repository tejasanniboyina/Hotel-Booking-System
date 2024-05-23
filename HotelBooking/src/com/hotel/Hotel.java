package com.hotel;
 
import java.sql.*;
import java.util.Scanner;

public class Hotel {
	private static final String url = "jdbc:oracle:thin:@Tejaspc:1521:xe";
	private static final String username = "system";
	private static final String password = "1234";
	
	public static void main(String[] args) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			while(true) {
				Scanner scanner = new Scanner(System.in);
				System.out.println("HOTEL MANAGETMENT SYSTEM\n"+
				"1. Reserve a room\n"+
						"2. View reservations\n"+
				"3. Get room number\n"+
						"4. Update reservation\n"+
				"5. Cancel reservation\n"+
						"0. Exit");
				int choice = scanner.nextInt();
				
				switch(choice) {
				case 1:
					reserveRoom(connection, scanner);
					break;
				case 2:
					viewReservation(connection, scanner);
					break;
				case 3:
					getRoomNumber(connection, scanner);
					break;
				case 4:
					updateReservation(connection, scanner);
					break;
				case 5:
					cancelReservation(connection, scanner);
					break;
				case 0:
					exit();
					scanner.close();
					return;
					default:
						System.out.println("invalid choice. Try again.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
			
			private static void reserveRoom(Connection connection, Scanner scanner) {
				try {
					System.out.println("Enter guest name ");
					String guestName = scanner.next();
					scanner.nextLine();
					System.out.println("Enter room number ");
					int roomNumber = scanner.nextInt();
					System.out.println("Enter contact number");
					String contactNumber = scanner.next();
					
					String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)" +
					"VALUES('"+ guestName +"', " +roomNumber+", '"+ contactNumber+"')";
					
					try (Statement statement = connection.createStatement()){
						int affectedRows = statement.executeUpdate(sql);
						
					if(affectedRows > 0) {
						System.out.println("Reservation Successfull!");
					}else{
							System.out.println("Resrevation failed!");	
							}
					}
						}catch(SQLException e) {
							e.printStackTrace();
					}
					
				}
			
			private static void viewReservation(Connection connection, Scanner scanner) {
				String sql ="SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
				
				try(Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery(sql)){
					
					System.out.println("Current Reservations:");
					System.out.println("+------------------+-----------------+-------------------+---------------------+-------------------------+");
			        System.out.println("| Reservation ID   |    Guest        |   Room Number     |    Contact Number   |   Date                  |")	;
					System.out.println("+------------------+-----------------+-------------------+---------------------+-------------------------+");
						
					
					while(resultSet.next()) {
						int reservationId = resultSet.getInt("reservation_id");
						String guestName = resultSet.getString("guest_name");
						int roomNumber = resultSet.getInt("room_number");
						String contactNumber = resultSet.getString("contact_number");
						String reservationDate = resultSet.getTimestamp("reservation_date").toString();
						
						//Format and display the reservation data in a table-like format
						System.out.printf("| %-16d | %-15s |%-18d | %-19s | %-19s |\n",
								reservationId,guestName,roomNumber,contactNumber, reservationDate);
						}
					System.out.println("+------------------+-----------------+-------------------+---------------------+-------------------------+");

				}catch(SQLException e) {
					e.printStackTrace();
				}
					
				}
			
			private static void getRoomNumber(Connection connection , Scanner scanner) {
				try {
					System.out.println("Enter reservation ID :");
					int reservationId= scanner.nextInt();
					System.out.println("Enter guest name: ");
					String guestName = scanner.next();
					
					String sql = "SELECT room_number FROM reservations" +
					" Where reservation_id = " + reservationId + " and Guest_name = '" + guestName +"'";
					
					try(Statement statement = connection.createStatement();
							ResultSet resultSet = statement.executeQuery(sql)){
						if(resultSet.next()) {
							int roomNumber = resultSet.getInt("room_number");
							System.out.println("Room number for reservation ID : "+ reservationId +
									" and Guest : "+ guestName + " is "+ roomNumber );
						}else {
							System.out.println("Reservation not found for the given ID and Guest name.");
						}
					}
					}catch(SQLException e) {
						e.printStackTrace();
					}
			}
			
			private static void updateReservation(Connection connection, Scanner scanner) {
				try {
					System.out.println("Enter reservation ID to update :");
					int reservationId= scanner.nextInt();
					scanner.nextLine();
					
					if(!reservationExists(connection, reservationId)) {
						System.out.println("Reservation not found for given ID");
						return;
					}
					
					System.out.println("Enter new guest name : ");
					String newGuestName = scanner.nextLine();
					System.out.println("Enter new room number: ");
					int newRoomNumber = scanner.nextInt();
					System.out.println("Enter new contact number : ");
					String newContactNumber = scanner.next();
					
					String sql = "UPDATE reservations SET guest_name = '" + newGuestName +"', "+
					"room_number = "+newRoomNumber +", " +
					"contact_number = '"+newContactNumber +"' " +
					"WHERE reservation_id  = "+ reservationId;
					
					try(Statement statement = connection.createStatement()){
						int affectedRows = statement.executeUpdate(sql);
						
						if(affectedRows>0) {
							System.out.println("Reservation updated successfully!");
						}else {
							System.out.println("Reservation update failed!");
						}
					}
				}catch (SQLException e) {
					e.printStackTrace();
				}
					
				}
			
			private static void cancelReservation(Connection connection, Scanner scanner) {
				try {
					System.out.println("Enter reservation ID to delete :");
					int reservationId= scanner.nextInt();
					
					if(!reservationExists(connection, reservationId)) {
						System.out.println("Reservation not found for given ID");
						return;
					}
					String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;
					
					try(Statement statement = connection.createStatement()){
						int affectedRows = statement.executeUpdate(sql);
						
						if(affectedRows>0) {
							System.out.println("Reservation canceled successfully!");
						}else {
							System.out.println("Reservation update failed!");
						}
					}
				}catch (SQLException e) {
					e.printStackTrace();
				}
				}
			
			private static boolean reservationExists(Connection connection, int reservationId) {
				try {
					String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;
					try(Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery(sql)){
						return resultSet.next();
					}
					}catch(SQLException e) {
						e.printStackTrace();
						return false;
						
					}
				}
			
			
			public static void exit() throws InterruptedException{
				System.out.println("Exiting System");
				int i = 5;
				while(i!=0) {
					System.out.println(".");
					Thread.sleep(450);
					i--;
				}
				System.out.println();
				System.out.println("ThankYou For Using Hotel Booking System!!!");
			}
			}
			
			
					
				
			
		
	


