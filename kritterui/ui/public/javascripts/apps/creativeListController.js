angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('creativeListController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.accountGuid = "";
	$scope.currentPage=0;
	$scope.maxPages = 5;
	$scope.pageSize = 10;
	$scope.totalItems = 30;
	$scope.creativeList = {data:[]}
	$scope.statusMap = [];
	$scope.tableParams ={};
	
	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.accountGuid = pathComps[2];	
		
		$scope.creativeListData =  Object.create(TableModel);
	    $scope.creativeListData.init("/entitylist" );
	    $scope.creativeListData.setFilterAttribute("entityType", "creative" );
	    $scope.creativeListData.setFilterAttribute("accountGuid", $scope.accountGuid ); 
	}
	
	$scope.init();
	
	 
});