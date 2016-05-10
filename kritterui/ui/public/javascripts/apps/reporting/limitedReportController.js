angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('limitedReportController',function ($scope, $http, ngTableParams, $modal) {
	$scope.reportType= "";
	$scope.savedReportId = 0;
	$scope.disableSave = false;
	$scope.initComplete = false;
	$scope.getCSV = false;
	
	$scope.init = function(type, id){
		$scope.reportType= type;
		$scope.savedReportId = id;
		$scope.initComplete = true;
		if( id != 0 ){
			$scope.refershData();
		}else{
			$scope.checkuncheckmetrics(true);
		}
	}
	$scope.togglechecks = function(){
		var checkval = false;
		if(typeof ($('#selectallmetric:checked').val()) !== 'undefined'){
			checkval = true;
		}
		$scope.checkuncheckmetrics(checkval);
	}
	$scope.checkuncheckmetrics = function(checkvalue){
		$('#total_request').prop("checked",checkvalue);
		$('#total_impression').prop("checked",checkvalue);
		$('#total_csc').prop("checked",checkvalue);
		$('#billedcsc').prop("checked",checkvalue);
		$('#total_click').prop("checked",checkvalue);
		$('#billedclicks').prop("checked",checkvalue);
		$('#total_win').prop("checked",checkvalue);
		$('#conversion').prop("checked",checkvalue);
		$('#supplyCost').prop("checked",checkvalue);
		$('#exchangepayout').prop("checked",checkvalue);
		$('#networkpayout').prop("checked",checkvalue);
		$('#demandCharges').prop("checked",checkvalue);
		$('#exchangerevenue').prop("checked",checkvalue);
		$('#networkrevenue').prop("checked",checkvalue);
		$('#cpa_goal').prop("checked",checkvalue);
		$('#total_bidValue').prop("checked",checkvalue);
		$('#total_win_bidValue').prop("checked",checkvalue);
		$('#fr').prop("checked",checkvalue);
		$('#eIPM').prop("checked",checkvalue);
		$('#billedEIPM').prop("checked",checkvalue);
		$('#eIPC').prop("checked",checkvalue);
		$('#ctr').prop("checked",checkvalue);
		$('#rtr').prop("checked",checkvalue);
		$('#wtr').prop("checked",checkvalue);
		$('#eIPW').prop("checked",checkvalue);
		$('#eCPW').prop("checked",checkvalue);
		$('#profitmargin').prop("checked",checkvalue);
		$('#eIPA').prop("checked",checkvalue);
		$('#eCPA').prop("checked",checkvalue);
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
	
	$scope.saveConfiguration = function(){
		var modalInstance = $modal.open({
			templateUrl: '/reporting/save-report-form/'+$scope.reportType+"/"+$scope.savedReportId,
			controller:savedReportsController,
			size: "medium" 
		});
		
		modalInstance.result.then(
			function () { 
			}, 
			function() { 
		    }
		);
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
      	var action_url = $('#limitedReportConfigForm').attr('action'); 
      	if($scope.getCSV){
       		action_url = action_url + "/CSV";
       	}
   		var data = $('#limitedReportConfigForm').serialize();
   		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
   		$http.post(action_url, data).success( function(response){ 
   			 $scope.tableData.data = response.data;
   			 $scope.tableData.column = response.column; 
   			 params.total(response.count);  
   			 $defer.resolve(response.data);
   			$('#preloader').addClass("hidden"); 
			 if($scope.getCSV==true){
 				 //window.open(response.downloadurl,'Download','_blank');
				 window.location.href = response.downloadurl;
 			 }
   	    }); 
      }
   }); 
	
	$scope.tableData= {"params":tbparams, "data":[], "column":[]};
	
	
	$scope.advid_url = "/metadata/options/advids";
	$scope.pub_url = "/metadata/options/publishers";
	$scope.sites_by_pub_url = "/metadata/options/sites/";
	$scope.campaigns_by_advid_url = "/metadata/options/campaignsbyadvid/";
	$scope.ads_by_campaigns_url = " /metadata/options/ads/"; 
	
	$scope.country_url = "/metadata/options/countries";
	$scope.carrier_url = "/metadata/options/carriers/";
	$scope.brand_url = "/metadata/options/brands/";
	$scope.os_url = "/metadata/options/operating-systems";
	
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
	$scope.changeDate = function () {
		var sel = $( "#frequency option:selected" ).text();
		if(sel == 'YESTERDAY'){
			var startdate = new Date();
			startdate.setDate(startdate.getDate() - 1) ;
			startdate.setHours(0,0,0,0);
			$scope["start_date_dateobj"] = startdate;
			var enddate = new Date();
			enddate.setDate(enddate.getDate() - 1) ;
			enddate.setHours(23,59,59,0)
			$scope["end_date_dateobj"] = enddate;
		}else if(sel == 'TODAY'){
			var startdate = new Date();
			startdate.setHours(0,0,0,0)
			$scope["start_date_dateobj"] = startdate;
			var enddate = new Date();
			enddate.setHours(23,59,59,0)
			$scope["end_date_dateobj"] = enddate;
		}else if(sel == 'LAST7DAYS'){
			var startdate = new Date();
			startdate.setDate(startdate.getDate() - 8) ;
			startdate.setHours(0,0,0,0)
			$scope["start_date_dateobj"] = startdate;
			var enddate = new Date();
			enddate.setDate(enddate.getDate() - 1) ;
			enddate.setHours(23,59,59,0)
			$scope["end_date_dateobj"] = enddate;
		}else if(sel == 'CURRENTMONTH'){
			var startdate = new Date();
			startdate.setDate(1);
			startdate.setHours(0,0,0,0)
			$scope["start_date_dateobj"] = startdate;
			var enddate = new Date();
			enddate.setHours(23,59,59,0);
			$scope["end_date_dateobj"] = enddate;
		}else if(sel == 'LASTMONTH'){
            var startdate = new Date();
            startdate.setDate(-1);
            startdate.setDate(1);
            startdate.setHours(0,0,0,0)
            $scope["start_date_dateobj"] = startdate;
            var enddate = new Date();
            enddate.setDate(0);
            enddate.setHours(23,59,59,0);
            $scope["end_date_dateobj"] = enddate;
        }

		
		if(sel == 'DATERANGE'){
			$scope.disableSave= true;
			$scope["end_date_disabled"] = true;
			$scope["start_date_disabled"] = true;
		}else{
			$scope.disableSave= false;
			$scope["end_date_disabled"] = false;
			$scope["start_date_disabled"] = false;
		}
	};

	// advt, campaign, ad
	$scope.advid_msmodel = Object.create(MultiSelectModel);
	$scope.advid_msmodel.init( $scope.advid_url, 'valueArray', true, true);
	
	
	$scope.campaigns_msmodel = Object.create(MultiSelectModel);
	$scope.campaigns_msmodel.init( $scope.campaigns_by_advid_url, 'valueArray', true, true);
	$scope.advid_msmodel.addDependent("campaigns", $scope.campaigns_msmodel);
	
	$scope.ads_msmodel = Object.create(MultiSelectModel);
	$scope.ads_msmodel.init($scope.ads_by_campaigns_url, 'valueArray', true, true);
	$scope.campaigns_msmodel.addDependent("ads", $scope.ads_msmodel);
	
	// publishers and sites
	$scope.publishers_msmodel = Object.create(MultiSelectModel);
	$scope.publishers_msmodel.init($scope.pub_url, 'valueArray', true, true);
	
	$scope.sites_msmodel = Object.create(MultiSelectModel);
	$scope.sites_msmodel.init($scope.sites_by_pub_url, 'valueArray', true, true);
	$scope.publishers_msmodel.addDependent("sites", $scope.sites_msmodel);

	//Country Carrier
	$scope.country_msmodel = Object.create(MultiSelectModel);
	$scope.country_msmodel.init($scope.country_url, 'valueArray', true, true);
	
	$scope.carrier_msmodel = Object.create(MultiSelectModel);
	$scope.carrier_msmodel.init( $scope.carrier_url, 'valueArray', true, true);
	$scope.country_msmodel.addDependent("carriers", $scope.carrier_msmodel);

	//os/brand/model configs
	$scope.os_msmodel = Object.create(MultiSelectModel);
	$scope.os_msmodel.init($scope.os_url, 'valueArray', true, true);
	
	$scope.brand_msmodel = Object.create(MultiSelectModel);
	$scope.brand_msmodel.init($scope.brand_url, 'valueArray', true, true);
	$scope.os_msmodel.addDependent("brands", $scope.brand_msmodel);
	
	$scope.initMS = function(field, value){
		$scope[field+"_msmodel"].setInitValue(value); 
	}

});

var savedReportsController = function ($scope, $modalInstance) {
	 
	  $scope.ok = function () {
		  var action_url = $('#savedReportForm').attr('action'); 
		  var reportConfig = $('#limitedReportConfigForm').serializeObject();
		  reportConfig = JSON.stringify(reportConfig) ;
		  $("#query").val(reportConfig); 
		  $.post(action_url, $('#savedReportForm').serialize(), function(response){
			  $modalInstance.dismiss('cancel'); 
			  window.location = response.redirect_url;
		  }); 
		  
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	    return false;
	  };
};
