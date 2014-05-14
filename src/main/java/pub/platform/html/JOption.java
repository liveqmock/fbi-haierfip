package pub.platform.html;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.ecs.html.*;

import pub.platform.db.RecordSet;
import pub.platform.form.config.EnumerationBean;
import pub.platform.form.config.EnumerationType;
import pub.platform.utils.DbUtil;




public class JOption implements Serializable {
	HashMap options = new HashMap(); 
	
	private  IFrame frame = new IFrame();	
	private  Div    div   = new Div();	
	private  Table  table = new Table();
	
	private  TR     tr;
	private  TD     td;	
	
	private String enumType ="";
	private String SQlStr="";	
	private RecordSet rs;	
	
	
	private String parentID;
	
	private String titleStr="";
	
    public boolean titleVisible =true;
    public boolean keyVisible   = true;
	
	

	public JOption(String comboBoxID){
		parentID = comboBoxID;
	}
	
	
	public void addOption(String optName,String optValue) {
	    options.put(optName,optValue);
	}
	
	public void setTitleStr(String titleStr){
		this.titleStr = titleStr;
	}
	
	public void setEnumType(String enumType){
		this.enumType = enumType;
	}
	
	public void setSQlStr(String SQlStr){
		this.SQlStr = SQlStr;
	}
	
	
   public void setTitleVisible(boolean titVis){
		this.titleVisible = titVis;
	}
   public void setKeyVisible(boolean keyVis){
		this.keyVisible = keyVis;
	}
	
	public void setRecordSet(RecordSet rs){
		this.rs = rs;
	}
	
	 private void createFrame(){
	 	frame.setID(parentID+"_frame");
	 	
	 	frame.addAttribute("style"," position: absolute; width:0; height:0;top:0 ;left:0; filter:blendTrans(duration=0.3);display:none ;z-index : 100000");
	 	frame.addAttribute("frameBorder","0");
	 	
	 }
	 
	 private void createDiv(){
	 	div.setID(parentID+"_div");
	 	div.addAttribute("style","position: absolute;overflow-y: scroll;  width:0; height:0;top:0;left:0; display:none ;Z-INDEX:100001");
	 	
	 	div.addAttribute("class","gridTable");
	 	
	 	div.addElement(table);
	 }
	 
	 
	 private void creatTitle(){
	 	tr = new TR();
	 	tr.addAttribute("class","borderGrooveB");
	 	
	 	if (!titleVisible)
	 		tr.addAttribute("style","display:none");
	 	
	 	String[] titleArr = titleStr.split(",");
	 	
	 	for(int i=0; i< titleArr.length; i++){
	 		
	 		td = new TD();
	 		if (!keyVisible && i==0)
		 		td.addAttribute("style","display:none");
      		
	 		td.addAttribute("align","center");
 		
	 		if (i >0)
	 			td.addAttribute("class","borderGrooveL");
	 		
	 		td.addElement(titleArr[i]);	 		
	 		
	 		tr.addElement(td);
	 		
	 	}
	 	
	 	table.addElement(tr);	
	 	
	 	setOption();
	 	
	 }
	 private void setOption(){
	 	Iterator optit = options.keySet().iterator();
	    
	    while(optit.hasNext()) {
		  Object object = optit.next();
		  
		  String[] enumArr=((String) object).split(";");
		  tr = new TR();
		  
		  
		  tr.addAttribute("value",(String) options.get(object));
		  tr.addAttribute("text",enumArr[0]);
		  tr.addAttribute("class","gridEvenRow");
		  tr.addAttribute("onclick","option_onClick(this)");
		  tr.addAttribute("style","cursor: hand;");
		
		  if (enumArr.length >1)
		       tr.addAttribute("expand",enumArr[1]);
		  
			td = new TD();		    	
			td.addElement(object.toString());
			if (!keyVisible)
		 		td.addAttribute("style","display:none");
			td.addAttribute("align","left");			
			tr.addAttribute("value",object.toString());
			tr.addElement(td);			
			
			
			td = new TD();		    	
			td.addElement(enumArr[0]);
			td.addAttribute("align","left");			
			tr.addAttribute("text",enumArr[0]);
			tr.addElement(td); 

	      
	      table.addElement(tr);
	    }
	 }
	 private void getRSComboBox(){
		

		String[] titleArr = titleStr.split(",");
		
		while (rs!=null && rs.next()) {
		      String value = rs.getString(0);
		      String name = rs.getString(1);
		      
		      tr = new TR();
		      
		      tr.addAttribute("value",value);
		      tr.addAttribute("text",name);
		      tr.addAttribute("class","gridEvenRow");
		      tr.addAttribute("onclick","option_onClick(this)");
		      tr.addAttribute("style","cursor: hand;");
		      
		      String retStr ="";
		      
		      if ( rs.getfieldCount()>2){
		         for (int i=2; i< rs.getfieldCount() ; i++ ){
		             retStr = rs.getString(i);
		             if (retStr == null)
		                  retStr ="";
		              tr.addAttribute(rs.getFieldName(i),retStr);
		         }		
		
		       }
		      
		      
		      	for(int i=0; i< titleArr.length; i++){		 		
		      		td = new TD();
		      		if (!keyVisible && i==0)
				 		td.addAttribute("style","display:none");
		      		
		      		retStr = rs.getString(i);
		             if (retStr == null)
		                  retStr ="";               
		             
		      		td.addElement(retStr);
		      		td.addAttribute("align","left");
		      		
		      		tr.addElement(td);
		      	}
		      	
		      	table.addElement(tr);
		}
	 }
	 
