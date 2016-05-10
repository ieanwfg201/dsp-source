angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('iddefinitionController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.status = "";
	 
	$scope.init = function(){
		
		$scope.iddefinitionListData =  Object.create(TableModel);
	    $scope.iddefinitionListData.init("/entitylist" );
	    $scope.iddefinitionListData.setFilterAttribute("entityType", "iddefinition" );
	    $scope.iddefinitionListData.setFilterAttribute("ids", $("#ids").val() );
	    $scope.iddefinitionListData.setFilterAttribute("id_guid", $("#id_guid").val() );
	    $scope.iddefinitionListData.setFilterAttribute("get_type", $("#get_type").val() );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.iddefinitionListData.setFilterAttribute("status", $scope.status);
		}else{
			$scope.iddefinitionListData.setFilterAttribute("status","Pending" );
		} 
	}
 
	$scope.init();
});

