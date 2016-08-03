angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('cacheInfoController',function ($scope, $http, $modal) {
	
	$scope.getcacheInfo = function(){
		$scope.callbackend();
	}
	$scope.callbackend = function(){
		$('#preloader').removeClass("hidden");
		var data = $('#cacheInfoForm').serialize();
		var action_url = $('#cacheInfoForm').attr('action'); 
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		$http.post(action_url, data).success( function(response){ 
			 $('#preloader').addClass("hidden"); 
			 $('#cachInfoDisplayWindow').html(response.data);
	    }); 
	}
});

