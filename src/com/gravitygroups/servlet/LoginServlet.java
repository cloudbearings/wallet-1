package com.gravitygroups.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.AccessLevelProperty;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.SelectedProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.gravitygroups.cashcan.gcalendar.api.GCalendarQuery;
import com.gravitygroups.cashcan.ui.CashCanApps;
import com.gravitygroups.util.StringUtils;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		try
		{
			String username = "";
			String password = "";
			/*
			Cookie[] cookies = request.getCookies();
			for ( Cookie c : cookies )
			{
				if ( c.getName().contains( "username" ) )
					username = c.getValue();
				if ( c.getName().contains( "password" ) )
					password = c.getValue();
			}
			if ( username.isEmpty() )
			*/
			username = request.getParameter( "username" );
			
			if ( !username.endsWith( "@gmail.com" ) )
				username += "@gmail.com";
//			if ( password.isEmpty() )
			password = request.getParameter( "password" );
			
			CalendarService myService = GCalendarQuery.login( username, password );
			HttpSession session = request.getSession(); 
			session.setAttribute( "myService", myService );
			session.removeAttribute( "errorMessage" );
			session.removeAttribute( "message" );
			
			if ( request.getParameter( "rememberMe" ) != null ) // 記住我!
			{
				Cookie usernameCookie = new Cookie("username", StringUtils.encrypt( username ) );
				Cookie passwdCookie = new Cookie("password", StringUtils.encrypt( password ) );
				response.addCookie( usernameCookie );
				response.addCookie( passwdCookie );
			}
			
			validateCalendar( myService );
			
			// NOTE: 這裡的forward servlet要跟這doPost一樣是用POST!!
			request.getRequestDispatcher( "index.jsp" ).forward( request, response );
		}
		catch ( AuthenticationException e )
		{
			request.getSession().setAttribute( "errorMessage", "錯誤的使用者或密碼，登入失敗!!" );
			request.getRequestDispatcher( "login.jsp" ).forward( request, response );
		}
		catch ( ServiceException e )
		{
			request.getSession().setAttribute( "errorMessage", "服務失敗!! 錯誤訊息: " + e.getMessage() );
			request.getRequestDispatcher( "login.jsp" ).forward( request, response );
		}
	}
	
	private static void validateCalendar( CalendarService myService ) throws IOException, ServiceException
	{
		List<String> calendarList = new ArrayList<String>();
		calendarList = GCalendarQuery.getCalendarNames( myService );
		if ( !calendarList.contains( CashCanApps.CALENDAR_NAME ) )
		{
			URL postUrl;
			try
			{
				postUrl = new URL( GCalendarQuery.FEED_URL );
				CalendarEntry newCalendar = new CalendarEntry();
				newCalendar.setTitle( new PlainTextConstruct( "CareMoney" ) );
				newCalendar.setSummary( new PlainTextConstruct( "Care Money Apps" ) );
				newCalendar.setHidden( HiddenProperty.FALSE );
				newCalendar.setTimeZone( new TimeZoneProperty( "Asia/Taipei" ) ); 
				newCalendar.setColor( new ColorProperty( "#2952A3" ) );
				newCalendar.setSelected( new SelectedProperty( "true" ) );
				newCalendar.setAccessLevel( AccessLevelProperty.OWNER );
				newCalendar.addLocation( new Where( "", "", "Taipei" ) );
				myService.insert( postUrl, newCalendar );
			}
			catch ( MalformedURLException e )
			{
				e.printStackTrace();
			}
		}
	}
}
