package main.java.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class models a trade that took place, including the entrance and exit.
 * The trade MUST have completed to be added.
 *
 * @author Daniel Gelber
 * @version 1.0
 * Created  2018-08-03
 * Modified 2018-08-08
 */
public class Trade {

    // INSTANCE VARIABLES

    private int tradeNumber;
    private String instrument;
    private String account;
    private String strategy;
    private String marketPosition;
    private int qty;
    private double entryPrice;
    private double exitPrice;
    private String entryDate;
    private String entryTime;
    private String exitDate;
    private String exitTime;


    // CONSTRUCTOR

    public Trade(int tradeNumber, String instrument, String account, String strategy, String marketPosition, int qty,
                 double entryPrice, double exitPrice, String entryDate, String entryTime, String exitDate,
                 String exitTime) {
        this.tradeNumber = tradeNumber;
        this.instrument = instrument;
        this.account = account;
        this.strategy = strategy;
        this.marketPosition = marketPosition;
        this.qty = qty;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.exitDate = exitDate;
        this.exitTime = exitTime;
    }


    // GETTERS AND SETTERS

    public int getTradeNumber() {
        return tradeNumber;
    }


    public void setTradeNumber(int tradeNumber) {
        this.tradeNumber = tradeNumber;
    }


    public String getInstrument() {
        return instrument;
    }


    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }


    public String getAccount() {
        return account;
    }


    public void setAccount(String account) {
        this.account = account;
    }


    public String getStrategy() {
        return strategy;
    }


    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }


    public String getMarketPosition() {
        return marketPosition;
    }


    public void setMarketPosition(String marketPosition) {
        this.marketPosition = marketPosition;
    }


    public int getQty() {
        return qty;
    }


    public void setQty(int qty) {
        this.qty = qty;
    }


    public double getEntryPrice() {
        return entryPrice;
    }


    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }


    public double getExitPrice() {
        return exitPrice;
    }


    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }


    public String getEntryDate() {
        return entryDate;
    }


    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }


    public String getEntryTime() {
        return entryTime;
    }


    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }


    public String getExitDate() {
        return exitDate;
    }


    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }


    public String getExitTime() {
        return exitTime;
    }


    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

}
