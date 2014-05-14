package com.ccb.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pub.platform.db.ConnectionManager;


public class BaseJsp extends HttpServlet implements HttpJspPage {
	private static final Log logger = LogFactory.getLog(BaseJsp.class);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			jspInit();
			_jspService(request,response);
			jspDestroy();
		} catch (Exception  e) {
			logger.debug("Ò³ÃæÅ×³öÀýÍâ",e);
			//response.getWriter().println(e.getMessage());
		} finally {
			ConnectionManager.getInstance().release();
		}
	}

	public void _jspService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			IOException {
	}

	public void jspInit() {
	}

	public void jspDestroy() {
	}
}
