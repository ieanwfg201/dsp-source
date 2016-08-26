angular.module('app.ui.form.ctrls',  [  "trNgGrid" ] ).
controller('pmpListController',function ($scope, $http, $modal) { 
	
	$scope.type = "";
	$scope.active = true;
	$scope.pmpList = {}; 
	
	$scope.init = function(){ 
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.type = pathComps[2];
		$scope.active = $.url().param('active'); 
		$scope.pmpData =  Object.create(TableModel);
	        $scope.pmpData.init("/entitylist" );
	        $scope.pmpData.setFilterAttribute("entityType", "pmp_deal" );
	}
  
	$scope.init();
});
