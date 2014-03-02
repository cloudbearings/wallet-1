package com.gravitygroups.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.ServiceException;
import com.gravitygroups.cashcan.expense.ExpenseCalculator;
import com.gravitygroups.cashcan.expense.ExpenseItem;
import com.gravitygroups.cashcan.expense.ExpensePair;

/**
 * Servlet implementation class CategoryDetailQueryServlet
 */
public class CategoryDetailQueryServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		session.setAttribute( "action", "CATEGORY_DETAIL_QUERY" );
		CalendarService myService = (CalendarService)session.getAttribute( "myService" );
		String date = request.getParameter( "categoryItemDetailDate" );
		int year = Integer.parseInt( date.split( "-" )[ 0 ] );
		int month = Integer.parseInt( date.split( "-" )[ 1 ] );
		
		// 項目名稱，該項目總合
		Map<String, Integer> itemTotalMap = new TreeMap<String, Integer>();
		
		// 項目名稱，所屬分類
		Map<String, String> expenseItemMap = (Map<String, String>)session.getAttribute( "expenseItemMap" );
		
		// 分類，屬於該分類的所有細項名稱和總和
		Map<String, List<Map.Entry<String, Integer>>> categoryDetailMap = new TreeMap<String, List<Map.Entry<String, Integer>>>();
		try
		{
			List<ExpensePair> allExpenseList = ExpensePair.getAllExpensePair( myService, year, month );
			for ( ExpensePair expense : allExpenseList )
			{
				if ( expense.getItemName().isEmpty() )
					continue;
				
				int itemValue = 0;
				if ( ExpensePair.isIncome( expense ) )
					itemValue = Integer.parseInt( expense.getItemValue().replace( "+", "" ) );
				else
					itemValue = Integer.parseInt( expense.getItemValue().replace( "-", "" ) );
				
				if ( itemTotalMap.containsKey( expense.getItemName() ) )
				{
					Integer total = itemTotalMap.get( expense.getItemName() );
					total += itemValue;
					itemTotalMap.put( expense.getItemName(), total );
				}
				else
				{
					itemTotalMap.put( expense.getItemName(), itemValue );
				}
				
				if ( expenseItemMap.containsKey( expense.getItemName() ) )
				{
					
				}	
				else // 項目是不屬於任何分類，加到｢其他｣
				{
				}
			}
			
//			System.out.printf("%s, %s\n", categoryDetailMap, expenseItemMap );
			
			// 將每個項目依照分類儲存
			for ( Map.Entry<String, Integer> e : itemTotalMap.entrySet() )
			{
				List<Map.Entry<String, Integer>> itemTotalList = new ArrayList<Map.Entry<String, Integer>>();
				try
				{
					if ( categoryDetailMap.containsKey( expenseItemMap.get( e.getKey() ) ) )
						itemTotalList = categoryDetailMap.get( expenseItemMap.get( e.getKey() ) );
					itemTotalList.add( e );
					categoryDetailMap.put( expenseItemMap.get( e.getKey() ), itemTotalList );
				}
				catch ( NullPointerException ee )
				{
					if ( e.getKey().contains( ExpenseItem.NO_EXPENSE ) )
						continue;
					if ( categoryDetailMap.containsKey( "其他" ) )
						itemTotalList = categoryDetailMap.get( "其他" );
					itemTotalList.add( e );
					categoryDetailMap.put( "其他", itemTotalList );
				}
			}
			session.setAttribute( "categoryDetailMap", categoryDetailMap );
			request.getRequestDispatcher( "index.jsp" ).forward( request, response );
			/*
			for ( Map.Entry<String, List<Map.Entry<String, Integer>>> e : categoryDetailMap.entrySet() )
			{
				System.out.println( e.getKey() );
				{
					List<Map.Entry<String, Integer>> itemTotalList = categoryDetailMap.get( e.getKey() );
					for ( Map.Entry<String, Integer> item : itemTotalList )
					{
						System.out.printf("\t%s, %d\n", item.getKey(), item.getValue() );
					}
				}
			}

			// TODO: 顯示其他
			categoryDetailMap.put( "其他", new ArrayList<Map.Entry<String, Integer>>() );
			System.out.println("其他");
			for ( ExpensePair expense : allExpenseList )
			{
				if ( !expenseItemMap.containsKey( expense.getItemName() ) && 
						!expense.getItemName().equals( ExpenseItem.NO_EXPENSE ) )
				{
					
					System.out.printf("\t%s, %s\n", expense.getItemName(), expense.getItemValue() );
//					List<Map.Entry<String, Integer>> itemTotalList = categoryDetailMap.get(  ) );
//					itemTotalList.add( e );
//					categoryDetailMap.put( expenseItemMap.get( e.getKey() ), itemTotalList );
				}
			}
			*/
		}
		catch ( ServiceException e )
		{
			e.printStackTrace();
		}
	}

}