	 private void getEnumComBox(){	 	
	 	if (enumType == null || enumType.trim().equals(""))
	 		return;
	 	
	 	EnumerationBean eb = EnumerationType.getEnu(enumType);	 	
	 	if (eb == null)
	         return ;
	 	 
	 	String[] titleArr = titleStr.split(",");
	 	
	    Collection tmpKey = eb.getKeys();
	    Object[] keys = tmpKey.toArray();
	    
	    for (int i = 0 ; i < keys.length ; i++ ) {
	    	Object object = keys[i];
	    	tr = new TR();
	    	tr.addAttribute("class","gridEvenRow");
	    	tr.addAttribute("onclick","option_onClick(this)");
	    	
	    	tr.addAttribute("style","cursor: hand;");
	    	
	    	td = new TD();
	    	if (!keyVisible)
		 		td.addAttribute("style","display:none");
	    	td.addElement(object.toString());
	    	td.addAttribute("align","left");
      		
	    	tr.addAttribute("value",object.toString());
      		tr.addElement(td);
	    	
	    	String[] enumArr=((String)eb.getValue(object)).split(";");
	    	
	    	td = new TD();		    	
	    	td.addElement(enumArr[0]);
	    	td.addAttribute("align","left");
      		
	    	tr.addAttribute("text",enumArr[0]);
      		tr.addElement(td);
	    	
      		
      		if (enumArr.length >1){
      			tr.addAttribute("expand",enumArr[1]);
      			
      			if (titleArr.length >2){
      				td = new TD();		    	
      		    	td.addElement(enumArr[1]);
      		    	td.addAttribute("align","left");
      	      		
      		        tr.addElement(td);
      			}
      		}
      		
      		table.addElement(tr);	    	
	    }	 	
	 	
	 }
	 
	 
	 private void createTableBody(){
	 	table.setID(parentID+"_table");
	 	
	 	table.addAttribute("style","width:100%;");
	 	table.addAttribute("class","borderGroove");
	 	
	 	table.addAttribute("border","0");
	 	table.addAttribute("cellspacing","1");
	 	table.addAttribute("cellpadding","1");
	 	
	 	if (SQlStr != null && ! SQlStr.trim().equals("")){
			rs = DbUtil.getRecord(SQlStr);
		}
	 	
	 	if (rs != null && rs.getRecordCount()>0){
	 		getRSComboBox();
	 		
	 	}else{
	 		getEnumComBox();
	 	}
	 	
	 }
	 
	 
	 public String toString(){
	 	
	 	createFrame();
	 	createDiv();
	 	creatTitle();
	 	createTableBody();
	 	return frame.toString()+div.toString();
	 }
	 
	 public String getDropHtml(){
	 	creatTitle();
	 	createTableBody();
	 	return table.toString();	 	
	 }
	 
	 public static void main(String[] args) {
	 	JOption boxbut = new JOption("333");
		
		System.out.println(boxbut.toString());
	}
	
}