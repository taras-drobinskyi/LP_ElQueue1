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
<link href="resources/css/bootstrap.css" rel="stylesheet">
        <script src="resources/js/jquery-2.1.4.min.js"></script>
        <script src="resources/js/bootstrap.min.js" type="text/javascript"></script>
<link href="resources/css/fileinput.css" rel="stylesheet">
        <script src="resources/js/fileinput.js"></script>

	</head>
	<body class="container" >
	<div class="jumbotron" style="background-image: url(resources/img/bg3.jpg); background-size: 100%;">
	<div style="size:100%; align:center; background-color:#FFFFFF"><img draggable="false" src="resources/img/versiya_logo_ua.gif" height="100" >
   </div>
	
  </div>
   
  <div style="height:100%; min-height:100%; height:auto;  position:absolute;">
<div class="container" >
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
  

<div  style="position:absolute; bottom:0; background-image: url(resources/img/footer3.jpg); background-size:100%; width:100%; background-color:#000000; height:55px; border-radius:6px; padding:5px; padding-left:20px;padding-right:20px;">
<p style="background-image: url(resources/img/logo1.png); float:right; height:40px; width:133px; ">
</p>
<h4 style="color:#FFFFFF;">Copyright © 2015</h4>
</div>

</div>
	</body>
	
</html>  