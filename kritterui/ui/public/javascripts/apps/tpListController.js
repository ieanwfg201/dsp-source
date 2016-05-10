angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('tpListController',function ($scope, $http) {
	$scope.status= "Pending";
	$scope.accountGuid = "";
	$scope.tpList = {}
	$scope.statusMap = [];

	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.accountGuid = pathComps[2]; 
		
		$scope.targetingListData =  Object.create(TableModel);
	    $scope.targetingListData.init("/entitylist" );
	    $scope.targetingListData.setFilterAttribute("entityType", "targeting_profile" );
	    $scope.targetingListData.setFilterAttribute("accountGuid", $scope.accountGuid );
	    $scope.targetingListData.setFilterAttribute("status", $scope.active );
	}
	
	$scope.init(); 
  
}); 