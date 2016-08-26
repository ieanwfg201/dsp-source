angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('publisherController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = "";
	$scope.currentPage=0;
	$scope.maxPages = 10;
	$scope.pageSize = 50;
	$scope.totalItems = 500;
	$scope.siteList = {}
	$scope.statusMap = [];

	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.guid = pathComps[2];
		$scope.siteListData =  Object.create(TableModel);
	    $scope.siteListData.init("/entitylist" );
	    $scope.siteListData.setFilterAttribute("entityType", "site" );
	    $scope.siteListData.setFilterAttribute("accountGuid", $scope.guid );
	}
	 
	$scope.init(); 
 
});
