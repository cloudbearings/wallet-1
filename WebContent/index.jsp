<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>記帳</title>
<%@ include file="includes/includes.html"%>
<style type="text/css">

/* Sticky footer styles
  -------------------------------------------------- */
html,body {
	height: 100%;
	/* The html and body elements cannot have any padding or margin. */
}

/* Wrapper for page content to push down footer */
#wrap {
	min-height: 100%;
	height: auto !important;
	height: 100%;
	/* Negative indent footer by it's height */
	margin: 0 auto -60px;
}

/* Set the fixed height of the footer here */
#push,#footer {
	height: 30px;
}

#footer {
	background-color: #f5f5f5;
}

/* Lastly, apply responsive CSS fixes as necessary */
@media ( max-width : 767px) {
	#footer {
		margin-left: -20px;
		margin-right: -20px;
		padding-left: 20px;
		padding-right: 20px;
	}
}

/* Custom page CSS
  -------------------------------------------------- */
/* Not required for template or sticky footer method. */
.container {
	width: auto;
	max-width: 800px;
}

.container .credit {
	margin: 20px 0;
}
</style>
</head>
<body>
	<div id="wrap">
	<div class="container">
		<div class="tabbable">
			<!-- 顯示Tab -->
			<input type="hidden" id="currentAction" name="currentAction" value="${ sessionScope.action }">
			
			<ul class="nav nav-tabs" id="tab">
				<li><a href="#record-tab" data-toggle="tab">記帳</a></li>
				<li><a href="#balance-tab" data-toggle="tab">查看結餘</a>
				<li><a href="#category-item-detail-tab" data-toggle="tab">分類明細</a>
				<li><a href="#item-total-tab" data-toggle="tab">項目總和</a>
				<li><a href="#month-balance-chart-tab" data-toggle="tab">統計圖</a></li>
				<li><a href="#statistic-chart-tab" data-toggle="tab">走勢圖</a></li>
				<li><a href="#expense-category-tab" data-toggle="tab">收支分類</a></li>
				<li><a href="#category-limit-tab" data-toggle="tab">分類支出上限</a></li>
			</ul>

			<div class="tab-content">
				<!-- 紀錄收支項目 -->
				<div class="tab-pane active" id="record-tab">
				<h2>新增一筆收支紀錄</h2>
					<form class="form-horizontal" action="record.do" method="POST"
						name="record-form">
						<div class="control-group">
							<label class="control-label" for="selectDate">1.選擇日期</label>
							<div class="controls">
								<jsp:useBean id="today" class="java.util.Date" scope="page"></jsp:useBean>
								<fmt:formatDate value="${ today }" var="todayFormat"
									pattern="yyyy-MM-dd" type="date" />
								<input type="text" id="date" name="date" placeholder="選擇日期"
									value="${ todayFormat }">
								<button class="btn" id="calendar-trigger">按我選擇日期</button>
								<script>
									Calendar.setup({
										trigger : "calendar-trigger",
										inputField : "date",
										onSelect : function() {
											this.hide();
										}
									});
								</script>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="選擇項目">2.選擇項目</label>
							<div class="controls">
								<select name="itemOption" id="record-select-item"
									onchange='checkSelectedItem( this.options[ this.selectedIndex ].text )'>
									<c:forEach items="${sessionScope.expenseItemList}" var="item">
										<c:choose>
											<c:when test="${fn:startsWith(item,':')}">
												<option value="" disabled>
													------------
													<c:out value="${fn:replace(item,':','') }"></c:out>
													------------
												</option>
											</c:when>
											<c:otherwise>
												<option value="${item}">
													<c:out value="${item}"></c:out>
												</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="inputAmount">3.輸入金額</label>
							<div class="controls">
								<input type="text" id="amount" name="amount" placeholder="輸入金額">
							</div>
						</div>
						<input type="submit" class="btn btn-primary btn-large btn-block" value="紀錄" />
					</form>
					
					<c:if test="${ sessionScope.message != null }">
						<div class="alert alert-success">
							<c:out value="${sessionScope.message}"></c:out>
						</div>
					</c:if>
					<c:if test="${ sessionScope.errorMessage != null }">
						<div class="alert alert-error">
							<strong>錯誤:</strong>
							<c:out value="${ sessionScope.errorMessage }"></c:out>
						</div>
					</c:if>
					<c:if test="${ sessionScope.recordCategoryExpenseMap != null }">
						<table class="table table-striped">
							<tr class="info">
								<td><strong>分類</strong></td>
								<td><strong>小計</strong></td>
								<td><strong>剩餘額度</strong></td>
							</tr>
							<c:forEach items="${ sessionScope.recordCategoryExpenseMap }"
								var="item">
								<tr>
									<td><c:out value="${ item.key }"></c:out></td>
									<td><c:out value="${ item.value }"></c:out></td>
									<c:choose>
										<c:when test="${ item.key != '總收入' && item.key != '總支出' && item.key != '收入' }">
											<c:choose>
												<c:when test="${ sessionScope.recordCategoryLimitMap[ item.key ] - item.value <= 0 }">
													<td><font color="red"><c:out value="${ sessionScope.recordCategoryLimitMap[ item.key ] - item.value }"></c:out></font></td>
												</c:when>
												<c:otherwise>
													<td><c:out value="${ sessionScope.recordCategoryLimitMap[ item.key ] - item.value }"></c:out></td>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<td><c:out value="略"></c:out></td>
										</c:otherwise>
									</c:choose>
								</tr>
							</c:forEach>
							<tr class="success">
								<td>手頭現金結餘</td>
								<td><c:out value="${ sessionScope.recordBalance }"></c:out></td>
							</tr>
						</table>
					</c:if>
				</div>

				<!-- 計算月結餘 -->
				<div class="tab-pane" id="balance-tab">
					<form class="form-horizontal" action="calculate.balance.do" method="get">
						<input type="text" id="balanceDate" name="balanceDate" placeholder="選擇日期"
									value="${ todayFormat }">
						<button class="btn" id="balance-date">按我選擇日期</button><br><br>
						<script>
							Calendar.setup({
								trigger : "balance-date",
								inputField : "balanceDate",
								onSelect : function() {
									this.hide();
								}
							});
						</script>
						<input type="submit" value="查詢結餘" class="btn btn-large btn-primary btn-block">
					</form>

					<c:if test="${ sessionScope.balanceCategoryExpenseMap != null }">
						<table class="table table-striped">
							<tr class="info">
								<td><strong>分類</strong></td>
								<td><strong>小計</strong></td>
								<td><strong>剩餘額度</strong></td>
							</tr>
							<c:forEach items="${ sessionScope.balanceCategoryExpenseMap }"
								var="item">
								<tr>
									<td><c:out value="${ item.key }"></c:out></td>
									<td><c:out value="${ item.value }"></c:out></td>
									<c:choose>
										<c:when test="${ item.key != '總收入' && item.key != '總支出' && item.key != '收入' }">
											<c:choose>
												<c:when test="${ sessionScope.balanceCategoryLimitMap[ item.key ] - item.value <= 0 }">
													<td><font color="red"><c:out value="${ sessionScope.balanceCategoryLimitMap[ item.key ] - item.value }"></c:out></font></td>
												</c:when>
												<c:otherwise>
													<td><c:out value="${ sessionScope.balanceCategoryLimitMap[ item.key ] - item.value }"></c:out></td>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<td><c:out value="略"></c:out></td>
										</c:otherwise>
									</c:choose>
								</tr>
							</c:forEach>
							<tr class="success">
								<td>手頭現金結餘</td>
								<td><c:out value="${ sessionScope.balanceBalance }"></c:out></td>
							</tr>
						</table>
					</c:if>
				</div>
				
				<!-- 分類明細 -->
				<div class="tab-pane" id="category-item-detail-tab">
					<form class="form-horizontal" action="categoryDetailQuery.do" method="get">
						<input type="text" id="categoryItemDetailDate" name="categoryItemDetailDate" placeholder="選擇日期"
									value="${ todayFormat }">
						<button class="btn" id="category-item-detail">按我選擇日期</button><br><br>
						<script>
							Calendar.setup({
								trigger : "category-item-detail",
								inputField : "categoryItemDetailDate",
								onSelect : function() {
									this.hide();
								}
							});
						</script>
						<input type="submit" value="查詢" class="btn btn-large btn-primary btn-block">
					</form>
					
					<table class="table table-striped">
						<tr class="info">
							<td><strong>分類</strong></td>
							<td><strong>項目明細加總</strong></td>
						</tr>
						<c:forEach items="${ sessionScope.categoryDetailMap }"
							var="categoryDetail">
							<tr>
								<td><c:out value="${ categoryDetail.key }"></c:out></td><td></td><td>
							</tr>
							<c:forEach items="${ categoryDetail.value }" var="detail">
								<tr><td></td><td><c:out value="${ detail }"></c:out></td><tr>
							</c:forEach>
						</c:forEach>
					</table>
				</div>
				
				<!-- 項目總和 -->
				<div class="tab-pane" id="item-total-tab">
					<form class="form-horizontal" action="" method="get">
						<input type="text" id="itemTotalDate" name="itemTotalDate" placeholder="選擇日期"
									value="${ todayFormat }">
						<button class="btn" id="item-total-date">按我選擇日期</button><br><br>
						<script>
							Calendar.setup({
								trigger : "item-total-date",
								inputField : "itemTotalDate",
								onSelect : function() {
									this.hide();
								}
							});
						</script>
						<input type="submit" value="查詢" class="btn btn-large btn-primary btn-block">
					</form>

					
				</div>

				<!-- 顯示月結算統計圖表 -->
				<div class="tab-pane" id="month-balance-chart-tab"></div>
				
				<!-- 顯示收支統計趨勢圖 -->
				<div class="tab-pane" id="statistic-chart-tab"></div>
				
				
				<!-- 設定分類支出上限 -->
				<div class="tab-pane" id="category-limit-tab">
					<form class="form-horizontal" action="get.categorylimit.do" method="get">
						<input type="text" id="categoryLimitDate" name="categoryLimitDate" placeholder="選擇日期"
									value="${ todayFormat }">
						<button class="btn" id="category-limit-date">按我選擇日期</button>
						<script>
							Calendar.setup({
								trigger : "category-limit-date",
								inputField : "categoryLimitDate",
								onSelect : function() {
									this.hide();
								}
							});
						</script>
						<input type="submit" value="查詢分類支出上限額度" class="btn btn-primary">
					</form>
					<c:if test="${ sessionScope.queryCategoryLimitMap != null }">
						<form name="setting-category-limit-form" method="post" action="update.categorylimit.do">
							<input type="hidden" name="queryCategoryLimitDate" value="${ sessionScope.queryCategoryLimitDate }">
							<table class="table table-striped">
								<tr class="success"><td>分類</td><td>支出上限</td></tr>
								<c:forEach items="${ sessionScope.queryCategoryLimitMap }" var="entry">
									<tr>
										<td><c:out value="${ entry.key }"></c:out></td>
										<td><input type="text" name="${ entry.key }" value="${ entry.value }"></td>
									</tr>
								</c:forEach>
							</table>
							<input type="submit" class="btn btn-large btn-block btn-primary" type="button" value="更新">
							<c:if test="${ sessionScope.queryCategoryMessage != null }">
								<div class="alert alert-success">
									<c:out value="${sessionScope.queryCategoryMessage}"></c:out>
								</div>
							</c:if>
							<c:if test="${ sessionScope.queryCategoryErrorMessage != null }">
								<div class="alert alert-error">
									<strong>錯誤:</strong>
									<c:out value="${ sessionScope.queryCategoryErrorMessage }"></c:out>
								</div>
							</c:if>
						</form>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	</div>
	<div id="footer">
		<form method="POST" action="logout">
			<input type="submit" class="btn" value="登出" />
		</form>
	</div>
</body>
</html>