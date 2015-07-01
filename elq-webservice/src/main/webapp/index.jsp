<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
	<%@ page contentType='text/html;charset=UTF-8' %>
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
        <script src="resources/js/jquery-2.1.4.js"></script>
        <script src="resources/js/bootstrap.min.js" type="text/javascript"></script>
<link href="resources/css/fileinput.css" rel="stylesheet">
        <script src="resources/js/fileinput.js"></script>
<script src="resources/js/jquery.form.js"></script>	

	</head>
	<body class="container">
	<div class="jumbotron" style="background-image: url(resources/img/bg3.jpg); background-size: 100%;">
		
   <div style="size:100%; align:center; background-color:#FFFFFF"><img draggable="false" src="resources/img/versiya_logo_ua.gif" height="100" >
   </div>
		  </div>
  
 <div style="height:100%; min-height:100%; height:auto;  position:absolute;"> 
<div class="container" >
  <h2>Форма вводу</h2>
<p>Введіть дані для відображення на електронному табло</p> 
<button type="button" class="btn btn-warning" onclick="addInput()">Додати поле</button> 
 <form role="form" method="post" action="advertisement" >
   <div id="textForm">
	   
    <div class="form-group">
      <label for="ad">Повідомлення :</label>
      <input name="textInfo" type="text" class="form-control" id="ad" style='width:400px'>
    </div>
   
     </div>
<button type="submit" class="btn btn-danger">Завантажити</button> 
</form>
<hr>

 <div id="message"></div>
 
<form role="form" id="uploadForm" action="upload" method="post" enctype="multipart/form-data">
<label for="file">Завантажте відео :</label>
            <div class="input-group">
                <span class="input-group-btn">
                    <span class="btn btn-warning btn-file">
                        Пошук... <input name="file" type="file" id="file" accept="video/*">
                    </span>
                </span>
                <input type="text" class="form-control" style='width:320px' readonly>
            </div>
   <br>
 <div class="progress" id="progressbox" style="width:400px">
  <div  id="percent"class="progress-bar progress-bar-danger progress-bar-striped" role="progressbar"
  aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width:0%">
   0%
  </div>
</div>
   
   <button type="submit" class="btn btn-danger">Завантажити</button>
   
    </form>
   

      
       
 <br />

</div>

<div id="footer" style="position:absolute; bottom:0; background-image: url(resources/img/footer3.jpg); background-size:100%; width:100%; background-color:#000000; height:55px; border-radius:6px; padding:5px; padding-left:20px;padding-right:20px;">
<p style="background-image: url(resources/img/logo1.png); float:right; height:40px; width:133px; ">
</p>
<h4 style="color:#FFFFFF;">Copyright © 2015</h4>
</div>
</div>


<script type="text/javascript">
count = 0;
addInput =function (){
    field_area = document.getElementById('textForm')
var divFormGroup = document.createElement("div");
    var divInputGroup = document.createElement("div");
    var spanInputGroup = document.createElement("span");
    var divFormGroup = document.createElement("div");

    var label = document.createElement('label');
    label.innerHTML = 'Повідомлення :';
    var input = document.createElement("input");
    input.id = 'textInfo'+count;    input.name = 'textInfo';    input.type = "text";    input.className = 'form-control';    input.style.width = '400px';//Type of field - can be any valid input type like text,file,checkbox etc.
divFormGroup.className = 'form-group';
    divInputGroup.className = 'input-group'; divInputGroup.style.width = '400px';
    spanInputGroup.className = 'input-group-btn';
    divInputGroup.appendChild(input);
    divInputGroup.appendChild(spanInputGroup);
    divFormGroup.appendChild(label);
    divFormGroup.appendChild(divInputGroup);
    
    field_area.appendChild(divFormGroup);
    //create the removal link
    var removalLink = document.createElement('button');
    removalLink.type = 'button';
    removalLink.className = 'btn btn-danger';
    removalLink.onclick = function(){
        field_area.removeChild(divFormGroup)
    }
    var removalText = document.createTextNode('Видалити поле');
    removalLink.appendChild(removalText);
    spanInputGroup.appendChild(removalLink);
    count++
}


</script>
	</body>
	
</html>  