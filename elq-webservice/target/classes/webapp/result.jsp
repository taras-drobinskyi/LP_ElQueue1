<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
	<%@ page import="java.util.List" %>
	<%@page contentType='text/html;charset=UTF-8' %> 
<html> 
	<head> 
		<meta http-equiv="Content-Type" 
			content="text/html; charset=UTF-8">
<title>Електронна черга</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
        <script src="resources/js/jquery-2.1.4.min.js"></script>
        <script src="resources/js/bootstrap.min.js" type="text/javascript"></script>
<link href="resources/css/fileinput.css" rel="stylesheet">
        <script src="resources/js/fileinput.js"></script>

	</head>
	<body class="container">
	<div class="jumbotron" style="background-image: url(resources/img/1.jpg); background-size: 100%;">
  </div>
   
  
<div class="container">
<h1>Результат вводу даних</h1>
<%
if(request.getAttribute("message") instanceof List){
List<String> resultList =(List<String>) request.getAttribute("message");
for(String ad: resultList){
	%><div class = "well" style = "width:400px"><%=ad %></div><%;
}
}else if(request.getAttribute("message") instanceof String){
	String file =(String) request.getAttribute("message");
	%><div class = "well" style = "width:400px"><%=file %></div><%;
}



%>

</div>

	</body>
	
</html>  