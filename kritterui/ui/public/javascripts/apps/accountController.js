angular.module('app.ui.form.ctrls', ['ngPasswordStrength','angularFileUpload', 'multi-select']).
    controller('accountController',function ($scope, $http, $upload) {
    	$scope.showPaymentOptions = false;
    	$scope.showAdvOptions = true;
    	$scope.firstIndustryCode_url = "/metadata/options/tier1mmaindustry";
		$scope.secondIndustryCode_url = "/metadata/options/tier2mmaindustry/";
    	
		$scope.firstIndustryCode_msmodel = Object.create(MultiSelectModel);  
		$scope.firstIndustryCode_msmodel.init($scope.firstIndustryCode_url, 'valueArray', true);
	
		$scope.secondIndustryCode_msmodel = Object.create(MultiSelectModel);  
		$scope.secondIndustryCode_msmodel.init($scope.secondIndustryCode_url, 'valueArray', true);
		$scope.firstIndustryCode_msmodel.addDependent("secondIndustryCode", $scope.secondIndustryCode_msmodel); 
    	
    	$scope.type_id = $("#type_id").attr("value"); 
    	if($scope.type_id=="directpublisher"){
    		$scope.showPaymentOptions = true;
        	$scope.showAdvOptions = false;
    	}
    	$scope.init= function(){ 
		}
    	 		
});
