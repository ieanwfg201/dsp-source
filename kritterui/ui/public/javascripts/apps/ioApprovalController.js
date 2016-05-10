angular.module('app.ui.form.ctrls',  [  'ngTable' ] ).
controller('ioApprovalController',function ($scope, $http, $modal) {
	$scope.currentPage=0;
	$scope.maxPages = 5;
	$scope.pageSize = 10;
	$scope.totalItems = 30;
	$scope.ioList = {}
	$scope.statusMap = [];
	$scope.status = "";

	 
	$scope.init = function(){
		var path = $.url().attr('path');
		var pathComps = path.split("/"); 
		if(pathComps.length == 4)
			$scope.status = pathComps[3]; 
	}
	
	$scope.statusLabel = function(code){
		return $scope.statusMap[code];
	}
	
	$scope.filterOptions = function() {
        var def = $q.defer(),
            arr = [],
            names = [];
        for(var key in $scope.statusMap){
        	 names.push({
                 'id': key,
                 'title': $scope.statusMap[key]
             });
         }
 
        def.resolve(names);
        return def;
    }; 
    
	$scope.fetch = function () {
		var url = "/operations/io-approval-queue/data";
		if($scope.status != "")
			url += "?status="+$scope.status;
		$http.get(url).success( function(response){
			$scope.statusMap = response.statusMap;
     		 $scope.ioList.data = response.list;
     		 $scope.ioList.count = response.count;
     		 
       });
	};
 
	$scope.init();
	$scope.fetch();
	
	$scope.updateSelectedIO = function(action){
		var templateUrl = '/advertiser/'+ $scope.guid +'/campaigns/workflow/'+action; 
		var ids = [];
		angular.forEach($scope.ioList.data, function(item) {
            if($scope.checkboxes.items[item.id])  
            	ids.push(item.id);
        });
		
		templateUrl += "?ids="+ ids.join("_");
		var modalInstance = $modal.open({
			templateUrl: templateUrl,
			controller:siteWorkflowController,
			size: "medium",
			resolve: {
			        campaigns: function () {
			        	var selections = [];
			        	angular.forEach($scope.ioList.data, function(item) {
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
 
	
	$scope.updateIO = function (guid, ioId, action) {
		var modalInstance = $modal.open({
			templateUrl: '/insertion-order/workflow/editFrom/'+guid+'/'+ encodeURIComponent(ioId)+'/'+action, 
			controller:ioEditerController,
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
	
	
	 $scope.checkboxes = { 'checked': false, items: {} };
	 
	 
    // watch for check all checkbox
    $scope.$watch('checkboxes.checked', function(value) {
        angular.forEach($scope.ioList.data, function(item) {
            if (angular.isDefined(item.id)) {
                $scope.checkboxes.items[item.id] = value;
            }
        });
    });
    // watch for data checkboxes
    $scope.$watch('checkboxes.items', function(values) {
        if (!$scope.ioList.data) {
            return;
        }
        var checked = 0, unchecked = 0,
            total = $scope.ioList.data.length;
        angular.forEach($scope.ioList.data, function(item) {
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

var ioApprovalFlowController = function ($scope, $modalInstance, campaigns) {

	  $scope.campaigns = campaigns;
	  $scope.ok = function () {
		  var action_url = $('#ioApprovalForm').attr('action'); 
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