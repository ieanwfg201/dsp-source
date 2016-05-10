angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('logPagesController',function ($scope, $http, ngTableParams, $modal) {
	$scope.initComplete = false;
	$scope.getCSV = false;
	
	$scope.init = function(){
		$scope.initComplete = true;
	}
	
	$scope.refershData = function(){
		 $scope.reset = true;
		 $scope.getCSV = false;
		 $scope.tableData.params.reload(); 
	}
	
	$scope.refershCSVData = function(){
		 $scope.reset = true;
		 $scope.getCSV = true;
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
				$("#pagesize").val(queryParams.count); 
				$("#startindex").val(queryParams.page);
			
			$('#preloader').removeClass("hidden"); 
			
	       	var action_url = $('#logPagesConfigForm').attr('action'); 
	       	if($scope.getCSV){
	       		action_url = action_url + "/CSV";
	       	}
	       	var data = $('#logPagesConfigForm').serialize();
	    		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
	    		$http.post(action_url, data).success( function(response){ 
	    			 $scope.tableData.data = response.data;
	    			 $scope.tableData.column = response.column; 
	    			 params.total(response.count);  
	    			 $defer.resolve(response.data);
	    			 $('#preloader').addClass("hidden"); 
	    			 if($scope.getCSV==true){
	    				 window.location.href = response.downloadurl;
	    			 }
	    	    }); 
       }
    }); 
	
	$scope.tableData= {"params":tbparams, "data":[], "column":[]};
	
	$scope.advt_url = "/metadata/options/advertisers";
	$scope.pub_url = "/metadata/options/publishers";
	$scope.sites_by_pub_url = "/metadata/options/sites/";
	$scope.extsites_by_pub_url = "/metadata/options/ext_site/";
	$scope.campaigns_by_advt_url = "/metadata/options/campaigns/";
	$scope.ads_by_campaigns_url = " /metadata/options/ads/"; 
	
	$scope.country_url = "/metadata/options/countries";
	$scope.carrier_url = "/metadata/options/carriers/";
	$scope.brand_url = "/metadata/options/brands/";
	$scope.model_url = "/metadata/options/devices/";
	$scope.os_url = "/metadata/options/operating-systems";
	$scope.browser_url = "/metadata/options/browsers";
	
	// advt, campaign, ad
	$scope.advertisers_msmodel = Object.create(MultiSelectModel);
	$scope.advertisers_msmodel.init( $scope.advt_url, 'valueArray', false, true);
	
	
	$scope.campaigns_msmodel = Object.create(MultiSelectModel);
	$scope.campaigns_msmodel.init( $scope.campaigns_by_advt_url, 'valueArray', true, true);
	$scope.advertisers_msmodel.addDependent("campaigns", $scope.campaigns_msmodel);
	
	$scope.ads_msmodel = Object.create(MultiSelectModel);
	$scope.ads_msmodel.init($scope.ads_by_campaigns_url, 'valueArray', true, true);
	$scope.campaigns_msmodel.addDependent("ads", $scope.ads_msmodel);
	
	// publishers and sites
	$scope.publishers_msmodel = Object.create(MultiSelectModel);
	$scope.publishers_msmodel.init($scope.pub_url, 'valueArray', true, true);
	
	$scope.sites_msmodel = Object.create(MultiSelectModel);
	$scope.sites_msmodel.init($scope.sites_by_pub_url, 'valueArray', true, true);
	$scope.publishers_msmodel.addDependent("sites", $scope.sites_msmodel);

	$scope.extsites_msmodel = Object.create(MultiSelectModel);
	$scope.extsites_msmodel.init($scope.extsites_by_pub_url, 'valueArray', true, true);
	$scope.publishers_msmodel.addDependent("extsites", $scope.extsites_msmodel);

	//Country Carrier
	$scope.country_msmodel = Object.create(MultiSelectModel);
	$scope.country_msmodel.init($scope.country_url, 'valueArray', true, true);
	
	$scope.carrier_msmodel = Object.create(MultiSelectModel);
	$scope.carrier_msmodel.init( $scope.carrier_url, 'valueArray', true, true);
	$scope.country_msmodel.addDependent("carriers", $scope.carrier_msmodel);
	
	//os/brand/model configs
	$scope.os_msmodel = Object.create(MultiSelectModel);
	$scope.os_msmodel.init($scope.os_url, 'valueArray', true, true);
	
	$scope.browser_msmodel = Object.create(MultiSelectModel);
	$scope.browser_msmodel.init($scope.browser_url, 'valueArray', true, true);
	
	$scope.brand_msmodel = Object.create(MultiSelectModel);
	$scope.brand_msmodel.init($scope.brand_url, 'valueArray', true, true);
	$scope.os_msmodel.addDependent("brands", $scope.brand_msmodel);
	
	$scope.model_msmodel = Object.create(MultiSelectModel);
	$scope.model_msmodel.async = true;
	$scope.model_msmodel.init($scope.model_url, 'valueArray', true, true);
	$scope.brand_msmodel.addDependent("models", $scope.model_msmodel);

	
	function pad0(a) {
		if( a < 10){
			return '0'+a;
		}
	    return a;
	}
	function returndatefromstr(a){
		var b = a.split(" ");
		var c = b[0].split("-");
		var d = b[1].split(":");
		return (new Date(c[0],(c[1]-1),c[2],d[0],d[1],0));
	}
	$scope.initDate = function(field, dateval){
		var tmp_date = returndatefromstr(dateval);
		if(field == 'start_date'){
			tmp_date.setHours(0,0,0,0);
		}
		if(field == 'end_date'){
			tmp_date.setHours(23,59,59,0);
		} 
		$scope[field+"_dateobj"] = tmp_date;
		$scope.$watch(field+"_dateobj", function(newvalue,oldvalue ) {
			var a = $scope[field+"_dateobj"];
			$scope[field] = a.getFullYear()+'-'+pad0((a.getMonth()+1))+'-'+pad0(a.getDate()) +' ' +pad0(a.getHours())+':'+pad0(a.getMinutes())+':00';
		});
	};	

	
});

