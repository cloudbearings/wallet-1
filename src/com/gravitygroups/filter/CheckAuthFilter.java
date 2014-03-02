package com.gravitygroups.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;
import com.gravitygroups.cashcan.gcalendar.api.GCalendarQuery;
import com.gravitygroups.util.StringUtils;

/**
 * Servlet Filter implementation class CheckAuthFilter
 */
public class CheckAuthFilter implements Filter
{
	/**
	 * Default constructor.
	 */
	public CheckAuthFilter()
	{
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException
	{
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpResp = (HttpServletResponse)response;
		HttpSession session = ( (HttpServletRequest)request ).getSession();

		String uri = httpReq.getRequestURI();
		session.removeAttribute( "errorMessage" );
		session.removeAttribute( "message" );
		if ( session.getAttribute( "myService" ) == null )
		{
//			System.out.println( uri + " 未登入!!");
			Cookie[] cookies = httpReq.getCookies();
			String username = "";
			String password = "";
			if ( cookies != null )
			{
				for ( Cookie c : cookies )
				{
					if ( c.getName().contains( "username" ) )
						username = StringUtils.decrypt( c.getValue() );
					if ( c.getName().contains( "password" ) )
						password = StringUtils.decrypt( c.getValue() );
				}
				
			}
			if ( ( username == null || password == null ) ||
				( username.isEmpty() || password.isEmpty() ) )
				httpResp.sendRedirect( "login.jsp" );
			else
			{
				CalendarService myService;
				try
				{
					myService = GCalendarQuery.login( username, password );
					session.setAttribute( "myService", myService );
					session.removeAttribute( "errorMessage" );
					session.removeAttribute( "message" );
					
					// pass the request along the filter chain
					chain.doFilter( request, response );
				}
				catch ( AuthenticationException e )
				{
					httpResp.sendRedirect( "login.jsp" );
				}
			}
		}
		else
		{
//			System.out.println( uri + " 已登入!!");
		
			// pass the request along the filter chain
			chain.doFilter( request, response );
			
			if ( uri.contains( "login.jsp" ) )
				httpResp.sendRedirect( "index.jsp" );
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init( FilterConfig fConfig ) throws ServletException
	{
	}

}
