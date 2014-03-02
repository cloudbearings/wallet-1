package com.gravitygroups.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.ServiceException;
import com.gravitygroups.cashcan.expense.ExpenseCalculator;
import com.gravitygroups.cashcan.expense.ExpenseItem;
import com.gravitygroups.cashcan.gcalendar.api.GCalendarQuery;
import com.gravitygroups.cashcan.ui.CashCanApps;

/**
 * Servlet implementation class RecordSerlvet
 */
public class RecordSerlvet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		CalendarService myService = (CalendarService)request.getSession().getAttribute( "myService" );
		try
		{
			String date = request.getParameter( "date" );
			String itemOption = request.getParameter( "itemOption" );
			String amount = request.getParameter( "amount" );
			String description = "";
			if ( itemOption.equals( ExpenseItem.USER_DEFINE_EXPENSE ) )
				description = request.getParameter( "itemName" ) + "$";
			else if ( itemOption.equals( ExpenseItem.NO_EXPENSE ) )
				description = ExpenseItem.NO_EXPENSE + "$";
			else
				description = itemOption + "$";
	
			if ( !description.contains( "收入" ) && !description.contains( ExpenseItem.NO_EXPENSE ) )
				description += "-";
			else
				description += "+";
			description += amount;
			try
			{
				GCalendarQuery.addEvent(
						myService,
						CashCanApps.CALENDAR_NAME,
						description,
						"",
						date,
						date );
				session.setAttribute( "message", "帳款: 在" + date + ", " + description + "新增完成囉!!" );
				
				ExpenseCalculator calcutor = new ExpenseCalculator();
				Map<String, Integer> recordCategoryExpenseMap = calcutor.getAllExpenseByCategory( 
						myService,
						Integer.parseInt( date.split( "-" )[ 0 ] ), 
						Integer.parseInt( date.split( "-" )[ 1 ] ), 
						(Map<String, String>)session.getAttribute( "expenseItemMap" ) );
				Map<String, Integer> recordCategoryLimitMap = ExpenseItem.getCategoryLimit( myService, 
						Integer.parseInt( date.split( "-" )[ 0 ] ), 
						Integer.parseInt( date.split( "-" )[ 1 ] ) );

				session.setAttribute( "recordBalance", 
						recordCategoryExpenseMap.get( ExpenseCalculator.TOTAL_INCOME ) - recordCategoryExpenseMap.get( ExpenseCalculator.TOTAL_EXPENSE ) );
				session.setAttribute( "recordCategoryExpenseMap", recordCategoryExpenseMap );
				session.setAttribute( "recordCategoryLimitMap", recordCategoryLimitMap );
				session.setAttribute( "action", "RECORD" );
				session.removeAttribute( "errorMessage" );
			}
			catch ( ServiceException e )
			{
				e.printStackTrace();
				session.removeAttribute( "message" );
				session.setAttribute( "errorMessage", e.getMessage() );
			}
		}
		catch ( NullPointerException e )
		{
			e.printStackTrace();
		}
		
		request.getRequestDispatcher( "index.jsp" ).forward( request, response );
	}

}
