angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('testExchangeController',function ($scope, $http, $modal) {
	
	$scope.exchangeresponsedebug = function(testExchangetype){
		if(testExchangetype == 'DEBUG'){
			$("#exchange_type").val('DEBUG');
		}else if(testExchangetype == 'RESPONSE'){
			$("#exchange_type").val('RESPONSE');
		}
		$scope.callbackend();
	}
	$scope.callbackend = function(){
		$('#preloader').removeClass("hidden");
		var data = $('#testExchangeForm').serialize();
		var action_url = $('#testExchangeForm').attr('action'); 
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		$http.post(action_url, data).success( function(response){ 
			 $('#preloader').addClass("hidden"); 
			 $('#advertDisplayWindow').html(response.data);
	    }); 
	}
});

