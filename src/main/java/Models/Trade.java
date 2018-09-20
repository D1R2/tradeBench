package main.java.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class models a trade that took place, including the entrance and exit.
 * The trade MUST have completed to be added.
 *
 * @author Daniel Gelber
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

    /**
     * Instantiates a new Trade.
     *
     * @param tradeNumber    the trade number
     * @param instrument     the instrument
     * @param account        the account
     * @param strategy       the strategy
     * @param marketPosition the market position
     * @param qty            the qty
     * @param entryPrice     the entry price
     * @param exitPrice      the exit price
     * @param entryDate      the entry date
     * @param entryTime      the entry time
     * @param exitDate       the exit date
     * @param exitTime       the exit time
     */
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

    /**
     * Gets trade number.
     *
     * @return the trade number
     */
    public int getTradeNumber() {
        return tradeNumber;
    }


    /**
     * Sets trade number.
     *
     * @param tradeNumber the trade number
     */
    public void setTradeNumber(int tradeNumber) {
        this.tradeNumber = tradeNumber;
    }


    /**
     * Gets instrument.
     *
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }


    /**
     * Sets instrument.
     *
     * @param instrument the instrument
     */
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }


    /**
     * Gets account.
     *
     * @return the account
     */
    public String getAccount() {
        return account;
    }


    /**
     * Sets account.
     *
     * @param account the account
     */
    public void setAccount(String account) {
        this.account = account;
    }


    /**
     * Gets strategy.
     *
     * @return the strategy
     */
    public String getStrategy() {
        return strategy;
    }


    /**
     * Sets strategy.
     *
     * @param strategy the strategy
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }


    /**
     * Gets market position.
     *
     * @return the market position
     */
    public String getMarketPosition() {
        return marketPosition;
    }


    /**
     * Sets market position.
     *
     * @param marketPosition the market position
     */
    public void setMarketPosition(String marketPosition) {
        this.marketPosition = marketPosition;
    }


    /**
     * Gets qty.
     *
     * @return the qty
     */
    public int getQty() {
        return qty;
    }


    /**
     * Sets qty.
     *
     * @param qty the qty
     */
    public void setQty(int qty) {
        this.qty = qty;
    }


    /**
     * Gets entry price.
     *
     * @return the entry price
     */
    public double getEntryPrice() {
        return entryPrice;
    }


    /**
     * Sets entry price.
     *
     * @param entryPrice the entry price
     */
    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }


    /**
     * Gets exit price.
     *
     * @return the exit price
     */
    public double getExitPrice() {
        return exitPrice;
    }


    /**
     * Sets exit price.
     *
     * @param exitPrice the exit price
     */
    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }


    /**
     * Gets entry date.
     *
     * @return the entry date
     */
    public String getEntryDate() {
        return entryDate;
    }


    /**
     * Sets entry date.
     *
     * @param entryDate the entry date
     */
    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }


    /**
     * Gets entry time.
     *
     * @return the entry time
     */
    public String getEntryTime() {
        return entryTime;
    }


    /**
     * Sets entry time.
     *
     * @param entryTime the entry time
     */
    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }


    /**
     * Gets exit date.
     *
     * @return the exit date
     */
    public String getExitDate() {
        return exitDate;
    }


    /**
     * Sets exit date.
     *
     * @param exitDate the exit date
     */
    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }


    /**
     * Gets exit time.
     *
     * @return the exit time
     */
    public String getExitTime() {
        return exitTime;
    }


    /**
     * Sets exit time.
     *
     * @param exitTime the exit time
     */
    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

}
