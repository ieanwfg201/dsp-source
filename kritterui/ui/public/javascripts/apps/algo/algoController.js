angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('algoController',function ($scope, $http, ngTableParams, $modal) {
	$scope.initComplete = false;
	
	$scope.init = function(){
		$scope.initComplete = true;
	}
	
	$scope.refershData = function(){
		 $scope.reset = true;
		 $scope.tableData.params.reload(); 
	}
	var tbparams = new ngTableParams({
      page: 1,            // show first page
      count: 100,           // count per page  
      total:100
   }, {  
      getData: function($defer, params) {   
      	
    	if(!$scope.initComplete)
   		   return;
    	  
      	if($scope.reset){
      		$scope.tableData.data = [];
   		    $scope.tableData.column = []; 
   		    params.page(1);  
      		$scope.reset = false;
      	}
      	
      	var queryParams = params.url();    
		$('#preloader').removeClass("hidden"); 
      	var action_url = $('#algoConfigForm').attr('action'); 
   		var data = $('#algoConfigForm').serialize();
   		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
   		$http.post(action_url, data).success( function(response){ 
   			 $scope.tableData.data = response.data;
   			 $scope.tableData.column = response.column; 
   			 params.total(response.count);  
   			 $defer.resolve(response.data);
   			$('#preloader').addClass("hidden"); 
   	    }); 
      }
   }); 
	
	$scope.tableData= {"params":tbparams, "data":[], "column":[]};

    $scope.advt_url = "/metadata/options/advids";
    $scope.campaigns_by_advt_url = "/metadata/options/campaignsbyadvid/";
    $scope.ads_by_campaigns_url = "/metadata/options/ads/"; 
    
	// advt, campaign, ad
	$scope.advertisers_msmodel = Object.create(MultiSelectModel);
	$scope.advertisers_msmodel.init( $scope.advt_url, 'valueArray', true, true);
	
	
	$scope.campaigns_msmodel = Object.create(MultiSelectModel);
	$scope.campaigns_msmodel.init( $scope.campaigns_by_advt_url, 'valueArray', true, true);
	$scope.advertisers_msmodel.addDependent("campaigns", $scope.campaigns_msmodel);
	
	$scope.ads_msmodel = Object.create(MultiSelectModel);
	$scope.ads_msmodel.init($scope.ads_by_campaigns_url, 'valueArray', true, true);
	$scope.campaigns_msmodel.addDependent("ads", $scope.ads_msmodel);
	
	$scope.initMS = function(field, value){
		$scope[field+"_msmodel"].setInitValue(value); 
	}

});

