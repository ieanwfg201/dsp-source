angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('siteApprovalController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	$scope.status = "";
	 
	$scope.init = function(){
		
		$scope.siteListData =  Object.create(TableModel);
	    $scope.siteListData.init("/entitylist" );
	    $scope.siteListData.setFilterAttribute("entityType", "site" );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.siteListData.setFilterAttribute("status", $scope.status);
		}else{
			$scope.siteListData.setFilterAttribute("status","Pending" );
		} 
	}
 
	$scope.init();
	
	$scope.updateSite = function (siteId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/site/workflow/form/'+siteId+'/'+action, 
			controller:siteEditerController,
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
	$scope.updateMultipleSite = function(action){
		var templateUrl = '/site/updateMultipleSite/'+action; 
		var ids = [];
		angular.forEach($scope.siteListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:updateMultipleSiteController,
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

var siteEditerController = function ($scope, $modalInstance) {

	  $scope.ok = function () {
		  var action_url = $('#siteWorkflowForm').attr('action'); 
		  $.post(action_url, $('#siteWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  });
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
	   
};

var siteWorkflowController = function ($scope, $modalInstance, campaigns) {

	  $scope.campaigns = campaigns;
	  $scope.ok = function () {
		  var action_url = $('#siteWorkflowForm').attr('action'); 
		  $.post(action_url, $('#siteWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
};
var updateMultipleSiteController = function ($scope, $modalInstance) {
	  location.reload(); 
};