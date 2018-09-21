package main.java.Models;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Uses a Monte Carlo Simulation to generate random candle data.
 *
 * @author Daniel Gelber created  2018-09-02 modified 2018-09-02
 */
public class MonteCarloSimulation {

    // INSTANCE VARIABLES

    private double drift;
    private double initialPrice;
    private double stdev;
    private Random rand = new Random();


    // LOGIC

    /**
     * Initialize a new MonteCarloSimulation
     *
     * @param drift        the drift
     * @param initialPrice the initial price
     * @param stdev        the standard deviation
     */
    public MonteCarloSimulation(double drift, double initialPrice, double stdev) {
        this.drift = drift;
        this.initialPrice = initialPrice;
        this.stdev = stdev;
    }

    /**
     * Generates a list of candles.
     *
     * @param amount number of candles to be generated.
     * @return list of candles.
     */
    public ArrayList<BarData> generateCandles(int amount) {

        ArrayList<BarData> result = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < amount; i++) {

            // Get open
            double open;
            if (i == 0)
                open = initialPrice;
            else
                open = result.get(i-1).getClose();

            // Get close
            double randValue = stdev * (rand.nextDouble() * 2 - 1);
            double close = open * Math.pow(Math.E, (drift + randValue));

            // Get high and low
            double high = (close > open ? close : open) + Math.pow(rand.nextDouble() * stdev * 20, 2);
            double low = (close > open ? open : close) - Math.pow(rand.nextDouble() * stdev * 20, 2);

            // Volume = zero for now.
            long volume = 0;

            // Time is the beginning of the year 2000 + the index amount of days
            GregorianCalendar cal = new GregorianCalendar(
                    2000, 00, 01, 00, 00, 00);
            cal.add(GregorianCalendar.MINUTE, i);

            result.add(new BarData(cal, open, high, low, close, volume));

        }

        return result;

    }


    // GETTERS AND SETTERS

    /**
     * Gets drift.
     *
     * @return the drift
     */
    public double getDrift() {
        return drift;
    }

    /**
     * Sets drift.
     *
     * @param drift the drift
     */
    public void setDrift(double drift) {
        this.drift = drift;
    }

    /**
     * Gets initial price.
     *
     * @return the initial price
     */
    public double getInitialPrice() {
        return initialPrice;
    }

    /**
     * Sets initial price.
     *
     * @param initialPrice the initial price
     */
    public void setInitialPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    /**
     * Gets stdev.
     *
     * @return the stdev
     */
    public double getStdev() {
        return stdev;
    }

    /**
     * Sets stdev.
     *
     * @param stdev the stdev
     */
    public void setStdev(double stdev) {
        this.stdev = stdev;
    }

}