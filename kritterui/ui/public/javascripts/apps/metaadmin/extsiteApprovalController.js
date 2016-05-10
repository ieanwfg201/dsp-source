angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('extsiteApprovalController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.guid = ""; 
	$scope.status = "";
	 
	$scope.init = function(){
		
		$scope.extsiteListData =  Object.create(TableModel);
	    $scope.extsiteListData.init("/entitylist" );
	    $scope.extsiteListData.setFilterAttribute("entityType", "extsite" );
	    $scope.extsiteListData.setFilterAttribute("exchangeId", $("#exchange").val() );
	    $scope.extsiteListData.setFilterAttribute("osId", $("#osId").val() );
	    $scope.extsiteListData.setFilterAttribute("pageSize", 10000 );
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		if(pathComps.length == 4){
			$scope.status = pathComps[3];
			$scope.extsiteListData.setFilterAttribute("status", $scope.status);
		}else{
			$scope.extsiteListData.setFilterAttribute("status","Pending" );
		} 
	}
 
	$scope.init();
	
	$scope.updateextSite = function (id, action) {
		var modalInstance = $modal.open({
			templateUrl: '/extsite/workflow/form/'+id+'/'+action, 
			controller:extsiteEditerController,
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
	$scope.updateMultipleextSite = function(action){
		var templateUrl = '/extsite/updateMultipleextSite/'+action; 
		var ids = [];
		angular.forEach($scope.extsiteListData.selections, function(item) {
            ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl, 
			controller:updateMultipleextSiteController,
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

var extsiteEditerController = function ($scope, $modalInstance) {

	  $scope.ok = function () {
		  var action_url = $('#extsiteWorkflowForm').attr('action'); 
		  $.post(action_url, $('#extsiteWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  });
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
	   
};

var extsiteWorkflowController = function ($scope, $modalInstance, campaigns) {

	  $scope.campaigns = campaigns;
	  $scope.ok = function () {
		  var action_url = $('#extsiteWorkflowForm').attr('action'); 
		  $.post(action_url, $('#extsiteWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
};
var updateMultipleextSiteController = function ($scope, $modalInstance) {
	  location.reload(); 
};