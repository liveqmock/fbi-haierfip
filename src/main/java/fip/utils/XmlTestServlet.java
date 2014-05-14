package fip.utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-12-10
 * Time: ÏÂÎç5:35
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(name = "xmltest", urlPatterns = "/xml")
public class XmlTestServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = request.getInputStream();
        InputStreamReader ir = new InputStreamReader(in, "utf-8");
        BufferedReader br = new BufferedReader(ir);
        String line = null;
        StringBuilder  buf = new StringBuilder();

        while ((line = br.readLine()) != null){
            buf.append(line);
        }
        in.close();
        br.close();
        System.out.println("========" + Thread.currentThread().getName()+ "===" + this.hashCode());
        System.out.println(buf);



        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML>");
        out.println("<HTML>");
        out.println("    <HEAD>");
        out.println("   ¡¡¡¡<TITLE>A Servlet</TITLE>");
        out.println("   ¡¡¡¡<meta http-equiv=\"content-type\" " + "content=\"text/html; charset=utf-8\">");
        out.println("¡¡¡¡ </HEAD>");
        out.println("    <BODY>");
        out.println("           Hello AnnotationServlet1111.ºº×Ö444");
        out.println("     </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = request.getInputStream();
        InputStreamReader ir = new InputStreamReader(in, "utf-8");
        BufferedReader br = new BufferedReader(ir);
        String line = null;
        StringBuilder  buf = new StringBuilder();

        while ((line = br.readLine()) != null){
            buf.append(line);
        }
        in.close();
        br.close();
        System.out.println("========" + Thread.currentThread().getName()+ "===" + this.hashCode());
        System.out.println(buf);

        OutputStream os = response.getOutputStream();
        OutputStreamWriter ow = new OutputStreamWriter(os, "utf-8");

        String toa = "<ROOT><stdmsgtype>0200</stdmsgtype><std400trcd>100102</std400trcd><stdprocode/><std400aqid>3</std400aqid><stdmercno/><stdtermtyp/><stdtermid/><std400tlno/><stdpriacno/><stdpindata/><stdlocdate>20121210</stdlocdate><stdloctime>061029</stdloctime><stdtermtrc>061029</stdtermtrc><std400autl/><stdauthid/><std400aups/><std400trdt/><stdrefnum>0</stdrefnum><stdsetdate/><std400trno/><std400mgid>AAAAAAA</std400mgid><std400acur/><LIST></LIST></ROOT>";
        ow.write(toa);
        ow.flush();
        os.close();
        ow.close();
    }
}
