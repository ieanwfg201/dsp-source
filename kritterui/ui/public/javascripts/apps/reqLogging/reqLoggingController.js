angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('reqLoggingController',function ($scope, $http, ngTableParams, $modal) {
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
      	var action_url = $('#reqLoggingConfigForm').attr('action'); 
   		var data = $('#reqLoggingConfigForm').serialize();
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

    $scope.pubId_url = "/metadata/options/guidexchanges";
    
	// advt, campaign, ad
	$scope.pubId_msmodel = Object.create(MultiSelectModel);
	$scope.pubId_msmodel.init( $scope.pubId_url, 'valueArray', true, true);
	
	$scope.initMS = function(field, value){
		$scope[field+"_msmodel"].setInitValue(value); 
	}

});

