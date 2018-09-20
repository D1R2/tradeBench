package main.java.Helpers;

import java.text.DecimalFormat;
import java.text.ParseException;
import javafx.util.StringConverter;

/**
 * Contains the DecimalFormat to be applied to the axes in a CandleStickChart.
 *
 * @author RobTerpilowski
 */
public class DecimalAxisFormatter extends StringConverter<Number>{

    // PROPERTIES

    protected DecimalFormat decimalFormat;


    // CONSTRUCTORS

    /**
     * Instantiates a new Decimal axis formatter.
     *
     * @param format the format
     */
    public DecimalAxisFormatter( String format ) {

        decimalFormat = new DecimalFormat(format);

    }

    /**
     * Instantiates a new Decimal axis formatter.
     *
     * @param decimalFormat the decimal format
     */
    public DecimalAxisFormatter( DecimalFormat decimalFormat ) {

        this.decimalFormat = decimalFormat;

    }


    // LOGIC

    /**
     * Converts the DecimalAxisFormatter to a String.
     *
     * @param object the Number to be converted
     * @return the String
     */
    @Override
    public String toString(Number object) {

        return decimalFormat.format(object.doubleValue());

    }

    /**
     * Creates a Number from a String.
     *
     * @param string the string to parse
     * @return the Number
     */
    @Override
    public Number fromString(String string) {

        try {
            return decimalFormat.parse(string);
        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }

    }

}