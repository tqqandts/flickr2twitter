<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Twitter the World</title>
</head>

<body>

<%request.setAttribute("checkLogin", true); %>
<%@ include file="/header.jsp"%>
<p>&nbsp;</p>

<div>Welcome to "From Here to There System". Now logined as <b><%=user.getScreenName() %></b>.</div>
<div>You can create source, target and link them. Click <a href="/about.jsp">here</a> for help.</div>

<h2>Switch to another user account</h2>
<form action="/userOperation" method="post">
<table>

	<tr>
		<td>User Name:</td>
		<td><input type="text" name="<%=UserAccountServlet.PARA_EMAIL%>"></input></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="<%=UserAccountServlet.PARA_PASSWORD%>"></input><input
			type="hidden" name="<%=UserAccountServlet.PARA_OPT%>" value="<%=UserAccountServlet.OPT_LOGIN%>"></input></td>

	</tr>
	<tr>
		<td><input type="submit" value="Login" /></td>
		<td>or sign in with Open ID account</td>
	</tr>
	<tr>
		<td valign="top"><a href="/openid?op=Google" ><img border="0" src="http://openid.net/images/get-logos/google.png" alt="Google"></a></td>
		<td valign="top"><a href="/openid?op=Yahoo" ><img border="0" src="http://openid.net/wordpress-content/uploads/2009/11/yahoo.png" alt="Yahoo"></a></td>
	</tr>
</table>
</form>

<%@ include file="/footer.jsp"%>
</body>
</html>
