/*
Copyright 2014 Zoi Capital, LLC
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package main.java.Models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

/**
 * A model for a candle on a candlestick chart.
 *
 * @author RobTerpilowski
 * Modified by Daniel Gelber
 * Modified 2018-09-20
 */
public class BarData implements Serializable {

    // STATIC PROPERTIES

    public static long serialVersionUID = 1L;

    public static final double NULL = -9D;
    public static final int OPEN = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;
    public static final int CLOSE = 4;

    public enum LENGTH_UNIT {
        TICK, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
    };

    // INSTANCE VARIABLES

    protected double open;
    protected BigDecimal formattedOpen;
    protected double high;
    protected BigDecimal formattedHigh;
    protected double low;
    protected BigDecimal formattedLow;
    protected double close;
    protected BigDecimal formattedClose;
    protected long volume = 0;
    protected long openInterest = 0;
    protected int barLength = 1;
    protected GregorianCalendar dateTime;

    /**
     * Initializes a BarData. Default, does nothing.
     */
    public BarData() {

    }

    /**
     * Initializes a BarData.
     *
     * @param dateTime the time
     * @param open     the open
     * @param high     the high
     * @param low      the low
     * @param close    the close
     * @param volume   the volume
     */
    public BarData(GregorianCalendar dateTime, double open, double high, double low, double close, long volume) {

        this.dateTime = dateTime;
        this.open = open;
        this.formattedOpen = format(open);
        this.close = close;
        this.formattedClose = format(close);
        this.low = low;
        this.formattedLow = format(low);
        this.high = high;
        this.formattedHigh = format(high);
        this.volume = volume;

    }

    /**
     * Initializes a BarData.
     *
     * @param dateTime     the time
     * @param open         the open
     * @param high         the high
     * @param low          the low
     * @param close        the close
     * @param volume       the volume
     * @param openInterest the open interest
     */
    public BarData(GregorianCalendar dateTime, double open, double high, double low, double close, long volume, long openInterest) {

        this(dateTime, open, high, low, close, volume);
        this.openInterest = openInterest;

    }


    // GETTERS AND SETTERS

    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    public long getOpenInterest() {
        return openInterest;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public void setOpenInterest(long openInterest) {
        this.openInterest = openInterest;
    }

    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }


    // LOGIC

    /**
     * Updates this BarData given new information.
     *
     * @param close the close
     */
    public void update(double close) {

        if( close > high ) {
            high = close;
        }

        if( close < low ) {
            low = close;
        }
        this.close = close;

    }

    /**
     * Formats the price into a BigDecimal.
     *
     * @param price the price
     * @return the BigDecimal
     */
    protected BigDecimal format(double price) {

        return BigDecimal.ZERO;

    }


    // OBJECT METHODS

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Date: ").append(dateTime.getTime());
        sb.append(" Open: ").append(open);
        sb.append(" High: ").append(high);
        sb.append(" Low: ").append(low);
        sb.append(" Close: ").append(close);
        sb.append(" Volume: ").append(volume);
        sb.append(" Open Int ").append(openInterest);

        return sb.toString();

    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;
        long temp;

        temp = Double.doubleToLongBits(close);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(high);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(low);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(open);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(openInterest);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        result = PRIME * result + ((dateTime == null) ? 0 : dateTime.hashCode());
        result = PRIME * result + (int) (volume ^ (volume >>> 32));

        return result;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BarData other = (BarData) obj;
        if (Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close)) {
            return false;
        }
        if (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high)) {
            return false;
        }
        if (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low)) {
            return false;
        }
        if (Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open)) {
            return false;
        }
        if (Double.doubleToLongBits(openInterest) != Double.doubleToLongBits(other.openInterest)) {
            return false;
        }
        if (dateTime == null) {
            if (other.dateTime != null) {
                return false;
            }
        } else if (!dateTime.equals(other.dateTime)) {
            return false;
        }
        if (volume != other.volume) {
            return false;
        }

        return true;

    }

}