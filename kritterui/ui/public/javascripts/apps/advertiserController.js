angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('advertiserController',function ($scope, $http, $modal) {
	$scope.status= "";
	$scope.guid = "";
	$scope.currentPage=0;
	$scope.maxPages = 5;
	$scope.pageSize = 10;
	$scope.totalItems = 30;
	$scope.campaignList = {}
	$scope.statusMap = [];

	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/");
		$scope.guid = pathComps[2];
		if(pathComps.length == 5)
			$scope.status = pathComps[4];
		
		$scope.campaignListData =  Object.create(TableModel);
	    $scope.campaignListData.init("/entitylist" );
	    $scope.campaignListData.setFilterAttribute("entityType", "campaign" );
	    $scope.campaignListData.setFilterAttribute("accountGuid", $scope.guid );
	    $scope.campaignListData.setFilterAttribute("status", $scope.active );
	}
	
	 
  
	$scope.init();
	
	
	$scope.statusLabel = function(code){
		return $scope.statusMap[code];
	}
    
//	$scope.fetch = function () {
//		
//		var url = "/advertiser/"+ $scope.guid+"/campaigns";
//		if($scope.status != ""){
//			url += "?status="+$scope.status;
//		}
//		$http.get(url).success( function(response){
//			$scope.statusMap = response.statusMap;
//     		 $scope.campaignList.data = response.list;
//     		 $scope.campaignList.count = response.count;
//     		 
//       });
//	};

//	$scope.init();
//	$scope.fetch();
	
	$scope.updateSelectedCampaigns = function(action){
		var templateUrl = '/advertiser/'+ $scope.guid +'/campaigns/workflow/'+action; 
		var ids = [];
		angular.forEach($scope.campaignList.data, function(item) {
            if($scope.checkboxes.items[item.id])  
            	ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl,
			controller:campaignWorkflowController,
			size: "medium",
			resolve: {
			        campaigns: function () {
			        	var selections = [];
			        	angular.forEach($scope.campaignList.data, function(item) {
			                if($scope.checkboxes.items[item.id])  
			                	selections.push(item);
			            });
			          return  selections;
			        }
			      }
		});

		modalInstance.result.then(
				function () {
					//.$log.info('Modal ok: ');
				}, 
				function() {
					//$log.info('Modal dismissed at: ' + new Date());
				}
		);
	};
 
	$scope.updateCampaign = function(campaignId,action){
		var templateUrl = '/advertiser/'+ $scope.guid +'/campaigns/workflow/'+action; 
		templateUrl += "?ids="+ campaignId;
		var modalInstance = $modal.open({
			templateUrl: templateUrl,
			controller:campaignWorkflowController,
			size: "medium",
			resolve: {
			        campaigns: function () {
			        	var selections = [];
			        	angular.forEach($scope.campaignList.data, function(item) {
			                if($scope.checkboxes.items[item.id])  
			                	selections.push(item);
			            });
			          return  selections;
			        }
			      }
		});

		modalInstance.result.then(
				function () {
					//.$log.info('Modal ok: ');
				}, 
				function() {
					//$log.info('Modal dismissed at: ' + new Date());
				}
		);
	};
	
	$scope.open = function (size,  guid, id, action) {
		var modalInstance = $modal.open({
			templateUrl: '/advertiser/campaigns/workflow/form?id='+id+'&accountGuid='+guid +'&action='+action, 
			controller:campaignWorkflowController,
			size: size 
		});

		modalInstance.result.then(
				function () {
					//.$log.info('Modal ok: ');
				}, 
				function() {
					//$log.info('Modal dismissed at: ' + new Date());
				}
		);
	}; 
	
	
	 $scope.checkboxes = { 'checked': false, items: {} };
	 
	 
    // watch for check all checkbox
    $scope.$watch('checkboxes.checked', function(value) {
        angular.forEach($scope.campaignList.data, function(item) {
            if (angular.isDefined(item.id)) {
                $scope.checkboxes.items[item.id] = value;
            }
        });
    });
    // watch for data checkboxes
    $scope.$watch('checkboxes.items', function(values) {
        if (!$scope.campaignList.data) {
            return;
        }
        var checked = 0, unchecked = 0,
            total = $scope.campaignList.data.length;
        angular.forEach($scope.campaignList.data, function(item) {
            checked   +=  ($scope.checkboxes.items[item.id]) || 0;
            unchecked += (!$scope.checkboxes.items[item.id]) || 0;
        });
        if ((unchecked == 0) || (checked == 0)) {
            $scope.checkboxes.checked = (checked == total);
        }
        // grayed checkbox
        angular.element(document.getElementById("select_all")).prop("indeterminate", (checked != 0 && unchecked != 0));
    }, true); 
});


var campaignWorkflowController = function ($scope, $modalInstance, campaigns) {

	  $scope.campaigns = campaigns;
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