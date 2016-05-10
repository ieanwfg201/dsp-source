angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('savedReportController',function ($scope, $http, ngTableParams) {
	$scope.chartData= {};
	
	$scope.reset = false;
	
	$scope.data_url = "/reporting/saved-reports/data";
		
	var tbparams = new ngTableParams({
        page: 1,            // show first page
        count: 100,           // count per page  
        total:100
     }, {  
        getData: function($defer, params) {   
        	
        	if($scope.reset){
          		$scope.savedReportList.data = [];
       		    $scope.savedReportList.column = []; 
       		    params.page(1);  
          		$scope.reset = false;
          	}
        	
//        	var queryParams = params.url();    
//			$("#pagesize").val(queryParams.count); 
//			$("#startindex").val(queryParams.page); 
			$('#preloader').removeClass("hidden");  
			var action_url = $scope.data_url ; 
     		$http.get(action_url).success( function(response){ 
     			 $scope.savedReportList.data = response.data; 
     			 params.total(response.count);  
     			 $defer.resolve(response.data);  
     			 $('#preloader').addClass("hidden"); 
     	    }); 
        }
     }); 
	
	$scope.savedReportList= {"params":tbparams, "data":[], "column":[]};
	
	
		
	 

});
