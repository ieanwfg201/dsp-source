angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('testsiteController',function ($scope, $http, $modal) {
	
	$scope.getadordebug = function(testsitetype,debug_url){
		if(testsitetype == 'DEBUG'){
			$scope.debug();
		}else if(testsitetype == 'GETAD'){
			$scope.getad(debug_url);
		}
	}
	
	$scope.getad = function(debug_url){
		$('#preloader').removeClass("hidden"); 
		 var uri = $('#testsiteForm').serialize();
         var url = debug_url+'?'+uri;
         $('#advertDisplayWindow').html('<iframe class="container col-sm-12" style="height: 400px;" src="'+url+'"></iframe>');
         $('#advertUrlDisplay').html(url);
         $('#preloader').addClass("hidden");
	}
	$scope.debug = function(){
		$("#site_guid").val($("#site-id").val());
		$scope.callbackend();
	}
	
	$scope.callbackend = function(){
		$('#preloader').removeClass("hidden");
		var data = $('#testsiteForm').serialize();
		var action_url = $('#testsiteForm').attr('action'); 
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		$http.post(action_url, data).success( function(response){ 
			 $('#preloader').addClass("hidden"); 
			 $('#advertDisplayWindow').html(response.data);
			 $('#advertUrlDisplay').html('');
	    }); 
	}
});

