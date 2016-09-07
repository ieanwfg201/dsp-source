angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('adpositiongetController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.adpositiongetListData =  Object.create(TableModel);
	    $scope.adpositiongetListData.init("/entitylist" );
	    $scope.adpositiongetListData.setFilterAttribute("entityType", "adpositionget" );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.adpositiongetListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.adpositiongetListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
		$scope.updateAdPositoionGetNew = function (pubIncId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/adpositionget/insertnew/'+pubIncId +'/'+action, 
			controller:adpositiongetController,
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
		$scope.updateAdPositoionGet = function (pubIncId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/adxbasedexchanges/adpositionget/updatesingle/'+pubIncId +'/'+action, 
			controller:adpositiongetController,
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
	
	$scope.updateMultipleAdPositoionGet = function(action){
		var templateUrl = '/adxbasedexchanges/adpositionget/updatemultiple/'+action; 
		var ids = [];
		angular.forEach($scope.adpositiongetListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:adpositiongetController,
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

var adpositiongetController = function ($scope, $modalInstance) {
	  location.reload(); 
};


