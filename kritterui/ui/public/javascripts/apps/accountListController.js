angular.module('app.ui.form.ctrls',  [  "trNgGrid" ] ).
controller('accountListController',function ($scope, $http, $modal) { 
	
	$scope.type = "";
	$scope.active = true;
	$scope.accountList = {}; 
	
	$scope.init = function(){ 
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.type = pathComps[2];
		$scope.active = $.url().param('active'); 
		$scope.accountData =  Object.create(TableModel);
	    $scope.accountData.init("/entitylist" );
	    $scope.accountData.setFilterAttribute("entityType", "account" );
	    $scope.accountData.setFilterAttribute("accountType", $scope.type );
	    if(typeof($scope.active ) != "undefined")
	    		$scope.accountData.setFilterAttribute("status", $scope.active );
	}
  
	$scope.init();
 
	$scope.updateAccount = function (guid, action) {
		var modalInstance = $modal.open({
			templateUrl: "/accounts/updateStatus/"+guid+"/"+action, 
			controller:accountWorkflowController,
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

var accountWorkflowController = function ($scope, $modalInstance) {

	  $scope.ok = function () {
		  var action_url = $('#accountWorkflowForm').attr('action'); 
		  $.post(action_url, $('#accountWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 	  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
	   
};