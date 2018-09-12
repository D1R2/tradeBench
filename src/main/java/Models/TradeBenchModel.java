package main.java.Models;

import main.java.Helpers.SQLiteTools;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * This class models the application, with the chart and the table.
 *
 * @author Daniel Gelber and David Randolph
 * Created  2018-08-08
 * Modified 2018-09-11
 */
public final class TradeBenchModel {

    // PROPERTIES

    private static ArrayList<Trade> trades;
    private static ArrayList<BarData> bars;
    private static Statement statement;
    private static String url = "C:\\Users\\rando\\eclipse-workspace\\TradeBench\\test.db"; //FIXME get program file db working.
    private static File file;
    private static Scanner scanner;
    private static String sql;


    // METHODS
    //FIXME I believe we need to close the connection to the databse at some point but am not sure what protocol is.

    public static void initializer() {

        Connection myConn = SQLiteTools.setConn(url);
        statement = SQLiteTools.setStatement(myConn);

    }

    //PRIMARY FUNCTIONS

    /**
     * Creates a new table in the database for market data imports.
     * @param tableName     Name of the new table.
     */
    public static void createMarketTable(String tableName) {
        //FIXME - Need to add primary keys so that duplicates are not added.

        //SET VARIABLES
        String[] columns = {"Date", "Time", "Open", "High", "Low", "Close", "Volume"};
        String[] types = {"DATE", "TIME", "REAL", "REAL",
                "REAL", "REAL", "INTEGER"};

        //CREATE SQL TABLE
        SQLiteTools.createTable(statement, tableName, columns, types);
    }


    /**
     * Creates a new table in the database for trade data imports.
     * @param tableName     Name of the new table.
     */
    public static void createTradeTable(String tableName) {
        //FIXME - Need to add primary keys so that duplicates are not added.

        //SET VARIABLES
        String[] columns = {"TradeNumber", "Instrument", "Account", "Strategy",
                "MarketPosition", "Qty", "EntryPrice", "ExitPrice", "EntryDate", "EntryTime", "ExitDate",
                "ExitTime", "EntryName", "ExitName"};
        String[] types = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER", "REAL", "REAL",
                "DATE", "TIME", "DATE", "TIME", "TEXT", "TEXT"};

