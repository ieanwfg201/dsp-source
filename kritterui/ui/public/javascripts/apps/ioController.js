/** Controllers */
angular.module('app.ui.form.ctrls', []).
controller('ioController',function ($scope, $modal, $http) {

	$scope.open = function (size,ioId, guid, action) {
		var modalInstance = $modal.open({
			templateUrl: '/insertion-order/workflow/editFrom/'+guid+'/'+ioId+'/'+action, 
			controller:ioEditerController,
			size: size 
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

var ioEditerController = function ($scope, $modalInstance) {

	  $scope.ok = function () {
		  var action_url = $('#ioEditForm').attr('action'); 
		  $.post(action_url, $('#ioEditForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel');
			  location.reload(); 
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
	   
};
	
