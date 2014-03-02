package com.gravitygroups.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.gravitygroups.cashcan.expense.ExpenseItem;

/**
 * Servlet implementation class GetCagtegoryLimitServlet
 */
public class GetCagtegoryLimitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		String date = request.getParameter( "categoryLimitDate" );
		session.setAttribute( "queryCategoryLimitMap", ExpenseItem.getCategoryLimit( 
				(CalendarService)session.getAttribute( "myService" ), 
				Integer.parseInt( date.split( "-" )[ 0 ] ),
				Integer.parseInt( date.split( "-" )[ 1 ] ) ) );
		session.setAttribute( "action", "CATEGORY_LIMIT" );
		session.setAttribute( "queryCategoryLimitDate", date );
		session.removeAttribute( "message" );
		session.removeAttribute( "errorMessage" );
		request.getRequestDispatcher( "index.jsp" ).forward( request, response );
	}
}
