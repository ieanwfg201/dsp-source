angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('videouploadController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.videouploadListData =  Object.create(TableModel);
	    $scope.videouploadListData.init("/entitylist" );
	    $scope.videouploadListData.setFilterAttribute("entityType", "materialvideoupload" );
		$scope.videouploadListData.setFilterAttribute("exchangeId", $("#exchange").val() );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.videouploadListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.videouploadListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
		$scope.updateVideoUpload = function (pubIncId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/videoupload/updatesingle/'+id +'/'+action, 
			controller:videouploadrefreshController,
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
	
	$scope.updateMultipleVideoUpload = function(action){
		var templateUrl = '/adxbasedexchanges/videoupload/updatemultiple/'+action; 
		var ids = [];
		angular.forEach($scope.videouploadListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:videouploadrefreshController,
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

var videouploadrefreshController = function ($scope, $modalInstance) {
	  location.reload(); 
};


