angular.module('app.ui.form.ctrls',  [  "trNgGrid" ] ).
controller('pmpListController',function ($scope, $http, $modal) { 
	
	$scope.type = "";
	$scope.active = true;
	$scope.pmpList = {}; 
        
        $scope.updateDeal = function(dealId,action){
                var templateUrl = '/pmp/' + dealId + '/updateStatus/'+action;
                var modalInstance = $modal.open({
                        templateUrl: templateUrl,
                        controller:updateDealController,
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
       
        var updateDealController = function ($scope, $modalInstance) 
        {
            location.reload();
        };

	$scope.init = function(){ 
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.type = pathComps[2];
		$scope.active = $.url().param('active'); 
		$scope.pmpData =  Object.create(TableModel);
	        $scope.pmpData.init("/entitylist" );
	        $scope.pmpData.setFilterAttribute("entityType", "pmp_deal" );
                $scope.updateDeal();
	}
  
	$scope.init();
});
