/** Controllers */
angular.module('app.ui.form.ctrls', [ 'multi-select','ngTable' ]).
    controller('editableTableController',function ($scope, $http, ngTableParams) {
         
        
    	$scope.testEditableTable = {};
		 $scope.editId = -1;
	
	     $scope.setEditId =  function(pid) {
	         $scope.editId = pid;
	     }
		
	
		 $scope.fetchInitialOptions = function () {
	         $http.get("/reporting/dashboard/data").success( function(response){
	         	$scope.testEditableTable = response.topPublishers;
	         }); 
	     };
        
    
        $scope.fetchInitialOptions();
    });
