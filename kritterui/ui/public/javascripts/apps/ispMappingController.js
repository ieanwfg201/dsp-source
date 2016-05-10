angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('ispMappingController',function ($scope, $http, $modal) { 
	
	$scope.status = ""; 
	
	$scope.init = function(){
		
		$scope.ispMappingListData =  Object.create(TableModel);
	    $scope.ispMappingListData.init("/entitylist" );
	    $scope.ispMappingListData.setFilterAttribute("entityType", "ispMapping" );
	    $scope.ispMappingListData.setFilterAttribute("pageType", "operations" );
	    $scope.ispMappingListData.setFilterAttribute("countryId", $("#country").val() );
	    
		var path = $.url().attr('path');
		var pathComps = path.split("/"); 
		if(pathComps.length == 4){
			$scope.status = pathComps[3]; 
			$scope.ispMappingListData.setFilterAttribute("status", $scope.status ); 
		}else{
			$scope.ispMappingListData.setFilterAttribute("status","Pending" );
		}
			
	} 
	$scope.init(); 
	
	
	$scope.updateIspMapping = function (countryName, ispName) {
		var modalInstance = $modal.open({
			templateUrl: '/ispMapping/'+countryName+'/'+ispName+'/workflow/create', 
			controller:ispMappingWorkFlowController,
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

	$scope.rejectIspMapping = function (id) {
		var modalInstance = $modal.open({
			templateUrl: '/ispMapping/deleteMapping/'+id+'/delete', 
			controller:ispMappingRejectWorkFlowController,
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


var ispMappingWorkFlowController = function ($scope, $modalInstance) {

	 
	  $scope.ok = function () {
		  var action_url = $('#ispMappingWorkflowForm').attr('action'); 
		  $.post(action_url, $('#ispMappingWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };

};

var ispMappingRejectWorkFlowController = function ($scope, $modalInstance) {
	location.reload();
};
