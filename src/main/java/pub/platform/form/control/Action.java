package pub.platform.form.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.debug.DebugInfo;
import pub.platform.form.util.event.LogAssistor;
import pub.platform.system.manage.dao.PtDeptBean;
import pub.platform.system.manage.dao.PtOperBean;
import pub.platform.utils.ActionRequest;
import pub.platform.utils.LogManager;
import pub.platform.utils.SQLResponse;



public class Action {
	 protected RecordSet rs;
	 protected SQLResponse res;
	 protected ActionRequest req;
	 protected DatabaseConnection dc;
	 protected SessionContext sc;
	 protected PtOperBean operator;
	 protected PtDeptBean dept;

	 protected LogManager logMamager;

	 protected String reqxml;


	 static  Vector PARAM_MOTHOD = new Vector();;

	 public Action() {

	 }




	 public int execute(ActionRequest req, SQLResponse res) {
		  this.req = req;


		  this.res = res;
		  int result = 0;
		  ConnectionManager cm = ConnectionManager.getInstance();
		  try {
//			   if(operator == null) {
//					throw new Exception("操作员超时，请重新签到！");
//			   }

			   logMamager = new LogManager();

			   dc = cm.get();
			   
			   String actionName = this.getClass().getName();
			   DebugInfo di = (DebugInfo)ConnectionManager.badconn.get(dc.hashCode()+"");
			   if (di!=null)
				   di.setAction(actionName);
			   
			   dc.begin();
			   if(!LogAssistor.log()) {
					this.setSuccess("");
			   }
			   //memoryManager();

			   if (!req.getmethodName().trim().equals(""))
					result = call(req.getmethodName().trim());
			   else
					result = doBusiness();



		  } catch(Exception e) {

			   e.printStackTrace();
			   result = -1;
			   res.setResult(false);
			   res.setType(0);
			   res.setMessage(e.getMessage());
		  } finally {
			   try {
					if(result < 0) {
						 dc.rollback();
					} else {
						 dc.commit();
					}
					cm.release();
			   } catch(Exception e) {
					res.setResult(false);
					res.setType(0);
					res.setMessage(e.getMessage());
			   }
			   return 0;
		  }
	 }

	 public int doBusiness()
		  throws Exception {
		  System.out.println("Now in in Action.dddd");
		  return 0;
	 }

	 public void setSc(SessionContext sc) {
		  this.sc = sc;
	 }

	 public void setOperator(PtOperBean operator) {
		  this.operator = operator;
	 }

	 public DatabaseConnection getDatabaseConnection()
	 {
		 return dc;
	 }

	 public PtOperBean getOperator() {
		  return operator;
	 }
	 public void setDept(PtDeptBean dept) {
		this.dept = dept;
	}

	public PtDeptBean getDept() {
		return dept;
	}



	 public void setError(String message) {
		  setData(false, res.MESSAGE_TYPE, message);
	 }

	 public void setSuccess(String message) {
		  setData(true, res.MESSAGE_TYPE, message);
	 }

	 public void setXml(String xmlStr) {
		  setData(true, res.SELF_XML_TYPE, xmlStr);
	 }

	 public void setHtml(String htmlStr) {
		  setData(true, res.HTML_TYPE, htmlStr);
	 }

	 public void setData(boolean isSuccess, int type, String message) {
		  this.res.setResult(isSuccess);
		  this.res.setType(type);
		  this.res.setMessage(message);
	 }


	 public void memoryManager() {
		  long freeMem = Runtime.getRuntime().freeMemory();
		  long maxMem = Runtime.getRuntime().maxMemory();
		  long totalMem = Runtime.getRuntime().totalMemory();

		  double memrate = (double)freeMem / (double)maxMem;

		  if( memrate < 0.2) {
//               System.out.println(maxMem+"="+totalMem+"="+freeMem+"="+memrate);
//               System.out.println("=========start gc=============" + System.currentTimeMillis());
			   System.gc();
//               System.out.println("=========end gc=============" + System.currentTimeMillis());
		  }

	 }

	 public int call(String methodname) throws Exception {

		 Method method = null;
		 try {
			 method = this.getClass().getMethod(methodname, new Class[] {});
		 } catch (SecurityException ex) {
			 throw new Exception("方法访问权限不够：" + methodname);
		 } catch (NoSuchMethodException ex) {
			 throw new Exception("方法不存在：" + methodname);
		 }

		 Object obj = null;
		 try {
			 obj = method.invoke(this, PARAM_MOTHOD.toArray());

			 if (obj != null) {
				 return Integer.parseInt(obj.toString());
			 }

		 } catch (InvocationTargetException ex1) {
			 ex1.getTargetException().printStackTrace();
			 throw new Exception(ex1.getTargetException().getMessage());
		 } catch (IllegalArgumentException ex1) {
			 throw new Exception("传递的方法参数不对:" + methodname);
		 } catch (IllegalAccessException ex1) {
			 throw new Exception("方法访问权限不够：" + methodname);
		 }

		 return 0;
	 }

	 private void getLogMessage(){


		  if (logMamager.getErrorType().equals(logMamager.ERR_TYPE_HINT)){

			   this.res.setType(0);
			   this.res.setResult(true);
			   this.res.setMessage(logMamager.getMessage());

		 }else if (logMamager.getErrorType().equals(logMamager.ERR_TYPE_COMMON)||logMamager.getErrorType().equals(logMamager.ERR_TYPE_BAD)){

			  this.res.setType(this.res.SELF_XML_TYPE);
			  this.res.setResult(false);
			  this.res.setMessage(logMamager.getMessage());


		 }else{
			   this.res.setType(0);
			   this.res.setResult(false);
			   this.res.setMessage(logMamager.getMessage());

		 }

	 }



	 public void setMessage(String moudleID,String errorType,String errorindex,boolean isWriteLog,Exception exp){
		  logMamager.setMessage(moudleID,errorType,errorindex,isWriteLog,exp);
		  getLogMessage();

	}

	public void setMessage(String moudleID,String errorType,String errorindex,boolean isWriteLog){

		  logMamager.setMessage(moudleID,errorType,errorindex,isWriteLog);
		  getLogMessage();

	}

	public void setMessage(String moudleID,String errorType,String errorindex){
		  logMamager.setMessage(moudleID,errorType,errorindex);
		  getLogMessage();
   }
   public void setReqXml(String reqxml){
		this.reqxml = reqxml;
   }
   public String getReqXml(){
	   return this.reqxml;
  }



}
