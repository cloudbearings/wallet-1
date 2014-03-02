<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>登入</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<%@ include file="includes/includes.html" %>
<script type="text/javascript">
$(document).ready( function() {
	$('#username').focus();
});
</script>
<!-- Le styles -->
<style type="text/css">
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

.form-signin {
	max-width: 300px;
	padding: 19px 29px 29px;
	margin: 0 auto 40px;
	background-color: #fff;
	border: 1px solid #e5e5e5;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
}

.form-signin .form-signin-heading,.form-signin .checkbox {
	margin-bottom: 10px;
}

.form-signin input[type="text"],.form-signin input[type="password"] {
	font-size: 16px;
	height: auto;
	margin-bottom: 15px;
	padding: 7px 9px;
}
</style>
</head>

<!-- 如果登入過後又轉到此頁，則自動轉到index -->
<c:if test="${ sessionScope.myService != null }">
	<c:redirect url="index.jsp"></c:redirect>
</c:if>
<body>
	<div class="container">
		<form class="form-signin" method="post" action="login">
			<h2 class="form-signin-heading">登入</h2>
			<div class="input-prepend">
				<input type="text" class="span2" id="username" placeholder="Gmail帳號" name="username" >
				<span class="add-on">@gmail.com</span>
			</div>
			<input type="password" class="input-block-level" placeholder="密碼" id="password" name="password">
			<label class="checkbox"> <input type="checkbox" name="rememberMe" value="1" checked>記住我</label>
			<button class="btn btn-large btn-primary" type="submit">登入</button>
		</form>
		<c:if test="${!empty sessionScope.errorMessage}">
			<div class="alert alert-error">
				<h4>錯誤: </h4><c:out value="${sessionScope.errorMessage}"></c:out>
			</div>
		</c:if>
	</div>
</body>
</html>
