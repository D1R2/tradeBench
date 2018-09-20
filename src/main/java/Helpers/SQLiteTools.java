package main.java.Helpers;

import java.sql.*;

/**
 * A series of static tools to help with accessing the database.
 *
 * @author David Randolph
 */
public class SQLiteTools {

    /**
     * Creates a connection to the database at the given URL.
     *
     * @param url the url
     * @return the Connection or null
     */
    public static Connection setConn(String url) {

        Connection myConn = null;
        String connString = "jdbc:sqlite:" + url;

        try {
            myConn = DriverManager.getConnection(connString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return myConn;

    }

    /**
     * Sets a Statement to a Connection.
     *
     * @param myConn the Connection
     * @return the Statement or null
     */
    public static Statement setStatement(Connection myConn) {

        Statement myStatement = null;

        try {
            myStatement = myConn.createStatement();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return myStatement;

    }

    /**
     * Creates a new table in a database.
     *
     * @param statement   the statement
     * @param tableName   the table name
     * @param columnNames the column names
     * @param columnTypes the column types
     * @return true if successful
     */
    public static boolean createTable(Statement statement, String tableName, String[] columnNames, String[] columnTypes) {

        // Construct SQL string
        String sql = ("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnNames[0] + " " + columnTypes[0]);
        for(int i = 1; i < columnNames.length; i++) {
            sql += ", " + columnNames[i] + " " + columnTypes[i];
        }
        sql += ")";

        // Execute SQL string
        try {
            statement.execute(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
