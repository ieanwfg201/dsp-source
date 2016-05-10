/** Controllers */
angular.module('app.ui.form.ctrls', [ ]).
controller('publisherReportController',function ($scope, $http) {
	$scope.publisherDataTable = {}; 

	$scope.drillDown = function(field, value){
		var url=  "?"+ field+"="+value;
		window.location = url;
	}
	
	$scope.refershData = function () {
		$http.get("/reports/static/publishers/data").success( function(response){
			$scope.publisherDataTable = response; 
		}); 
	};

	$scope.refershData();

});