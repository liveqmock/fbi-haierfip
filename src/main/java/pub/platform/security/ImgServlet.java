package pub.platform.security;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImgServlet extends HttpServlet {
	
	String retSign;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
				String ImgSg = request.getParameter("ImgSg");			
					
				OutputStream os = response.getOutputStream();
				
				int width=80, height=20;			    
				
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
			    Graphics g = image.getGraphics();
			    
			    g.setColor(Color.lightGray);
			    g.fillRect(0, 0, width, height);
			    g.setColor(Color.BLACK);
		
			    int i=0;
			    int j=0;
			    int rad=0;
			    while(i<width){
			    	 while(j<height){
			    		 rad =(int)Math.round(Math.random()*10);
			    		 g.fillOval(rad+i,j,1,1);
			    		 j+=Math.round(Math.random()*10);
			    	 }
			    	 j=0;
			    	 i+=Math.round(Math.random()*10);
			    }
			    
			   writeSign(g,ImgSg);		   
			    
			   JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
			   encoder.encode(image);
			    
			    
	}

	
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		doGet(request,response);
		 
	}

	
	public void writeSign(Graphics graph,String imgSign){	
		
		if (imgSign.length() != 4)
			return;
		
		int rad = Integer.parseInt(imgSign.substring(0,1));		
		graph.setColor(Color.BLUE);
		if (rad>3&&rad<9){
			graph.setFont(new Font("", Font.ITALIC, 18));
			graph.drawString(rad+"",5,15);
		}else {
			graph.setFont(new Font("", Font.BOLD, 15));
			graph.drawString(rad+"",5,15);
		}
		
		
		rad = Integer.parseInt(imgSign.substring(1,2));		
		if (rad<6){
			graph.setFont(new Font("", Font.ITALIC, 18));
			graph.drawString(rad+"",25,15);
		}else {
			graph.setFont(new Font("", Font.BOLD, 15));
			graph.drawString(rad+"",25,15);
		}
		
		
		rad = Integer.parseInt(imgSign.substring(2,3));			
		if (rad<6){
			graph.setFont(new Font("", Font.PLAIN, 18));
			graph.drawString(rad+"",45,15);
		}else {
			graph.setFont(new Font("", Font.ITALIC, 15));
			graph.drawString(rad+"",45,15);
		}
		
		rad = Integer.parseInt(imgSign.substring(3,4));	
		graph.setFont(new Font("", Font.BOLD, 18));
		graph.drawString(rad+"",65,15);
		
	}

}
