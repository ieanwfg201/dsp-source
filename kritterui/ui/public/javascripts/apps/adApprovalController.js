angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('adApprovalController',function ($scope, $http, $modal) { 
	
	$scope.status = ""; 
	
	$scope.init = function(){
		
		$scope.adListData =  Object.create(TableModel);
	    $scope.adListData.init("/entitylist" );
	    $scope.adListData.setFilterAttribute("entityType", "ad" );
	    $scope.adListData.setFilterAttribute("pageType", "operations" );
	    
		var path = $.url().attr('path');
		var pathComps = path.split("/"); 
		if(pathComps.length == 4){
			$scope.status = pathComps[3]; 
			$scope.adListData.setFilterAttribute("status", $scope.status ); 
		}else{
			$scope.adListData.setFilterAttribute("status","Pending" );
		}
			
	} 
	$scope.init(); 
	
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