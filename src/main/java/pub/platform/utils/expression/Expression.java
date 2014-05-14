package pub.platform.utils.expression;

public class Expression extends Number {
    Number left;
    char operator;
    Number right;

    public void calc() throws Exception {
        if (operator == '+')
            value = left.value + right.value;
        else if (operator == '-')
            value = left.value - right.value;
        else if (operator == '*')
            value = left.value * right.value;
        else if (operator == '/')
            value = left.value / right.value;
        else {
            throw new Exception("the operator " + operator + " is illegle");
        }
        fuzz();
    }
}