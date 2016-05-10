angular.module('app.ui.form.ctrls' ).
controller('campaignListController',function ($scope, $http, $modal) {
	
	$scope.status= "Pending";
	$scope.currentPage=0;
	$scope.advtguid = "";
	$scope.maxPages = 5;
	$scope.pageSize = 10;
	$scope.totalItems = 30;

	$scope.init = function(){
		$scope.status= $.url().param("status");
		$scope.pageSize = $.url().param("pageSize");
		$scope.currentPage =  $.url().param("pageNo");
	}
	
	$scope.init();
	
	$scope.fetch = function (advertiser,page) {
		var url = "/advertiser/"+advertiser+"/campaigns?status="+$scope.status+"&pageNo="+page+"&pageSize="+$scope.pageSize;
		location= url;
	};

	$scope.open = function (size,  guid, id, action) {
		var modalInstance = $modal.open({
			templateUrl: '/advertiser/campaigns/workflow/form?id='+id+'&accountGuid='+guid +'&action='+action, 
			controller:campaignWorkflowController,
			size: size 
		});

		modalInstance.result.then(
				function () { 
				}, 
				function() { 
				}
		);
	};  
}); 

var campaignWorkflowController = function ($scope, $modalInstance) {

	  $scope.ok = function () {
		  var action_url = $('#campaignWorkflowForm').attr('action'); 
		  $.post(action_url, $('#campaignWorkflowForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
	   
};