        //CREATE SQL TABLE
        SQLiteTools.createTable(statement, tableName, columns, types);
    }


    /**
     * Imports market data from a NinjaTrader export into the database.
     * @param fileURL      File to import data from.
     * @param tableName    Table to import data to.
     */
    public static void processMarketData(String fileURL, String tableName) {
        //FIXME - Need to fix primary key in this database.

        try {
            //INSTANTIATE FILE SCANNER
            file = new File(fileURL);
            scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                //SPLIT LINE INTO COMPONENTS
                String[] thisLine = scanner.nextLine().split(";");

                //FORMAT DATE AND TIME.
                String[] dateTime = formatMarketDateTime(thisLine[0]).split(" ");

                //BUILD SQL STRING
                sql = "INSERT INTO " + tableName + " (Date, Time, Open, High, Low, Close, Volume) "
                        + "VALUES('" + dateTime[0] + "', '" + dateTime[1] + "', " + thisLine[1] + ", " + thisLine[2]
                        + ", " + thisLine[3] + ", " + thisLine[4] + ", " + thisLine[5] + ");";

                //EXECUTE SQL STRING
                statement.executeUpdate(sql);

            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Import trade data from NinjaTrader exports into the database.
     * @param fileURL      File to import data from.
     * @param tableName    Table to import data to.
     */
    public static void processTradeData(String fileURL, String tableName) {

        try {
            //INSTANTIATE FILE SCANNER AND ATTRIBUTES
            file = new File(fileURL);
            scanner = new Scanner(file);
            boolean firstLine = true;

            /*
            Trade number is intended to be used as a primary key in the database, so it is necessary to retrieve the
            most recent number from the existing database and start with the next int in line when performing imports
            of new trade data.
             */
            int tradeNumber = getNextTradeNumber(statement, tableName);

            //ITERATE THROUGH TRADE FILE
            while(scanner.hasNextLine()) {

                if(firstLine == true) {
                    firstLine = false;
                    String thisLine = scanner.nextLine();
                }

                else {
                    // SPLIT LINE INTO COMPONENTS
                    String[] thisLine = scanner.nextLine().split(",");

                    // FORMAT DATES AND TIME
                    String[] entry = thisLine[8].split(" ");
                    String[] exit = thisLine[9].split(" ");
                    String[] formattedEntry = formatTradeDateTime(entry);
                    String[] formattedExit = formatTradeDateTime(exit);

                    //CONSTRUCT SQL STRING
                    sql = "INSERT INTO " + tableName + " VALUES(" + tradeNumber + ", '" +
                            thisLine[1]+ "', '" + thisLine[2] + "', '" + thisLine[3] + "', '"
                            + thisLine[4] + "', " + thisLine[5] + ", " + thisLine[6] + ", " +
                            thisLine[7] + ", '" + formattedEntry[0] + "', '" + formattedEntry[1]  + "', '" + formattedExit[0] + "', '" + formattedExit[1] + "', '" +
                            thisLine[10] + "', '" + thisLine[11] + "');";

                    //EXECUTE SQL STRING
                    statement.executeUpdate(sql);

                    //Increment trade number.
                    tradeNumber ++;
                }
            }
        }

        catch(Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Gets an array list of trades to display in the table view.
     * Called by Controller.loadTrades
     * @param tableName     Name of the table to retrieve data from.
     * @param startDate     Retrieve trade data on or after this startDate.
     * @param endDate       Retrieve trade data on or before this endDate.
     * @return      Array list of Trade objects.
     */
    public static ArrayList<Trade> getTradeList(String tableName, String startDate, String endDate) {

        //Instantiate return variable.
        ArrayList<Trade> tradeList = new ArrayList<Trade>();

        //Construct query to select trade data from the given table name and between the given start and end date.
        sql = "SELECT * FROM " + tableName + " WHERE EntryDate BETWEEN '" + startDate + "' AND '" + endDate + "';";

        try {
            //Execute query.
            ResultSet rs = statement.executeQuery(sql);

            //Iterate through the result set to generate and append each Trade item to the tradeList.
            while(rs.next()) {
                int tradeNumber = rs.getInt("TradeNumber");
                String instrument = rs.getString("Instrument");
                String account = rs.getString("Account");
                String strategy = rs.getString("Strategy");
                String marketPosition = rs.getString("MarketPosition");
                int qty = rs.getInt("qty");
                double entryPrice = rs.getDouble("EntryPrice");
                double exitPrice = rs.getDouble("ExitPrice");
                String entryDate = rs.getString("EntryDate");
                String entryTime = rs.getString("ExitTime");
                String exitDate = rs.getString("ExitDate");
                String exitTime = rs.getString("ExitTime");

                tradeList.add(new Trade(tradeNumber, instrument, account, strategy, marketPosition, qty, entryPrice,
                        exitPrice, entryDate, entryTime, exitDate, exitTime));

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        trades = tradeList; //FIXME Why didn't I just append to the return variable from the get-go?
        return trades;
    }


    /**
     * Create and return a list of Bar objects to create the candlestick chart from.
     * Called by Controller.loadChart
     * @param trade         Trade object from which to set parameters for the chart.
     * @param marketTable   Table to get market data from.
     * @return  ArrayList of BarData objects from which to generate the candlestick chart.
     */
    public static ArrayList<BarData> getBars(Trade trade, String marketTable) {

        //Instatiate return variable
        bars = new ArrayList<BarData>();

        //Instantiate formatter for later use.
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //Instantiate Variables from the trade object for use in the query.
        String startDate = trade.getEntryDate();
        String endDate = trade.getExitDate();
        String entryTime = trade.getEntryTime();
        String exitTime = trade.getExitTime();

        //Run offsetTradeTime method to add padding before and after the start and end of the trade in the chart.
        String[] times = offsetTradeTime(entryTime, exitTime);
        String startTime = times[0];
        String endTime = times[1];

        //Construct query to grab the market data between the offset start and end times.
        sql = "SELECT * FROM " + marketTable + " WHERE DATE BETWEEN '" + startDate + "' AND '" + endDate +
                "' AND TIME BETWEEN '" + startTime + "' AND '" + endTime + "';";


        try {
            //Execute query and store results in a result set.
            ResultSet rs = statement.executeQuery(sql);

            //Iterate through the result set to generate BarData objects from the result.
            while(rs.next()) {
                String date = rs.getString("Date");
                String time = rs.getString("Time");
                double open = rs.getDouble("Open");
                double high = rs.getDouble("High");
                double low = rs.getDouble("Low");
                double close = rs.getDouble("Close");
                int volume = rs.getInt("Volume");

                //Format time as a GregorianCalendar: BarData's parameter required this
                String dateTime = date + " " + time;
                Date formattedDate = df.parse(dateTime);
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(formattedDate);

                //Generate and append BarData objects to the list.
                bars.add(new BarData(cal, open, high, low, close, volume));

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return bars;
    }


    /**
     * Check if a given table exists.
     * @param tableName     Tablename that will be looked for.
     * @return  Boolean: true if table exists, false if it doesn't.
     */
    public static boolean checkExists(String tableName) {

        try {
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" +tableName + "';");
            boolean exists = rs.next();
            return exists;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * Gets a list of the names of currently existing tables within the database file.
     * @return      ArrayList of existing table names.
     */
    public static ArrayList<String> getTableNames(){

        ArrayList<String> tableArray = new ArrayList<String>();

        try {
            //Generate a result set containing the names of all existing tables.
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            //Append each table name to the array list.
            while(rs.next()) {
                tableArray.add(rs.getString("name"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return tableArray;

    }

    /**
     * Deletes designated table from database.
     * @param tableName     Table to be deleted.
     */
    public static void dropTable(String tableName) {
        //Construct sql query to delete table.
        String sql = "DROP TABLE " + tableName;

        //Execute Query
        try {
            statement.execute(sql);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    //SUPPORT FUNCTIONS

    /**
     * Gets the next trade number in line from existing trade data table.
     * Called by processTradeData to avoid Primary Key overlap and keep trade numbers in order.
     * @param statement     sqLite statement object to use.
     * @param tableName     Table to retrieve the next up trade number from.
     * @return      Int - trade number to use next.
     */
    public static int getNextTradeNumber(Statement statement, String tableName) {
        //FIXME I think we can remove the Statement parameter from this method.

        //Instantiate return variable.
        int tradeNumber = 0;

        //Construct query to retrieve the most recent (which will always be the highest) trade number used.
        sql = "SELECT MAX(TradeNumber) FROM " + tableName;

        //Increment the most recent trade number by one to get the next unused integer.
        try {
            ResultSet rs = statement.executeQuery(sql);
            tradeNumber = rs.getInt(1) + 1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return tradeNumber;
    }


    /**
     * Reformats market data export date and time to be compatible with market data sqLite tables.
     * Called by processMarketData
     * @param dateTime      The original unformatted time
     * @return  String: Correctly formatted time.
     */
    public static String formatMarketDateTime(String dateTime) {

        //Instantiate return variable and set offset (original market data is not received in EST).
        String formattedDateTime = "";
        int offset = 40000;

        //CONVERT TIME TO INT
        String[] split = dateTime.split(" ");
        int time = Integer.parseInt(split[1]);

        //ADJUST FOR OFFSET
        if(time >= offset)
            time = time - offset;
        else {
            time = time + 230000 - offset;
        }

        //CONVERT BACK TO STRING
        String stringTime = Integer.toString(time);

        //STANDARDIZE FORMAT to hhmmss
        while(stringTime.length() < 6) {
            stringTime = "0" + stringTime;
        }

        //APPEND DATE AND TIME BACK TOGETHER
        dateTime = split[0] + " " + stringTime;

        //INSTANTIATE FORMATTERS
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd HHmmss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //FORMAT AND RETURN FORMATTED DATE
        try {
            formattedDateTime = dateFormatter.format(fromUser.parse(dateTime));
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return formattedDateTime;

    }


    /**
     * Formats trade data date and time in the format desired for use in this applications sqLite tables.
     * Called by processTradeData
     * @param time      Unformatted original date-time
     * @return      String: Correctly formatted time.
     */
    public static String[] formatTradeDateTime(String[] time) {

        String formattedTime = "";
        String formattedDate = "";

        //INSTANTIATE FORMATTERS
        SimpleDateFormat fromUser = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat breakDown = new SimpleDateFormat("HHmmss");

        //ADJUST TO MILITARY TIME
        try {
            if(time[2].equals("PM")){
                int intTime = Integer.parseInt(breakDown.format(fromUser.parse(time[1])));

                if(intTime < 120000) {
                    intTime = intTime + 120000;
                }

                formattedTime = Integer.toString(intTime);
                formattedTime = fromUser.format(breakDown.parse(formattedTime));

            }

            else {
                formattedTime = time[1];
            }

            //FORMAT DATE
            String[] splitDate = time[0].split("/");

            String month = splitDate[0];
            String day = splitDate[1];
            String year = splitDate[2];

            if(month.length() < 2) {
                month = "0" + month;
            }

            if(day.length() < 2) {
                day = "0" + day;
            }

            formattedDate = year + "-" + month + "-" + day;

        }

        catch(Exception e) {
            e.printStackTrace();
        }

        String[] formattedDateTime = {formattedDate, formattedTime};

        return formattedDateTime;
    }


    /**
     * Gets specified trade item.
     * Called by Controller.loadChart
     * @param tableName     Data table to pull trade from.
     * @param tradeNumber   Number of the trade to be retrieved.
     * @return      Trade object of desired trade.
     */
    public static Trade getTrade(String tableName, String tradeNumber) {

        Trade trade = null;

        sql = "SELECT * FROM " + tableName + " WHERE TradeNumber = " + tradeNumber + ";";

        try {
            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()) {

                String instrument = rs.getString("Instrument");
                String account = rs.getString("Account");
                String strategy = rs.getString("Strategy");
                String marketPosition = rs.getString("MarketPosition");
                int qty = rs.getInt("Qty");
                double entryPrice = rs.getDouble("EntryPrice");
                double exitPrice = rs.getDouble("ExitPrice");
                String entryDate = rs.getString("EntryDate");
                String entryTime = rs.getString("EntryTime");
                String exitDate = rs.getString("ExitDate");
                String exitTime = rs.getString("ExitTime");

                trade = new Trade(Integer.parseInt(tradeNumber), instrument, account, strategy, marketPosition,
                        qty, entryPrice, exitPrice, entryDate, entryTime, exitDate, exitTime);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return trade;
    }


    /**
     * Offsets trade objects time values to add padding before and after a given trade when loading a candlestick chart.
     * Called by getBars
     * @param entryTime     Time trade was entered.
     * @param exitTime      Time trade was exited.
     * @return      Return array list of offset times.
     */
    public static String[] offsetTradeTime(String entryTime, String exitTime) {
        String [] entrySplit = entryTime.split(":");
        String [] exitSplit = exitTime.split(":");

        int entryHour = Integer.parseInt(entrySplit[0]);
        int exitHour = Integer.parseInt(exitSplit[0]);

        entryHour = entryHour - 4;
        if(entryHour < 9) {
            entryHour = 9;
            entrySplit[1] = "30";
        }

        exitHour = exitHour + 3;
        if(exitHour > 16) {
            exitHour = 16;
            exitSplit[1] = "00";
        }

        String entryHourString = Integer.toString(entryHour);
        String exitHourString = Integer.toString(exitHour);

        if(entryHourString.length() < 2) {
            entryHourString = "0" + entryHourString;
        }

        if(exitHourString.length() < 2) {
            exitHourString = "0" + exitHourString;
        }

        entryTime = entryHourString + ":" + entrySplit[1] + ":" + entrySplit[2];
        exitTime = exitHourString + ":" + exitSplit[1] + ":" + exitSplit[2];

        String[] times = {entryTime, exitTime};

        return times;
    }


}
