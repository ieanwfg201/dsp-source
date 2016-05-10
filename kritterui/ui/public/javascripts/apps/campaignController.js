angular.module('app.ui.form.ctrls', ["ngQuickDate"] ).
controller('campaignController',function ($scope, $http) {
	
	$scope.initDate = function(field, dateval){ 
		$scope[field+"_dateobj"] = new Date(dateval); 
		$scope.$watch(field+"_dateobj", function(oldvalue,newvalue ) { 
			$scope[field] = $scope[field+"_dateobj"].getTime();
		});
	};
	$scope.campaignUnlimitedBudget = function () {
		var sel = $( "#campaignUnlimitedBudgetId option:selected" ).text();
		var val = 100000;
		if(sel == 'No'){
			val = 0;
			$scope.adv_total_budget_obj = val;
			$scope.internal_total_budget_obj = val;
		}
		if(sel == 'Yes'){
			val = 100000;
			$scope.adv_total_budget_obj = val;
			$scope.internal_total_budget_obj = val;
		}
		//$('#internal_total_budget').attr('value',val);
		//$('#adv_total_budget').attr('value',val);
	};
	$scope.campaignUnlimitedEndDate = function () {
		var sel = $( "#campaignUnlimitedEndDateId option:selected" ).text();
		var unlimiteddate = $scope["start_date_dateobj"];
		unlimiteddate = new Date(unlimiteddate.getTime());
		if(sel == 'Yes'){
			unlimiteddate.setDate(unlimiteddate.getDate() + 365) ;
		}
		$scope["end_date_dateobj"] =unlimiteddate;
		
	};
	$scope.initCurrency = function(field, val){
		$scope[field+"_obj"] = parseFloat(val); 
	};
	$scope.$watch("adv_total_budget_obj", function(newValue, oldValue) {
		if(newValue === oldValue){
		    return;
		}
		if($scope.adv_total_budget_obj != 0.0){
		var a = $scope.adv_total_budget_obj;
		$scope.internal_total_budget_obj = a;
	}
	});
	$scope.$watch("adv_daily_budget_obj", function(newValue, oldValue) {
		if(newValue === oldValue){
		    return;
		}
		if($scope.adv_daily_budget_obj != 0.0){
			var a = $scope.adv_daily_budget_obj;
			$scope.internal_daily_budget_obj = a;
		}
	});
	
	
}).
controller('campaignListController',function ($scope, $http, $modal) {
	$scope.status= "Pending";
	$scope.currentPage=0;
	$scope.maxPages = 5;
	$scope.pageSize = 10;
	$scope.totalItems = 30;
	$scope.adv_total_budget = 0.0;
	$scope.init = function(){
		$scope.status= $.url().param("status");
		$scope.pageSize = $.url().param("pageSize");
		$scope.currentPage =  $.url().param("pageNo");
	}
	
	$scope.init();

	$scope.fetch = function (page) {
		var url = "/advertiser/campaigns/list?status="+$scope.status+"&pageNo="+page+"&pageSize="+$scope.pageSize;
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
					//.$log.info('Modal ok: ');
				}, 
				function() {
					//$log.info('Modal dismissed at: ' + new Date());
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