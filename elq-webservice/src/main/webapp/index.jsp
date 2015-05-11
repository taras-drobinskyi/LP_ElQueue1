<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
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
	<div class="jumbotron" style="background-image: url(1.jpg); background-size: 100%;">
  </div>
   
<div class="container">
  <h2>Форма вводу</h2>
<p>Введіть дані для відображення на електронному табло</p> 
<button type="button" class="btn btn-success" onclick="addInput()">Додати поле</button> 
 <form role="form" method="get" action="advertisement" >
   <div id="textForm">
	   
    <div class="form-group">
      <label for="ad">Повідомлення :</label>
      <input name="textInfo" type="text" class="form-control" id="ad" style='width:400px'>
    </div>
   
     </div>
<button type="submit" class="btn btn-primary">Завантажити</button> 
</form>
<hr>

 
<form role="form" action="upload" method="post" enctype="multipart/form-data">
<label for="file">Завантажте відео :</label>
            <div class="input-group">
                <span class="input-group-btn">
                    <span class="btn btn-primary btn-file">
                        Пошук... <input name="file" type="file" id="file" accept="video/*">
                    </span>
                </span>
                <input type="text" class="form-control" style='width:200px' readonly>
            </div>
   <br>
   <button type="submit" class="btn btn-primary">Завантажити</button>
   
    </form>

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
    var removalText = document.createTextNode('Remove Field');
    removalLink.appendChild(removalText);
    spanInputGroup.appendChild(removalLink);
    count++
}
</script>
	</body>
	
</html>  