angular.module('app.ui.form.ctrls', ['ngPasswordStrength']).
    controller('accountController',function ($scope, $http) {
    	$scope.showPaymentOptions = false;
    	$scope.showAdvOptions = true;
    	
    	 
    	
    	$scope.type_id = $("#type_id").attr("value"); 
    	if($scope.type_id=="directpublisher"){
    		$scope.showPaymentOptions = true;
        	$scope.showAdvOptions = false;
    	} 		
});
