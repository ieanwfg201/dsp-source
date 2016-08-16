angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select"] ).
controller('pmpController',function ($scope, $http) {
	
	$scope.initDate = function(field, dateval){
		if(dateval == 0){
			$scope[field+"_dateobj"] = new Date();
		}else{ 
			$scope[field+"_dateobj"] = new Date(dateval); 
		}
		$scope.$watch(field+"_dateobj", function(oldvalue,newvalue ) { 
			$scope[field] = $scope[field+"_dateobj"].getTime();
		});
	};
        $scope.pub_url = "/metadata/options/directpublishers";
        $scope.pubIdList_msmodel = Object.create(MultiSelectModel);
        $scope.pubIdList_msmodel.init($scope.pub_url, 'valueArray', true);

        $scope.site_url = "/metadata/options/targeting_direct_sites/";
        $scope.siteIdList_msmodel = Object.create(MultiSelectModel);
        $scope.siteIdList_msmodel.init($scope.site_url, 'valueArray', false, true);
        $scope.pubIdList_msmodel.addDependent("siteIdList", $scope.siteIdList_msmodel);
	
	
});

