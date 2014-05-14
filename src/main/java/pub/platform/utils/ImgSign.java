package pub.platform.utils;

import java.io.*;
import java.util.*;
import com.sun.image.codec.jpeg.*;
import java.awt.image.*;
import java.awt.*;
public class ImgSign {

	BufferedImage image;
	  
	  // 创建 jpg 文件到指定路径下
	  public void writeJpg(String path) {
	    try {
	      FileOutputStream fos = new FileOutputStream(path);
	      BufferedOutputStream bos = new BufferedOutputStream(fos);
	      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
	      encoder.encode(image);
	      bos.close(); 
	    } catch(FileNotFoundException fnfe) {
	      System.out.println(fnfe);
	    } catch(IOException ioe) {
	      System.out.println(ioe);
	    }
	  }  
	  
	  
	public String writeSign(Graphics graph){
		String retSign="";
		
		int rad =(int)Math.round(Math.random()*10);
		if (rad ==10)
        	rad = 9;
		
		graph.setColor(Color.BLUE);
		if (rad>3&&rad<9){
			graph.setFont(new Font("", Font.ITALIC, 18));
			graph.drawString(rad+"",5,15);
		}else {
			graph.setFont(new Font("", Font.BOLD, 15));
			graph.drawString(rad+"",5,15);
		}
		retSign +=rad;
		
		rad =(int)Math.round(Math.random()*10);
		if (rad ==10)
        	rad = 9;
		
		if (rad<6){
			graph.setFont(new Font("", Font.ITALIC, 18));
			graph.drawString(rad+"",25,15);
		}else {
			graph.setFont(new Font("", Font.BOLD, 15));
			graph.drawString(rad+"",25,15);
		}
		retSign +=rad;
		
		rad =(int)Math.round(Math.random()*10);		
		if (rad ==10)
        	rad = 9;
		
		if (rad<6){
			graph.setFont(new Font("", Font.PLAIN, 18));
			graph.drawString(rad+"",45,15);
		}else {
			graph.setFont(new Font("", Font.ITALIC, 15));
			graph.drawString(rad+"",45,15);
		}
		retSign +=rad;
		
        rad =(int)Math.round(Math.random()*10);	
        if (rad ==10)
        	rad = 9;
		graph.setFont(new Font("", Font.BOLD, 18));
		graph.drawString(rad+"",65,15);
		retSign +=rad;
		
		return retSign;
	}
	
	public String creatImgSign(String filePath)
	{
		int width=80, height=20;
	    
		
	    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
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
	    String retSign = writeSign(g);

	    writeJpg(filePath); 
	    
	    return retSign;
	    
	    
	}

}


