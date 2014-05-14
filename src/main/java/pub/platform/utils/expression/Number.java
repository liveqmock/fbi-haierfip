package pub.platform.utils.expression;

public class Number {

    static double PRECISION = 1E-6;

    double value;
    boolean available;

    public void fuzz() {
        long l = Math.round(value);
        if ( -PRECISION < value - l && value - l < PRECISION)
            value = l;
    }

}