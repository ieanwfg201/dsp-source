angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('audienceMetadataController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	 
	$scope.init = function(){
		
		$scope.audienceMetadataListData =  Object.create(TableModel);
	    $scope.audienceMetadataListData.init("/entitylist" );
	    $scope.audienceMetadataListData.setFilterAttribute("entityType", "audiencemetadata" );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.audienceMetadataListData.setFilterAttribute("status", "Pending");
		}else{
			$scope.audienceMetadataListData.setFilterAttribute("status","Pending" );
		} 
	}
	$scope.init();
		$scope.updateAudienceMetadata = function (id, action) {
		var modalInstance = $modal.open({
			templateUrl: '/audience/audiencemetadata/updatesingle/'+id +'/'+action, 
			controller:audienceMetadatarefreshController,
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

var audienceMetadatarefreshController = function ($scope, $modalInstance) {
	  location.reload(); 
};


