package pub.platform.utils.expression;

import java.util.regex.*;
import java.util.*;
import java.io.*;

public class Parser {

    private List stack = new ArrayList();
    private int bracketLevel;
    static Pattern doublePattern = Pattern.compile(
            "[+-]?((\\d*\\.\\d+)|(\\d+\\.?))([eE][+-]?\\d+)?");
    static Pattern spacePattern = Pattern.compile("\\s");

    public void parse(String s) throws Exception {
        String ss = s.trim();
        if (ss == null || ss.equals(""))
            return;
        stack.clear();
        bracketLevel = 0;
        int i = 0;
        while ( true ) {
            // 去除首部空白（回车、换行、空格、回车换行、TAB）
            Matcher matcher = spacePattern.matcher(s);
            for (; matcher.find(i) && matcher.start() == i; )
                i += matcher.end() - matcher.start();
            // 长度为0的字符串终止计算
            if (s.substring(i).equals("")) {
                if (bracketLevel != 0)
                    throw new ParserException("表达式定义错误：括号层次错误", 0, s.length());
                evaluate(0, 0);
                break;
            }
            // 处理小括号()
            char c = s.charAt(i);
            if (c == '(') {
                bracketLevel++;
                i++;
                continue;
            }
            if (c == ')') {
                bracketLevel--;
                if (bracketLevel < 0)
                    throw new ParserException("右括号不匹配", i, i + 1);
                i++;
                continue;
            }
            // 判断元数据是操作符(Operator)还是数据(Data)
            boolean expectNumber = false;
            if (stack.isEmpty())
                expectNumber = true;
            else {
                int n = stack.size();
                Token token = (Token) stack.get(stack.size() - 1);
                if (Operator.class.isInstance(token.data))
                    expectNumber = true;
            }
            // 处理数据
            if (expectNumber) {
                matcher = doublePattern.matcher(s);
                if (!matcher.find(i) || matcher.start() != i)
                    throw new ParserException("应该是数据", i, i + 1);
                push(Double.parseDouble(matcher.group()), matcher.start(), matcher.end());
                i += matcher.end() - matcher.start();
                continue;
            }
            // 处理操作符
            c = s.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/')
                push(c, i, i + 1);
            else
                throw new ParserException("标识符 " + c + " 非法", i, i + 1);
            i++;
        }
    }

    public double getResult() throws Exception {
        int n = stack.size();
        if (n == 0)
            throw new Exception("解析表达式出问题！");
        if ( n > 1 )
            throw new Exception("解析出多个结果！");
        Token token = (Token) stack.get(0);
        if (!Number.class.isInstance(token.data))
            throw new Exception("解析出的数据不是Number类型！");
        Number number = (Number) token.data;
        return number.value;
    }

    private void evaluate(int bracketLevel, int priority) throws Exception {
        while ( true ) {
            int n = stack.size();
            if (n < 3)
                break;
            Token tokenLeft = (Token) stack.get(n - 3);
            Number left = (Number) tokenLeft.data;
            Token tokenOperator = (Token) stack.get(n - 2);
            Operator operator = (Operator) tokenOperator.data;
            Token tokenRight = (Token) stack.get(n - 1);
            Number right = (Number) tokenRight.data;

            if (operator.bracketLevel < bracketLevel)
                break;
            if (operator.bracketLevel == bracketLevel && operator.priority < priority)
                break;
            Expression expression = new Expression();
            expression.left = left;
            expression.operator = operator.data;
            expression.right = right;
            expression.calc();
            // 替换成新的表达式
            Token token = new Token();
            token.data = expression;
            token.start = tokenLeft.start;
            token.end = tokenRight.end;
            stack.remove(n - 1);
            stack.remove(n - 2);
            stack.remove(n - 3);
            stack.add(token);
        }
    }

    private void push(double d, int start, int end) throws Exception {
        // 检查合法性
        if (!stack.isEmpty()) {
            int n = stack.size();
            Token token = (Token) stack.get(n - 1);
            if (!Operator.class.isInstance(token.data))
                throw new ParserException("额外 Number", start, end);
        }
        // 入栈
        Number number = new Number();
        number.value = d;
        Token token = new Token();
        token.data = number;
        token.start = start;
        token.end = end;
        stack.add(token);
    }

    private void push(char c, int start, int end) throws Exception {
        // 检查合法性
        if (stack.isEmpty())
            throw new ParserException("第一个标识符不能是操作符", start, end);
        int n = stack.size();
        Token token = (Token) stack.get(n - 1);
        if (!Number.class.isInstance(token.data))
            throw new ParserException("额外操作符", start, end);
        // 入栈
        Operator operator = new Operator();
        operator.data = c;
        operator.bracketLevel = bracketLevel;
        if (c == '+' || c == '-')
            operator.priority = 1;
        else if (c == '*' || c == '/')
            operator.priority = 2;
        else
            throw new ParserException("操作符 " + c + " 非法", start, end);
        token = new Token();
        token.data = operator;
        token.start = start;
        token.end = end;
        evaluate(operator.bracketLevel, operator.priority);
        stack.add(token);
    }

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (String s; (s = in.readLine()) != null; ) {
            try {
                parser.parse(s);
            } catch (ParserException ex) {
                ex.printStackTrace();
                continue;
            }
            System.out.println(parser.getResult());
        }
    }

}