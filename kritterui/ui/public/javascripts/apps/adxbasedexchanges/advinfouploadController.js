angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('advinfouploadController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.advinfouploadListData =  Object.create(TableModel);
	    $scope.advinfouploadListData.init("/entitylist");
	    $scope.advinfouploadListData.setFilterAttribute("entityType", "materialadvinfoupload");
		$scope.advinfouploadListData.setFilterAttribute("exchangeId", $("#exchange").val() );
		$scope.advinfouploadListData.setFilterAttribute("adxBasedExchangesStates", $("#adxBasedExchangesStates").val() );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.advinfouploadListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.advinfouploadListData.setFilterAttribute("status","Pending" );
		} 
	};
	$scope.init();
	$scope.updateAdvInfoUpload = function (id, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/advinfoupload/updatesingle/'+id +'/'+action, 
			controller:advinfouploadrefreshController,
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
	
	$scope.updateMultipleAdvInfoUpload = function(action){
		var templateUrl = '/adxbasedexchanges/advinfoupload/updatemultiple/'+action; 
		var ids = [];
		angular.forEach($scope.advinfouploadListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:advinfouploadrefreshController,
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

var advinfouploadrefreshController = function ($scope, $modalInstance) {
	  location.reload(); 
};


