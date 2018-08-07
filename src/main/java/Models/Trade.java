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
 * Modified 2018-08-03
 */
public class Trade {

    // INSTANCE VARIABLES

    private int number;
    private String type;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double entryPrice;
    private double exitPrice;


    // CONSTRUCTOR

    public Trade(int number, String type, LocalDateTime entryTime, LocalDateTime exitTime, double entryPrice, double exitPrice) {
        this.number = number;
        this.type = type;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
    }


    // GETTERS AND SETTERS

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
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

}
