angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('payoutController',function ($scope, $http, ngTableParams, $modal) {
	$scope.initComplete = false;
	
	$scope.init = function(){
		$scope.initComplete = true;
	}
	
	$scope.refershData = function(){
		 $scope.reset = true;
         $scope.tableData.params.reload();
	}
	
    $scope.edit = function () {
        var action_url = '/payout/defaultpayout/update'; 
        var data = $('#payoutConfigForm').serialize();
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http.post(action_url, data).success( function(response){ 
        }); 
         location.reload();    
    };
	
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
			$('#preloader').removeClass("hidden"); 
			
	       	var action_url = $('#payoutConfigForm').attr('action'); 
	       	var data = $('#payoutConfigForm').serialize();
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
});


