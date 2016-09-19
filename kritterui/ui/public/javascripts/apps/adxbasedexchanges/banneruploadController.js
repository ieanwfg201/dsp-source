angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('banneruploadController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.banneruploadListData =  Object.create(TableModel);
	    $scope.banneruploadListData.init("/entitylist" );
	    $scope.banneruploadListData.setFilterAttribute("entityType", "materialbannerupload" );
		$scope.banneruploadListData.setFilterAttribute("exchangeId", $("#exchange").val() );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.banneruploadListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.banneruploadListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
		$scope.updateBannerUpload = function (pubIncId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/bannerupload/updatesingle/'+id +'/'+action, 
			controller:banneruploadrefreshController,
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
	
	$scope.updateMultipleBannerUpload = function(action){
		var templateUrl = '/adxbasedexchanges/bannerupload/updatemultiple/'+action; 
		var ids = [];
		angular.forEach($scope.banneruploadListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:banneruploadrefreshController,
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

var banneruploadrefreshController = function ($scope, $modalInstance) {
	  location.reload(); 
};


