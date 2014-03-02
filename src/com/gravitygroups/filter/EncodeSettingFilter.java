package com.gravitygroups.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Servlet Filter implementation class EncodeSettingFilter
 */
public class EncodeSettingFilter implements Filter
{

	/**
	 * Default constructor.
	 */
	public EncodeSettingFilter()
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
		String encoding = "UTF-8";
		request.setCharacterEncoding( encoding );
		response.setCharacterEncoding( encoding );

		// pass the request along the filter chain
		chain.doFilter( request, response );
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init( FilterConfig fConfig ) throws ServletException
	{
		// TODO Auto-generated method stub
	}

}
