package com.gravitygroups.util;

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
	private FilterConfig filterConfig = null;
	private String encoding = "";
	
	/**
	 * @param filterConfig
	 */
	public EncodeSettingFilter( FilterConfig filterConfig )
	{
		super();
		this.filterConfig = filterConfig;
		this.encoding = this.filterConfig.getInitParameter( "Encoding" );
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException
	{
		String encoding = "UTF-8";
		if ( this.encoding != null )
			encoding = this.encoding;
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
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
	}

}
