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

/**
 * Servlet implementation class CalculateBalanceServlet
 */
public class CalculateBalanceServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CalculateBalanceServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		CalendarService myService = (CalendarService)session.getAttribute( "myService" );
		
		String date = request.getParameter( "balanceDate" );
		ExpenseCalculator calculator = new ExpenseCalculator();
		Map<String, Integer> balanceCategoryExpenseMap;
		try
		{
			balanceCategoryExpenseMap = calculator.getAllExpenseByCategory( 
					myService,
					Integer.parseInt( date.split( "-" )[ 0 ] ), 
					Integer.parseInt( date.split( "-" )[ 1 ] ), 
					(Map<String, String>)session.getAttribute( "expenseItemMap" ) );
			Map<String, Integer> balanceCategoryLimitMap = ExpenseItem.getCategoryLimit( myService, 
					Integer.parseInt( date.split( "-" )[ 0 ] ), 
					Integer.parseInt( date.split( "-" )[ 1 ] ) );
			session.setAttribute( "balanceBalance", 
					balanceCategoryExpenseMap.get( ExpenseCalculator.TOTAL_INCOME ) - balanceCategoryExpenseMap.get( ExpenseCalculator.TOTAL_EXPENSE ) );
			session.setAttribute( "balanceCategoryLimitMap", balanceCategoryLimitMap );
			session.setAttribute( "balanceCategoryExpenseMap", balanceCategoryExpenseMap );
			session.setAttribute( "action", "BALANCE" );
			request.getRequestDispatcher( "index.jsp" ).forward( request, response );
		}
		catch ( NumberFormatException e )
		{
			e.printStackTrace();
		}
		catch ( ServiceException e )
		{
			e.printStackTrace();
		}
	}
}
