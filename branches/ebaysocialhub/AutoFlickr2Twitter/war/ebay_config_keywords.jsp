<%@ page language="java"
	import="com.googlecode.flickr2twitter.impl.ebay.*,java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="stylesheets/site.css" rel="stylesheet" type="text/css" />
<link href="stylesheets/style.css" rel="stylesheet" type="text/css" />
<link href="stylesheets/content.css" rel="stylesheet" type="text/css" />
<title>SocialHub</title>
</head>

<body>
<div id="container"><%@ include file="header.jsp"%>
<div id="content">
	<h1>Enter the keywords you want to search and follow</h1>
	<hr/>
	<div id="middle">
		<%
			String keywords = request
					.getParameter(EbayConfigKeywordsServlet.PARA_SEARCH_KEYWORDS);
			boolean hasKeywords = (keywords != null) && (keywords.length() > 0);
		%>
		<form action="/ebay_config_keywords.jsp" method="post" name="searchebaykeywords">
			<table class="border_table">
				<tr>
					<td class="first_ebay"">Keywords:</td>
					<td><input type="text"
						name="<%=EbayConfigKeywordsServlet.PARA_SEARCH_KEYWORDS%>" /></td>
				</tr>
				<tr>
					<td/>
					<td><a href="#" onclick="searchebaykeywords.submit();"><img src="/images/button_search.png" alt=""/></a></td>
				</tr>
			</table>
		</form>
	
		<%
			if (hasKeywords) {
				FindItemsDAO findItemsDao = new FindItemsDAO();
				List<EbayItem> items = findItemsDao.findItemsByKeywordsFromProduction(keywords, 10);
				// show user details if found a user
				if (items != null) {
				%>
			
				<h1>Search Result for '<%=keywords%>'</h1>
				<form action="/ebayConfigkeywords" method="post" name="showebaykeywords">
					<input type="hidden" value="<%=keywords%>" name="<%=EbayConfigKeywordsServlet.PARA_KEYWORDS%>"/>
					<table class="border_table">
					<tr>
						<td><a href="#" onclick="showebaykeywords.submit();"><img src="/images/button_submit.png" alt=""/></a></td>
						<td/>
					</tr>
					<%for (EbayItem item : items) { %>
						<tr>
							<% if(item.getGalleryURL() != null) { %>
								<td><img src="<%=item.getGalleryURL()%>" alt="<%=item.getTitle()%>"/></a></td>
							<% } else { %>
							<td/>
							<% }%>
							<td><a href="<%=item.getViewItemURL()%>"><%=item.getTitle()%></a></td>
						</tr>
						<% } %>
					</table>
				</form>
				<%
				} else {
				// notify user that there is no search result
		%>
		<h1>Search Result</h1>
		<table class="border_table">
			<tr>
				<td class="first_ebay">No Items Found for Keywords:</td>
				<td><%=keywords%></td>
			</tr>
		</table>
		<%
				}
			}
		%>
	</div>
</div>
<%@ include file="footer.jsp"%></div>
</body>
</html>