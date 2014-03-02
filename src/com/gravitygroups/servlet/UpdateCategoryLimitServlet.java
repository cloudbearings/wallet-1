package com.gravitygroups.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.gravitygroups.cashcan.expense.ExpenseItem;
import com.gravitygroups.cashcan.gcalendar.api.GCalendarQuery;
import com.gravitygroups.cashcan.ui.CashCanApps;

/**
 * Servlet implementation class UpdateCategoryLimitServlet
 */
public class UpdateCategoryLimitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		CalendarService myService = (CalendarService)session.getAttribute( "myService" );
		String date = request.getParameter( "queryCategoryLimitDate" );
		Map<String, Integer> currentCategoryLimitMap = new TreeMap<String, Integer>();
		if ( session.getAttribute( "queryCategoryLimitMap" ) != null )
		{
			currentCategoryLimitMap = (Map<String, Integer>)session.getAttribute( "queryCategoryLimitMap" );
		}
		else
		{
			currentCategoryLimitMap = ExpenseItem.getCategoryLimit( 
					(CalendarService)session.getAttribute( "myService" ), 
					Integer.parseInt( date.split( "-" )[ 0 ] ),
					Integer.parseInt( date.split( "-" )[ 1 ] ) );
		}
		
		StringBuffer limitStringBuffer = new StringBuffer();
		for ( Map.Entry<String, Integer> entry : currentCategoryLimitMap.entrySet() )
		{
			String category = entry.getKey();
			if ( category.contains( "收入" ) || category.contains( "存款" ) )
				continue;
			int limit = Integer.parseInt( request.getParameter( entry.getKey() ) );
			limitStringBuffer.append( category + "=" + limit + "\n" );
		}
		
		List<CalendarEventEntry> expenseItemEvent;
		try
		{
			expenseItemEvent = GCalendarQuery.retrieveByDateAndText( 
					myService, 
					Integer.parseInt( date.split( "-" )[ 0 ] ), 
					Integer.parseInt( date.split( "-" )[ 1 ] ), 
					CashCanApps.CALENDAR_NAME, 
					CashCanApps.CATEGORY_LIMIT_FILE );
			
			if ( expenseItemEvent.size() == 0 )
			{
				GCalendarQuery.addEvent( myService, 
						CashCanApps.CALENDAR_NAME, 
						CashCanApps.CATEGORY_LIMIT_FILE,
						limitStringBuffer.toString(),
						date.split( "-" )[ 0 ] + "-" + date.split( "-" )[ 1 ] + "-01",
						date.split( "-" )[ 0 ] + "-" + date.split( "-" )[ 1 ] + "-01" );
				session.setAttribute( "queryCategoryMessage", "完成寫入" + date + "收支上限額度設定!" );
			}
			else
			{
				CalendarEventEntry event = expenseItemEvent.get( 0 );
				event.setContent( new PlainTextConstruct( limitStringBuffer.toString() ) );
				GCalendarQuery.updateEvent( myService, CashCanApps.CALENDAR_NAME, event );
				session.setAttribute( "queryCategoryMessage", "完成更新" + date + "收支上限額度設定!" );
			}
		}
		catch ( Exception e )
		{
			session.setAttribute( "queryCategoryErrorMessage", "更新收支上限失敗!!" );
		}
		
		session.setAttribute( "queryCategoryLimitMap", ExpenseItem.getCategoryLimit( 
				(CalendarService)session.getAttribute( "myService" ), 
				Integer.parseInt( date.split( "-" )[ 0 ] ),
				Integer.parseInt( date.split( "-" )[ 1 ] ) ) );
		session.setAttribute( "action", "CATEGORY_LIMIT" );
		session.setAttribute( "queryCategoryLimitDate", date );
		request.getRequestDispatcher( "index.jsp" ).forward( request, response );
	}

}
