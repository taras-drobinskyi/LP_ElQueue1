$(document).on('change', '.btn-file :file', function() {
  var input = $(this),
      numFiles = input.get(0).files ? input.get(0).files.length : 1,
      label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
  input.trigger('fileselect', [numFiles, label]);
});

$(document).ready( function() {
    $('.btn-file :file').on('fileselect', function(event, numFiles, label) {
        
        var input = $(this).parents('.input-group').find(':text'),
            log = numFiles > 1 ? numFiles + ' files selected' : label;
        
        if( input.length ) {
            input.val(log);
        } else {
            if( log ) alert(log);
        }
        
    });
});

$(document).ready(function() {
	 $("#progressbox").hide();
var options = {
        beforeSend : function() {
                $("#progressbox").show();
                // clear everything
                $("#progressbar").width('0%');
                $("#message").empty();
                $("#percent").html("0%");
        },
        uploadProgress : function(event, position, total, percentComplete) {
                $("#progressbar").width(percentComplete + '%');
                $("#percent").html(percentComplete + '%');
                $("#percent").width(percentComplete + '%');

        },
        success : function() {
                $("#progressbar").width('100%');
                $("#percent").html('100%');
                $("#percent").width('100%');
                
        },
        complete : function(response) {
        	 $("#progressbox").hide();
        $("#message").html("<h3>Файл успішно завантажився!</h3>");
        },
        error : function() {
        $("#message").html("<font color='red'> ERROR: unable to upload files</font>");
        }
};
$("#uploadForm").ajaxForm(options);
});