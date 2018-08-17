package main.java.Helpers;

import java.sql.*;
import java.util.ArrayList;

public class SQLiteTools {

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


    public static void createTable(Statement statement, String tableName, String[] columnNames, String[] columnTypes) {

        //CONSTRUCT SQL STRING
        String sql = ("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnNames[0] + " " + columnTypes[0]);

        for(int i = 1; i < columnNames.length; i++) {
            sql = sql + ", " + columnNames[i] + " " + columnTypes[i];
        }

        sql = sql + ")";

        //EXECUTE SQL STRING
        try {
            statement.execute(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
