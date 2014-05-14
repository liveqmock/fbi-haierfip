package pub.platform.form.control;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TestAction extends Action{

    public int doBusiness() {
        this.res.setResult(false);
        this.res.setMessage("ttttttttttt");
        this.res.setType(0);

        System.out.println("It's Very Good");
        return 0;
    }

    public static void main(String[] args) {
    }
}