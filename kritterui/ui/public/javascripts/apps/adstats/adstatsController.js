angular.module('app.ui.form.ctrls', ["ngQuickDate", "multi-select",  'ngTable'] ).
controller('adstatsController',function ($scope, $http, ngTableParams, $modal) {
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
      	var action_url = $('#adstatsConfigForm').attr('action'); 
      	if($scope.getCSV){
       		action_url = action_url + "/CSV";
       	}
   		var data = $('#adstatsConfigForm').serialize();
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
	
	
	$scope.advt_url = "/metadata/options/advids";
	$scope.campaigns_by_advt_url = "/metadata/options/campaignsbyadvid/";
	$scope.ads_by_campaigns_url = " /metadata/options/ads/";
	

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
			$scope["end_date_disabled"] = true;
			$scope["start_date_disabled"] = true;
		}else{
			$scope["end_date_disabled"] = false;
			$scope["start_date_disabled"] = false;
		}
	};

	// advt, campaign, ad
	$scope.advertisers_msmodel = Object.create(MultiSelectModel);
	$scope.advertisers_msmodel.init( $scope.advt_url, 'valueArray', true, true);
	
	
	$scope.campaigns_msmodel = Object.create(MultiSelectModel);
	$scope.campaigns_msmodel.init( $scope.campaigns_by_advt_url, 'valueArray', true, true);
	$scope.advertisers_msmodel.addDependent("campaigns", $scope.campaigns_msmodel);
	
	$scope.ads_msmodel = Object.create(MultiSelectModel);
	$scope.ads_msmodel.init($scope.ads_by_campaigns_url, 'valueArray', true, true);
	$scope.campaigns_msmodel.addDependent("ads", $scope.ads_msmodel);
	
	$scope.initMS = function(field, value){
		$scope[field+"_msmodel"].setInitValue(value); 
	}

});

