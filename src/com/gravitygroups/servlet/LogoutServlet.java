package com.gravitygroups.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogoutServlet
 */
public class LogoutServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		session.invalidate();
		/*
		session.removeAttribute( "myService" );
		session.removeAttribute( "errorMessage" );
		session.removeAttribute( "currentBalance" );
		session.removeAttribute( "queryBalance" );
		session.removeAttribute( "categoryExpenseMap" );
		session.removeAttribute( "categoryLimitMap" );
		session.removeAttribute( "queryCategoryLimitMap" );
		session.removeAttribute( "queryCategoryExpenseMap" );
		session.removeAttribute( "expenseItemList" );
		session.removeAttribute( "expenseItemMap" );
		session.removeAttribute( "action" );
		session.removeAttribute( "categoryLimitMap" );
		*/
		
		Cookie usernameCookie = new Cookie( "username", "" );
		usernameCookie.setMaxAge( 0 );
		response.addCookie( usernameCookie );
		
		Cookie passwdCookie = new Cookie( "password", "" );
		passwdCookie.setMaxAge( 0 );
		response.addCookie( passwdCookie );
		response.sendRedirect( "login.jsp" );
	}
}
