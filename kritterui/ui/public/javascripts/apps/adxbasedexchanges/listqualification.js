angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('listqualificationController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.qualificationdataListData =  Object.create(TableModel);
	    $scope.qualificationdataListData.init("/entitylist" );
	    $scope.qualificationdataListData.setFilterAttribute("entityType", "qualification" );
	    $scope.qualificationdataListData.setFilterAttribute("advIncId", $("#advIncId").val() );
	    
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.qualificationdataListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.qualificationdataListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
	
	$scope.updateQualification = function (advIncId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/qualification/updatesingle/'+advIncId +'/'+action, 
			controller:qualificationrefreshController,
			size: "medium" 
		});
		
		modalInstance.result.then(
			function () {
				$log.info('Modal ok: ');
			}, 
			function() {
				$log.info('Modal dismissed at: ' + new Date());
		    }
		);
	}; 
	
	$scope.updateMultipleQualification = function(action){
		var templateUrl = '/adxbasedexchanges/qualification/updatemultiple/'+action; 
		var ids = [];
		angular.forEach($scope.qualificationdataListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:qualificationrefreshController,
			size: "medium" 
		});
		modalInstance.result.then(
				function () {
					//$log.info('Modal ok: ');
				}, 
				function() {
					//$log.info('Modal dismissed at: ' + new Date());
			    }
			);
	};
});

var qualificationrefreshController = function ($scope, $modalInstance) {
	  location.reload(); 
};



