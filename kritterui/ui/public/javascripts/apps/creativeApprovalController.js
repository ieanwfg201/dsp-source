angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('creativeApprovalController',function ($scope, $http, $modal) { 
	
	$scope.status = ""; 
	
	$scope.init = function(){
		
		$scope.creativeListData =  Object.create(TableModel);
	    $scope.creativeListData.init("/entitylist" );
	    $scope.creativeListData.setFilterAttribute("entityType", "creative" );
	    $scope.creativeListData.setFilterAttribute("pageType", "operations" );
	    
		var path = $.url().attr('path');
		var pathComps = path.split("/"); 
		if(pathComps.length == 4){
			$scope.status = pathComps[3]; 
			$scope.creativeListData.setFilterAttribute("status", $scope.status ); 
		}else{
			$scope.creativeListData.setFilterAttribute("status","Pending" );
		}
			
	} 
	$scope.init(); 
	
	$scope.updateCreative = function (creativeId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/advertiser/campaigns/creative/'+creativeId +'/updateStatus/'+action, 
			controller:creativeWorkFlowController,
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
	
	$scope.updateMultipleCreative = function(action){
		var templateUrl = '/advertiser/campaigns/creative/updateMultipleCreative/'+action; 
		var ids = [];
		angular.forEach($scope.creativeListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:updateMultipleCreativeController,
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


var creativeWorkFlowController = function ($scope, $modalInstance) {
 
	  $scope.ok = function () {
		  var action_url = $('#creativeWorkflowForm').attr('action'); 
		  $.post(action_url, $('#creativeWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
};
var updateMultipleCreativeController = function ($scope, $modalInstance) {
	  location.reload(); 
};