package pub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.net.URLDecoder;
import java.util.StringTokenizer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: leonwoo</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ControlServlet extends HttpServlet {
    private static final Log logger = LogFactory.getLog(ControlServlet.class);

    public void init() {
        String initClassSet = this.getInitParameter("initclassset");
        if (initClassSet != null) {
            StringTokenizer st = new StringTokenizer(initClassSet, "|");
            while (st.hasMoreTokens()) {
                try {
                    String className = st.nextToken().trim();
                    Class classIn = Class.forName(className);
                    AutorunInterface ai = (AutorunInterface) classIn.newInstance();
                    ai.startServer();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }

    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        try {
            BufferedReader br = req.getReader();
            byte[] buffer = new byte[50];
            String tt = br.readLine();
            if (tt != null)
                System.out.println(URLDecoder.decode(tt, "GBK"));
            else
                System.out.println("is null");

            ServletOutputStream os = res.getOutputStream();
            os.write("<html><body><form action='ControlServlet' enctype='application/x-www-form-urlencoded' method='post'><input type='text' value='1111111' name='ttt'><input type='submit'></form></body></html>".getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
