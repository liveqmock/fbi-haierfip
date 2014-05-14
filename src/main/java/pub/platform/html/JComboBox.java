/*
 * Created on 2005-5-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pub.platform.html;

import org.apache.ecs.html.*;

import pub.platform.utils.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JComboBox {

	private String comboBoxID   ="";	
	private String enumType     ="";	
	private String defaultValue ="";
	private String defaultoption ="";
	
	private String dropwidth   ="140";
	private String sqlStr      ="";
	
	private String titleStr="±àÂë,Ãû³Æ";
	
	boolean displayAll = false; 
	
	private Input jcomboBox = new Input();
	
	private JOption  jOption;
	
	
	public JComboBox(String name,String enumType){
		comboBoxID     = name;
		this.enumType = enumType;		
		
		jcomboBox.setID(comboBoxID);		
		jOption = new JOption (comboBoxID);		
		
	}
	
	
	public JComboBox(String name,String enumType,String defValue){	
		comboBoxID      = name;
		this.enumType  = enumType;
		defaultValue    = defValue;
		
		jcomboBox.setID(comboBoxID);
		
		jOption = new JOption (comboBoxID);	
	
	}
		
	public void addAttr(String attrName,String attrCont) {
		jcomboBox.addAttribute(attrName,attrCont);
	}
	public void setDisplayAll(boolean displayAll) {
	    this.displayAll = displayAll;
	}
	
	
	public void setDropwidth(String dropwidth) {
	    this.dropwidth = dropwidth;
	}
	
	public void addOption(String optName,String optValue) {
	  	jOption.addOption(optName,optValue);
	  	defaultoption =optName+","+optValue;
	} 
	 
	 public void setSqlString(String sqlString) {
	 	sqlStr = sqlString;
	 	jOption.setSQlStr(sqlString);
     }
	 
	 public void setTitleStr(String titleStr) {
	 	this.titleStr = titleStr;
	 	jOption.setTitleStr(titleStr);
     }
	 
	 public void setTitleVisible(boolean titVis){
		 jOption.setTitleVisible(titVis);
	 }
	 public void setKeyVisible(boolean keyVis){
		   jOption.setKeyVisible(keyVis);
	 }
		
	 
	 public String toString(){
	 	
	    ComboBoxButton comboBoxButton = new ComboBoxButton(comboBoxID);
	 	jcomboBox.addAttribute("dropwidth",dropwidth);
	 	jcomboBox.addAttribute("titleStr",titleStr);
	 	jcomboBox.addAttribute("enumType",enumType);
	 	jcomboBox.addAttribute("defvalue",defaultValue);
	 	jcomboBox.addAttribute("dropOldValue",defaultValue);
	 	jcomboBox.addAttribute("displayAll",displayAll+"");	
	 	jcomboBox.addAttribute("titleVisible",jOption.titleVisible+"");	
	 	jcomboBox.addAttribute("keyVisible",jOption.keyVisible+"");	
	 	jcomboBox.addAttribute("fieldname",comboBoxID);
		jcomboBox.addAttribute("fieldtype","select");
		jcomboBox.addAttribute("sqlStr",sqlStr);
		jcomboBox.addAttribute("defaultOption",defaultoption);
		
		jcomboBox.addAttribute("onkeyup","parentCompent_OnKeyUp()");
		//jcomboBox.addAttribute("onfocus","parentCompent_onfocus()");
		jcomboBox.addAttribute("onclick","parentCompent_onfocus()");
		
		
		jOption.setEnumType(enumType);
		jOption.setTitleStr(titleStr);
		
        jcomboBox.addAttribute("optionStr",Basic.encode(jOption.toString()));
		jcomboBox.addAttribute("buttonStr",Basic.encode(comboBoxButton.toString()));
		
	 	
		StringBuffer StrBuf = new StringBuffer();
		
		StrBuf.append("<script language=\"javascript\">");
//		StrBuf.append(" var comboBox = new ComboBox(\"" +comboBoxID+ "\");");
		StrBuf.append("  document.all[\""+comboBoxID+"\"].comboBox=new ComboBox(\""+comboBoxID+"\");");
//		StrBuf.append("  comboBox.setButtonRect(); ");
		
		StrBuf.append("</script>");
		
		return jcomboBox.toString()+ StrBuf.toString();
	}

	
	public static void main(String[] args) {
		JComboBox boxbut = new JComboBox("333","AJ_BAQK");
		
		System.out.println(boxbut.toString());
	}
	
	
	
	
}


 class ComboBoxButton{
	private String comboBoxID="";
	
	private Input boxButton;
		
	public ComboBoxButton (String comboboxID){
		
		comboBoxID = comboboxID;
		
		boxButton = new Input();
		boxButton.setType("button");
		
		boxButton.setID(comboboxID+"_button");
		
		boxButton.addAttribute("parentID",comboBoxID);
		
		boxButton.addAttribute("class","buttonGrooveActive");
		
		
		boxButton.addAttribute("style","position: absolute;width:15;display:none;height:22;FONT-SIZE:9;FONT-FAMILY: webdings;Z-INDEX:10001;");
		
		
		boxButton.setValue("6");
		
	
		
	}
	
	public String toString(){
		return boxButton.toString();
	}
	
	
	
	
}