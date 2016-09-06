angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('listadxbasedexchangesmetadataController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.adxbasedexchangesmetadataListData =  Object.create(TableModel);
	    $scope.adxbasedexchangesmetadataListData.init("/entitylist" );
	    $scope.adxbasedexchangesmetadataListData.setFilterAttribute("entityType", "adxbasedexchangesmetadata" );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.adxbasedexchangesmetadataListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.adxbasedexchangesmetadataListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
});


