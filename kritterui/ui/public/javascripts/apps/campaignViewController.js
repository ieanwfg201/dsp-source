angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('campaignViewController',function ($scope, $http, $modal) {
	$scope.status= "";
	$scope.campid = "";

	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.campid = pathComps[3];	 
		$scope.adListData =  Object.create(TableModel);
	    $scope.adListData.init("/entitylist" );
	    $scope.adListData.enableMultiSelect = true;
	    $scope.adListData.setFilterAttribute("entityType", "ad" ); 
	    $scope.adListData.setFilterAttribute("campaignId", $scope.campid );
	}

	$scope.init();

	$scope.updateMultipleAds = function(action){
		var templateUrl = '/advertiser/campaigns/ads/updateMultipleAds/'+action; 
		var ids = [];
		angular.forEach($scope.adListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:updateMultipleAdsController,
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
	
	$scope.updateAd = function (adId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/advertiser/campaigns/ads/'+adId +'/updateStatus/'+action, 
			controller:adWorkFlowController,
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
});


var adWorkFlowController = function ($scope, $modalInstance) {
	  $scope.ok = function () {
		  var action_url = $('#adWorkflowForm').attr('action'); 
		  $.post(action_url, $('#adWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
 };
 var updateMultipleAdsController = function ($scope, $modalInstance) {
	  location.reload(); 
};
