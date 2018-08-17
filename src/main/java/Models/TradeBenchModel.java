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
 * Modified 2018-08-08
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
    public static void initializer() {
        Connection myConn = SQLiteTools.setConn(url);
        statement = SQLiteTools.setStatement(myConn);
    }

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
     * Gets a list of bars to be displayed for selected trade. This method handles the logic,
     * such as loading the bars from wherever, choosing how many to show, etc.
     * @return list of bars.
     */

    public static ArrayList<String> getTableNames(){
        ArrayList<String> tableArray = new ArrayList<String>();

        try {
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
            while(rs.next()) {
                tableArray.add(rs.getString("name"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return tableArray;
    }

    public static void createMarketTable(String tableName) {
        //FIXME - Need to add primary keys so that duplicates are not added.

        //SET VARIABLES
        String[] columns = {"Date", "Time", "Open", "High", "Low", "Close", "Volume"};
        String[] types = {"DATE", "TIME", "REAL", "REAL",
                "REAL", "REAL", "INTEGER"};

        //CREATE SQL TABLE
        SQLiteTools.createTable(statement, tableName, columns, types);
    }

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

    public static ArrayList<Trade> getTradeList(String tableName, String startDate, String endDate) {
        ArrayList<Trade> tradeList = new ArrayList<Trade>();

        sql = "SELECT * FROM " + tableName + " WHERE EntryDate BETWEEN '" + startDate + "' AND '" + endDate + "';";

        try {
            ResultSet rs = statement.executeQuery(sql);
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

        trades = tradeList;
        return trades;
    }

    //Create and return a list of Bar objects to create the trade Chart from.
    //FIXME Add more appropriate time range function.
    public static ArrayList<BarData> getBars(Trade trade, String marketTable) {

        String startDate = trade.getEntryDate();
        String endDate = trade.getExitDate();
        String entryTime = trade.getEntryTime();
        String exitTime = trade.getExitTime();

        String[] times = offsetTradeTime(entryTime, exitTime);

        String startTime = times[0];
        String endTime = times[1];

        sql = "SELECT * FROM " + marketTable + " WHERE DATE BETWEEN '" + startDate + "' AND '" + endDate +
                "' AND TIME BETWEEN '" + startTime + "' AND '" + endTime + "';";

        bars = new ArrayList<BarData>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()) {
                String date = rs.getString("Date");
                String time = rs.getString("Time");
                double open = rs.getDouble("Open");
                double high = rs.getDouble("High");
                double low = rs.getDouble("Low");
                double close = rs.getDouble("Close");
                int volume = rs.getInt("Volume");

                String dateTime = date + " " + time;
                Date formattedDate = df.parse(dateTime);
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(formattedDate);


                bars.add(new BarData(cal, open, high, low, close, volume));

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return bars;
    }

    // TODO feel free to add more methods..

    public static void processMarketData(String fileName, String tableName) {
        //FIXME - Need to fix primary key in this database.

        try {
            //INSTANTIATE FILE SCANNER
            file = new File(fileName);
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


    public static void processTradeExports(String fileName, String tableName) {

        //FIXME need to change time to correct format.

        try {
            //INSTANTIATE FILE SCANNER AND ATTRIBUTES
            file = new File(fileName);
            scanner = new Scanner(file);

            boolean firstLine = true;

            int tradeNumber = getNextTradeNumber(statement, tableName);

            //ITERATE THROUGH TRADE FILE
            while(scanner.hasNextLine()) {

                if(firstLine == true) {
                    firstLine = false;
                    String thisLine = scanner.nextLine();
                }

                else {
                    // SPLIT LINE INTO COMPONETS
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
                    tradeNumber ++;

                    //EXECUTE SQL STRING
                    statement.executeUpdate(sql);

                }
            }
        }

        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static int getNextTradeNumber(Statement statement, String tableName) {
        int tradeNumber = 0;

        sql = "SELECT MAX(TradeNumber) FROM " + tableName;

        try {
            ResultSet rs = statement.executeQuery(sql);
            tradeNumber = rs.getInt(1) + 1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return tradeNumber;
    }


    public static String formatMarketDateTime(String dateTime) {

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


    //Grabs specified trade for use in Controller --> Load Chart
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

    public static String[] offsetTradeTime(String entryTime, String exitTime) {
        String [] entrySplit = entryTime.split(":");
        String [] exitSplit = exitTime.split(":");

        int entryHour = Integer.parseInt(entrySplit[0]);
        int exitHour = Integer.parseInt(exitSplit[0]);

        entryHour = entryHour - 2;
        if(entryHour < 9) {
            entryHour = 9;
            entrySplit[1] = "30";
        }

        exitHour = exitHour + 1;
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

    public static void dropTable(String tableName) {
        String sql = "DROP TABLE " + tableName;
        try {
            statement.execute(sql);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
