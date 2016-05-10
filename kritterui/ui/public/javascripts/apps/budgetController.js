angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('budgetController',function ($scope, $http, $modal) {

	$scope.status = "Approved";
	$scope.guid = "";
	$scope.ioList= {};
	$scope.ioList.data= [];
	 
	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/"); 
		$scope.guid = pathComps[2];  
	}
    
	$scope.updateIOList = function (status) { 
		var url = "/advertiser/"+$scope.guid+"/insertion-orders/" +status;
		$http.get(url).success( function(response){ 
     		$scope.ioList.data = response.list;    		 
       });
	};
 
	$scope.init(); 
 
});
