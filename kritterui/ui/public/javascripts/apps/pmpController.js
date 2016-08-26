angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select"] ).
controller('pmpController',function ($scope, $http) {

        $scope.guid = "";
        $scope.currentPage=0;
        $scope.maxPages = 10;
        $scope.pageSize = 50;
        $scope.totalItems = 500;
        $scope.pmpList = {}

        $scope.init = function(){
                var path = $.url().attr('path');
                var pathComps = path.split("/");
                $scope.guid = pathComps[2];
                $scope.pmpListData =  Object.create(TableModel);
                $scope.pmpListData.init("/entitylist" );
                $scope.pmpListData.setFilterAttribute("entityType", "pmp_deal" );
        }

        $scope.init();
	
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
        //direct publishers and site get
        $scope.pub_url = "/metadata/options/directpublishers";
        $scope.pubIdList_msmodel = Object.create(MultiSelectModel);
        $scope.pubIdList_msmodel.init($scope.pub_url, 'valueArray', true, true);

        $scope.site_url = "/metadata/options/sites/";
        $scope.siteIdList_msmodel = Object.create(MultiSelectModel);
        $scope.siteIdList_msmodel.init($scope.site_url, 'valueArray', true, true);
        $scope.pubIdList_msmodel.addDependent("siteIdList", $scope.siteIdList_msmodel);

        $scope.campaignIdList_msmodel = Object.create(MultiSelectModel);
        $scope.campaignIdList_msmodel.init("/metadata/options/campaigns/none", 'valueArray', true, true);	

        //setting external demand's dsp and adv id multiselect to empty in beginning using special keyword none.
        $scope.dspIdList_msmodel = Object.create(MultiSelectModel);
        $scope.dspIdList_msmodel.init("/metadata/options/tpdspidlist/none", 'valueArray', true, true);
        $scope.advertiserIdList_msmodel = Object.create(MultiSelectModel);
        $scope.advertiserIdList_msmodel.init("/metadata/options/tpadvidlist/none", 'valueArray', true, true);

        //select campaigns using advertiser guid.Only one value but input comes as an array of csv,e.g: adv1,adv2 but here its just adv1
        $scope.thirdPartyConnectionGuid = "none";
        $scope.updateCampaignsAndExternalData= function()
        {
            if($scope.thirdPartyConnectionGuid != "")
            {
                $scope.campaignIdList_msmodel.setUrl("/metadata/options/campaigns/"+$scope.thirdPartyConnectionGuid);
                $scope.dspIdList_msmodel.setUrl("/metadata/options/tpdspidlist/"+$scope.thirdPartyConnectionGuid);
                $scope.advertiserIdList_msmodel.setUrl("/metadata/options/tpadvidlist/"+$scope.thirdPartyConnectionGuid);
            }
        }
        
        //select ads based on what campaigns are selected, campaignid input comes as 1,2,3. array of integers.
        $scope.ads_url = "/metadata/options/ads/";
        $scope.adIdList_msmodel = Object.create(MultiSelectModel);
        $scope.adIdList_msmodel.init($scope.ads_url, 'valueArray', true, true);
        $scope.campaignIdList_msmodel.addDependent("adIdList",$scope.adIdList_msmodel);

        //iab categories 
        $scope.tier1categories_url = "/metadata/options/tier1categories";
        $scope.blockedIABCategories_msmodel = Object.create(MultiSelectModel);
        $scope.blockedIABCategories_msmodel.init($scope.tier1categories_url, 'valueArray', true,true);
});

