package pub.platform.advance.utils;

import pub.platform.db.ConnectionManager;
import pub.platform.db.DBUtil;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.form.config.EnumerationBean;

public class SortEnu
{
	private String enuType;
	private String sqlOrder;
	private DatabaseConnection conn;

	public SortEnu()
	{
		super();
	}

	public void print()
	{
		String ls_value, ls_label, ls_desc, ls_expand;
		int li_dispno;
		DatabaseConnection con = null;
		con = ConnectionManager.getInstance().getConnection();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select ENUTYPE, ENUITEMVALUE, ENUITEMLABEL, ENUITEMDESC, DISPNO, ENUITEMEXPAND from ptenudetail");
		buffer.append(" where enutype='"+enuType+"'");
		buffer.append(sqlOrder);
		RecordSet rs = con.executeQuery(buffer.toString());
		while(rs.next())
		{
			ls_value=rs.getString("ENUITEMVALUE");
			ls_label=rs.getString("ENUITEMLABEL");
			ls_desc =rs.getString("ENUITEMDESC");
			li_dispno=rs.getInt("DISPNO");
			ls_expand=rs.getString("ENUITEMEXPAND");
			System.out.println(ls_value + "-" + ls_label + "-" + ls_desc + "-" + ls_expand);
		}
		rs.close();
		con.close();
	}

	public void sort()
	{
		String ls_value, ls_label, ls_desc, ls_expand;
		int li_dispno;
		DatabaseConnection con = null, con2 = null;
		con = ConnectionManager.getInstance().getConnection();
		con2= ConnectionManager.getInstance().getConnection();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select ENUTYPE, ENUITEMVALUE, ENUITEMLABEL, ENUITEMDESC, DISPNO, ENUITEMEXPAND from ptenudetail");
		buffer.append(" where enutype='"+enuType+"'");
		buffer.append(sqlOrder);
		RecordSet rs = con.executeQuery(buffer.toString());
		int nOrder=0;
		while(rs.next())
		{
			nOrder++;
			ls_value=rs.getString("ENUITEMVALUE");
			ls_label=rs.getString("ENUITEMLABEL");
			ls_desc =rs.getString("ENUITEMDESC");
			li_dispno=rs.getInt("DISPNO");
			ls_expand=rs.getString("ENUITEMEXPAND");
			//System.out.println(ls_value + "-" + ls_label + "-" + ls_desc + "-" + ls_expand);
			con2.executeUpdate("update PTENUDETAIL set dispno="+nOrder+" where enutype='"+enuType+"' and enuitemvalue='"+ls_value+"'");
		}
		//con2.executeUpdate("update ptenudetail set enutype='BAK_"+enuType+"' where enutype='"+enuType+"'");
		//con2.executeUpdate("insert into ptenudetail( ENUTYPE, ENUITEMVALUE, ENUITEMLABEL, ENUITEMDESC, DISPNO, ENUITEMEXPAND) "+
		//		"select '"+enuType+"', ENUITEMVALUE, ENUITEMLABEL, ENUITEMDESC, DISPNO, ENUITEMEXPAND "+
		//		"from ptenudetail where enutype='BAK_"+enuType+"' order by dispno");
		//con2.executeUpdate("delete ptenudetail where enutype='BAK_"+enuType+"'");
		rs.close();
		con.close();
		con2.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SortEnu se=new SortEnu();
		se.setEnuType("HYDM");
		se.setSqlOrder("order by ENUITEMVALUE");
		//se.print();
		se.sort();
	}

	public String getEnuType()
	{
		return enuType;
	}

	public void setEnuType(String enuType)
	{
		this.enuType = enuType;
	}

	public String getSqlOrder()
	{
		return sqlOrder;
	}

	public void setSqlOrder(String sqlSort)
	{
		this.sqlOrder = sqlSort;
	}

}
