/** Controllers */
angular.module('app.ui.form.ctrls', [ 'multi-select' ,'ngTable']).
controller('dashboardController',function ($scope, $http, $filter, ngTableParams) {
    
	$scope.topAdvertiserByRevenueYes = {};
	$scope.topCampaignByRevenueYes = {};
	$scope.topPublisherByIncomeYes = {};
	$scope.topSiteByIncomeYes = {};
	$scope.topExchangeByIncomeYes = {};
	$scope.topGainerAdvertiserByRevenueYesDayBefore = {};
	$scope.topGainerPublisherByPubIncomeYesDayBefore = {};
	$scope.topGainerCampaignByRevenueYesDayBefore = {};
	$scope.topGainerSiteByIncomeYesDayBefore = {};
	$scope.topLooserAdvertiserByRevenueYesDayBefore = {};
	$scope.topLooserPublisherByIncomeYesDayBefore = {};
	$scope.topLooserCampaignByRevenueYesDayBefore = {};
	$scope.topLooserSiteByIncomeYesDayBefore = {};
	$scope.topCountryByRequestYes = {};
	$scope.topOsByRequestYes = {};
	$scope.topManufacturerByRequestYes = {};
	$scope.topBrowserByRequestYes = {};
	$scope.RequestImpressionRender = {};
	$scope.RequestImpressionRender.options = {type:"bar", xKey:"time", 
    		yKeys:["total_request_name","total_impressions_name","total_csc_name"],
    		labels:["Total Requests", "Total Impressions", "Render"]};
	$scope.ImpressionWin = {};
	$scope.ImpressionWin.options = {type:"bar", xKey:"time", 
    		yKeys:["total_impressions_name", "total_win_name"],
    		labels:["Impression", "Win"]};
	$scope.RevenuePubIncome = {};
	$scope.RevenuePubIncome.options = {type:"bar", xKey:"time", 
    		yKeys:["demandCharges_name","supplyCost_name"],
    		labels:["Revenue", "PubIncome"]};
	$scope.ClickConversion = {};
	$scope.ClickConversion.options = {type:"bar", xKey:"time", 
    		yKeys:["total_click_name","conversion_name"],
    		labels:["Click", "Conversion"]};
    $scope.dataLoaded = false;
    
    
    $scope.refershData = function(){
    	$('#preloader').removeClass("hidden");
        var action_url = $('#dashboardForm').attr('action'); 
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        $http.post(action_url, $('#dashboardForm').serialize()).success( function(response){
            $scope.topAdvertiserByRevenueYes = response.topAdvertiserByRevenueYes;
        	$scope.topCampaignByRevenueYes = response.topCampaignByRevenueYes;
        	$scope.topPublisherByIncomeYes = response.topPublisherByIncomeYes;
        	$scope.topSiteByIncomeYes = response.topSiteByIncomeYes;
        	$scope.topExchangeByIncomeYes = response.topExchangeByIncomeYes;
        	$scope.topGainerAdvertiserByRevenueYesDayBefore = response.topGainerAdvertiserByRevenueYesDayBefore;
        	$scope.topGainerPublisherByPubIncomeYesDayBefore = response.topGainerPublisherByPubIncomeYesDayBefore;
        	$scope.topGainerCampaignByRevenueYesDayBefore = response.topGainerCampaignByRevenueYesDayBefore;
        	$scope.topGainerSiteByIncomeYesDayBefore = response.topGainerSiteByIncomeYesDayBefore;
        	$scope.topLooserAdvertiserByRevenueYesDayBefore = response.topLooserAdvertiserByRevenueYesDayBefore;
        	$scope.topLooserPublisherByIncomeYesDayBefore = response.topLooserPublisherByIncomeYesDayBefore;
        	$scope.topLooserCampaignByRevenueYesDayBefore = response.topLooserCampaignByRevenueYesDayBefore;
        	$scope.topLooserSiteByIncomeYesDayBefore = response.topLooserSiteByIncomeYesDayBefore;
        	$scope.topCountryByRequestYes = response.topCountryByRequestYes;
        	$scope.topOsByRequestYes = response.topOsByRequestYes;
        	$scope.topManufacturerByRequestYes = response.topManufacturerByRequestYes;
        	$scope.topBrowserByRequestYes = response.topBrowserByRequestYes;
        	$scope.RequestImpressionRender = response.RequestImpressionRender.data;
        	$scope.ImpressionWin = response.ImpressionWin.data;
        	$scope.RevenuePubIncome = response.RevenuePubIncome.data;
        	$scope.ClickConversion = response.ClickConversion.data;
            $scope.dataLoaded = true;
            $('#preloader').addClass("hidden");
         });
    }
    $scope.refershData();
     
});
