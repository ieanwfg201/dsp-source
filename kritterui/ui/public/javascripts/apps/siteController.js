/** Controllers */
angular.module('app.ui.form.ctrls', [ 'multi-select' ]).
controller('siteController',function ($scope, $http) {
	$scope.checkvalid = function(obj){
		if(obj.value != "all" && obj.value != "none" )
			return true;
		return false;
	}
	$scope.advertiser_url = "/metadata/options/directadvertiserforfiltering";
	$scope.campaign_url = "/metadata/options/campaignforfiltering/";
	
	$scope.tier1categories_url = "/metadata/options/tier1categories";
	$scope.tier2categories_url = "/metadata/options/tier2categories";
	
	$scope.hygiene_list_url = "/metadata/options/hygienelist";
	$scope.creative_attr_url= "/metadata/options/creativeattr";
	
	$scope.id=$("#id").val();
	$scope.guid=$("#guid").val(); 
	$scope.pub_id = $("#pub_id").val();;
	$scope.pub_guid = $("#pub_guid").val(); 
	$scope.site_platform_id = $("#site_platform_id").val();
	
	$scope.optInHygiene= $("#opt_in_hygiene_list").val();
	$scope.creativeAttrs= $("#creative_attr_inc_exc").val();
	$scope.url_exclusion=$("#url_exclusion").val();
	$scope.url_exclusion_input= "";
	
	$scope.showWapForm = false;
	$scope.showAppForm = false;

	$scope.filteredUrls = "";
 

	$scope.advertiser_json_array_msmodel = Object.create(MultiSelectModel);  
	$scope.advertiser_json_array_msmodel.init($scope.advertiser_url, 'valueArray', true);
	
	$scope.campaign_json_array_msmodel = Object.create(MultiSelectModel);  
	$scope.campaign_json_array_msmodel.init($scope.campaign_url, 'valueArray', false, true);
	$scope.advertiser_json_array_msmodel.addDependent("campaigns", $scope.campaign_json_array_msmodel);

	
	
	$scope.categories_tier_1_list_msmodel = Object.create(MultiSelectModel);
	$scope.categories_tier_1_list_msmodel.init($scope.tier1categories_url, 'valueArray', true);
	$scope.categories_tier_2_list_msmodel = Object.create(MultiSelectModel);
	$scope.categories_tier_2_list_msmodel.init($scope.tier2categories_url, 'valueArray', true);
	
	$scope.category_list_inc_exc_tier_1_msmodel = Object.create(MultiSelectModel);
	$scope.category_list_inc_exc_tier_1_msmodel.init($scope.tier1categories_url, 'valueArray', true);
	$scope.category_list_inc_exc_tier_2_msmodel = Object.create(MultiSelectModel);
	$scope.category_list_inc_exc_tier_2_msmodel.init($scope.tier2categories_url, 'valueArray', true);
	
	$scope.opt_in_hygiene_list_msmodel = Object.create(MultiSelectModel);
	$scope.opt_in_hygiene_list_msmodel.init($scope.hygiene_list_url, 'valueArray', true);
	$scope.creative_attr_inc_exc_msmodel = Object.create(MultiSelectModel);
	$scope.creative_attr_inc_exc_msmodel.init($scope.creative_attr_url, 'valueArray', true);
	
	$scope.initComplete = false;

	$scope.init= function(){ 
		var tmp = $scope.url_exclusion.split(",");
		$scope.url_exclusion_input = tmp.join("\n"); 
		$scope.updateSitePlatform();
	}

	
	$scope.initSitePlatform = function(id){
		$scope.site_platform_id = id;
		$scope.updateSitePlatform();
	}
	
	$scope.updateSitePlatform = function(){		
		switch($scope.site_platform_id) {
			case "WAP":
				$scope.showWapForm = true;
				$scope.showAppForm = false; 
				break;
			
			case "APP":
				$scope.showWapForm = false;
				$scope.showAppForm = true;  
				break;
				 
			default: 
				scope.showWapForm = false;
				$scope.showAppForm = false;  
				break;
		} 
	}

	$scope.initSitePlatform($scope.site_platform_id);
	$scope.init();
	



	$scope.$watch('url_exclusion_input', function() {
		if($scope.initComplete){
			var tmp = $scope.url_exclusion_input.split("\n");
			$scope.url_exclusion=tmp.join();
		}
	});
});
