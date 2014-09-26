package uk.ac.cam.tl364.fjava.tick5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import uk.ac.cam.cl.fjava.messages.RelayMessage;

public class Database {
	
	private Connection connection;
	
	public Database(String databasePath) throws SQLException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver");
		connection = DriverManager.getConnection("jdbc:hsqldb:file:"
		+databasePath,"SA","");

		Statement delayStmt = connection.createStatement();
		try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
		finally {delayStmt.close();}
		
		connection.setAutoCommit(false);
		
		Statement sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("CREATE TABLE statistics(key VARCHAR(255), value INT)");
		} catch (SQLException e) {
		 System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
		 sqlStmt.close();
		}
		
		sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("INSERT INTO statistics(key,value) VALUES ('Total messages',0)");
		} catch (SQLException e) {
		 e.printStackTrace();
		} finally {
		 sqlStmt.close();
		}
		
		sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("INSERT INTO statistics(key,value) VALUES ('Total logins',0)");
		} catch (SQLException e) {
		 e.printStackTrace();
		} finally {
		 sqlStmt.close();
		}
		
		sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
		                 "message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		} catch (SQLException e) {
		 System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
		 sqlStmt.close();
		}
		
		connection.commit();
	}
	
	public void close() throws SQLException {
		connection.close();
	}
	
	public void incrementLogins() throws SQLException {
		Statement sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("UPDATE statistics SET value = value+1 WHERE key='Total logins'");
		} catch (SQLException e) {
		 e.printStackTrace();
		} finally {
		 sqlStmt.close();
		}
		  
		connection.commit();
	}
	
	public void addMessage(RelayMessage m) throws SQLException {
		String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";
		  PreparedStatement insertMessage = connection.prepareStatement(stmt);
		  try {
		   insertMessage.setString(1, m.getFrom());
		   insertMessage.setString(2, m.getMessage());
		   insertMessage.setLong(3, m.getCreationTime().getTime());
		   insertMessage.executeUpdate();
		  } finally { //Notice use of finally clause here to finish statement
		   insertMessage.close();
		  }
		  
		  Statement sqlStmt = connection.createStatement();
			try {
			 sqlStmt.execute("UPDATE statistics SET value = value+1 WHERE key='Total messages'");
			} catch (SQLException e) {
			 e.printStackTrace();
			} finally {
			 sqlStmt.close();
			}
		  
		  connection.commit();
	}
	
	public List<RelayMessage> getRecent() throws SQLException {
		ArrayList<RelayMessage> out = new ArrayList<RelayMessage>();
		
		String stmt = "SELECT nick,message,timeposted FROM messages "+
                "ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(stmt);
		try {
			ResultSet rs = recentMessages.executeQuery();
		try {
			while (rs.next())
				out.add(new RelayMessage(rs.getString(1),rs.getString(2),new Date(rs.getLong(3))));
		} finally {
			rs.close();
		}
		} finally {
			recentMessages.close();
		}
		
		connection.commit();
		Collections.reverse(out);
		return out;
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException SQLException
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		if (args.length!=1) {
			System.err.println("Usage: java uk.ac.cam.tl364.fjava.tick5.Database <database name>");
			return;
		}
		Class.forName("org.hsqldb.jdbcDriver");
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:"
		+args[0],"SA","");

		Statement delayStmt = connection.createStatement();
		try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
		finally {delayStmt.close();}

		connection.setAutoCommit(false);
		
		Statement sqlStmt = connection.createStatement();
		try {
		 sqlStmt.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
		                 "message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		} catch (SQLException e) {
		 System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
		 sqlStmt.close();
		}
		
		String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";
		  PreparedStatement insertMessage = connection.prepareStatement(stmt);
		  try {
		   insertMessage.setString(1, "Alastair"); //set value of first "?" to "Alastair"
		   insertMessage.setString(2, "Hello, Andy");
		   insertMessage.setLong(3, System.currentTimeMillis());
		   insertMessage.executeUpdate();
		  } finally { //Notice use of finally clause here to finish statement
		   insertMessage.close();
		  }
		  
		  connection.commit();
		  
		  stmt = "SELECT nick,message,timeposted FROM messages "+
                  "ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(stmt);
		try {
			ResultSet rs = recentMessages.executeQuery();
		try {
		while (rs.next())
			System.out.println(rs.getString(1)+": "+rs.getString(2)+" ["+rs.getLong(3)+"]");
		} finally {
			rs.close();
		}
		} finally {
			recentMessages.close();
		}
		
		connection.close();
	}

}
