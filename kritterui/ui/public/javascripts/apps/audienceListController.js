angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('audienceListController',function ($scope, $http) {
	$scope.status= "Pending";
	$scope.accountGuid = "";
	$scope.tpList = {}
	$scope.statusMap = [];

	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.accountGuid = pathComps[2]; 
		
		$scope.audienceListData =  Object.create(TableModel);
	    $scope.audienceListData.init("/entitylist" );
	    $scope.audienceListData.setFilterAttribute("entityType", "audienceList" );
	    $scope.audienceListData.setFilterAttribute("accountGuid", $scope.accountGuid );
	}
	
	$scope.init(); 
  
}); 