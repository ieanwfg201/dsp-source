angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('testclickController',function ($scope, $http, $modal) {
	
	$scope.performclick = function(){
		$scope.callbackend();
	}
	
	$scope.callbackend = function(){
		$('#preloader').removeClass("hidden");
		var data = $('#testclickForm').serialize();
		var action_url = $('#testclickForm').attr('action'); 
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		$http.post(action_url, data).success( function(response){ 
			 $('#preloader').addClass("hidden"); 
			 $('#advertDisplayWindow').html('<iframe class="container col-sm-12" style="height: 400px;" src="'+response.data+'"></iframe>');
			 $('#advertUrlDisplay').html(response.data);
	    }); 
	}
});

