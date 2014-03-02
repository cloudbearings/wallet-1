package com.gravitygroups.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.ServiceException;
import com.gravitygroups.cashcan.expense.ExpenseItem;
import com.gravitygroups.cashcan.gcalendar.api.GCalendarQuery;
import com.gravitygroups.cashcan.ui.CashCanApps;

/**
 * Servlet Filter implementation class LoadConfigFilter
 */
public class LoadConfigFilter implements Filter
{
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter( ServletRequest req, ServletResponse resp, FilterChain chain ) throws IOException, ServletException
	{
//		System.out.println( LoadConfigFilter.class );
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		HttpSession session = request.getSession();
		if ( session.getAttribute( "myService" ) != null )
		{
			List<CalendarEventEntry> expenseItemEvent;
			List<String> expenseItemList = new ArrayList<String>();
			if ( session.getAttribute( "expenseItemList" ) != null &&
					session.getAttribute( "expenseItemMap" ) != null &&
					session.getAttribute( "categoryLimitMap" ) != null )
			{
//				System.out.println("Config OK");
				chain.doFilter( request, response );
			}
			else
			{
//				System.out.printf("%s, %s\n", "expenseItemList", session.getAttribute( "expenseItemList" ) == null ? "null" : "not null" );
//				System.out.printf("%s, %s\n", "expenseItemMap", session.getAttribute( "expenseItemMap" ) == null ? "null" : "not null" );
//				System.out.printf("%s, %s\n", "categoryLimitMap", session.getAttribute( "categoryLimitMap" ) == null ? "null" : "not null" );
//				System.out.println("Load Config");
				
				try
				{
					CalendarService myService = (CalendarService)session.getAttribute( "myService" );
					expenseItemEvent = GCalendarQuery.retrieveByText( myService,
							CashCanApps.CALENDAR_NAME, CashCanApps.CATEGORY_FILE );
					for ( CalendarEventEntry event : expenseItemEvent )
					{
						String[] categoryElements = event.getPlainTextContent().split( "\\n" );
						
						for ( String item : categoryElements )
						{
							// read the name and group
							item = item.replaceAll( "\\s+", "" ).trim();
							expenseItemList.add( item );
							
							if ( item.contains( "其他" ) )
							{ 
								expenseItemList.add( ExpenseItem.USER_DEFINE_EXPENSE );
								expenseItemList.add( ExpenseItem.NO_EXPENSE );
							}
						}
					}
					session.setAttribute( "expenseItemList", expenseItemList );
					
					Map<String, String> expenseItemMap = new TreeMap<String, String>();
					expenseItemMap = ExpenseItem.loadExpenseItem2( myService );
					session.setAttribute( "expenseItemMap", expenseItemMap );
					
					
					// TODO: catch java.lang.IndexOutOfBoundsException 就可以得知有沒有當月的支出上限設定
					session.setAttribute( "categoryLimitMap", ExpenseItem.getCategoryLimit( myService, 
							Calendar.getInstance().get( Calendar.YEAR ),
							Calendar.getInstance().get( Calendar.MONTH ) + 1 ) );
					// pass the request along the filter chain
					chain.doFilter( req, resp );
				}
				catch ( ServiceException e )
				{
					session.setAttribute( "errorMessage", "無法載入設定和組態值，請重新登入來重新載入!!" );
				}
			}
			
		}
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init( FilterConfig fConfig ) throws ServletException
	{
	}

}
