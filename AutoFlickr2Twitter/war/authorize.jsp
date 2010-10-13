<%@ page language="java"
	import="com.gmail.yuyang226.autoflickr2twitter.datastore.*,com.gmail.yuyang226.autoflickr2twitter.datastore.model.*,com.gmail.yuyang226.autoflickr2twitter.servlet.*,java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />

<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="refresh" content="15" > 
<title>Twitter the World - Authorize your Source and Target</title>
</head>
<body>
<%
	request.setAttribute("checkLogin", true);
%>
<%@ include file="/header.jsp"%>

<h2>Authorize</h2>
<ul>
	<li>
	<h3>Source List</h3>
	</li>

	<table>

		<tr>
			<%
				List<UserSourceServiceConfig> sourceSvcs = MyPersistenceManagerFactory
						.getUserSourceServices(user);
				List<UserTargetServiceConfig> targetSvcs = MyPersistenceManagerFactory
						.getUserTargetServices(user);

				String currentProviderID = null;
				currentProviderID = "flickr";
				Map<String, Object> currentData = (Map<String, Object>) session
						.getAttribute(currentProviderID);
			%>
			<td>
			<!-- onclick="location.reload()"  -->
			<a  
				href="/oauth?<%=OAuthServlet.PARA_OPT%>=<%=OAuthServlet.OPT_AUTH_SOURCE%>&<%=OAuthServlet.PARA_PROVIDER_ID%>=<%=currentProviderID%>" target="_blank">Authorize Flicker Account</a></td>
			<td>
			<%
				if (currentData == null) {
					out.print(" <-Please click the link on the left side. It will lead you to the offical authorize page. After authorization, please refresh this page and click the Confirm Authorize link. ");
				} else {
			%>
				<a href="/oauth?<%=OAuthServlet.PARA_OPT%>=<%=OAuthServlet.OPT_AUTH_SOURCE_CONFIRM%>&<%=OAuthServlet.PARA_PROVIDER_ID%>=<%=currentProviderID%>">Confirm Authorize</a>
			<%
				}
			%>
			<h4>Already Authorized Accounts:</h4>
			<ul>
			<%
				for (UserSourceServiceConfig sourceSvc : sourceSvcs) {
					if (currentProviderID.equalsIgnoreCase(sourceSvc
							.getServiceProviderId())) {
						out.println("<li>" + sourceSvc.getServiceUserName()
								+ "  <a href=\"" + sourceSvc.getUserSiteUrl()
								+ "\">Go to my page</a></li>");
					}
				}
			%>
				</ul>
			</td>
		</tr>

	</table>

	<li>
	<h3>Target List</h3>
	</li>
	<table>
		<tr>
			<td>
			<%
				currentProviderID = "twitter";
				currentData = (Map<String, Object>) session
						.getAttribute(currentProviderID);
			%>
			<a
				href="/oauth?<%=OAuthServlet.PARA_OPT%>=<%=OAuthServlet.OPT_AUTH_TARGET%>&<%=OAuthServlet.PARA_PROVIDER_ID%>=twitter" target="_blank">Authorize Twitter Account</a></td>
			<td>
			<%
				if (currentData == null) {
					out.print(" <-Please click the link on the left side. It will lead you to the offical authorize page. After authorization, please refresh this page and click the Confirm Authorize link. ");
				} else {
			%>
				<a href="/oauth?<%=OAuthServlet.PARA_OPT%>=<%=OAuthServlet.OPT_AUTH_TARGET_CONFIRM%>&<%=OAuthServlet.PARA_PROVIDER_ID%>=<%=currentProviderID%>">Confirm Authorize</a>
			<%
				}
				out.println(" Already Authorized Accounts: ");
				for (UserTargetServiceConfig targetSvc : targetSvcs) {
					if (currentProviderID.equalsIgnoreCase(targetSvc
							.getServiceProviderId())) {
						out.println(targetSvc.getServiceUserName() + "    ");
					}
				}
			%>
			</td>
		</tr>

		<tr>
			<td><a href="/sinacall.jsp" target="_new">Authorize Sina
			Account</a></td>
			<td>Here we can add a button so user can test the authorize
			result</td>
		</tr>
	</table>

</ul>
<%@ include file="/foot.jsp"%>
</body>
</html>