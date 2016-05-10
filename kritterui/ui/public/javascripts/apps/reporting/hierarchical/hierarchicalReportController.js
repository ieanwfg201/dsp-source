angular.module('app.ui.form.ctrls', ["ngQuickDate",  "trNgGrid"] ).
controller('hierarchicalReportController',function ($scope, $http) {
	
	$scope.startDate= new Date();
	$scope.endDate= new Date();
	
	$scope.reportType= "";
	$scope.initComplete = false;
	$scope.getCSV = false;
	
	$scope.showtabs = false;
	$scope.initComplete = false;
	
	$scope.reportFilters = {};
	$scope.reportFilters.filterSeq =[];
	$scope.reportFilters.activeFilterField ="";
	$scope.supplysourcetype = "NONE";
	$scope.site_hygiene = "NONE";
	
	$scope.tableData = { 
			"data":[],  
			"selections":[],
			"currentPage": 0, 
			"itemsPerPage":500,
			"totalItems":1000,
			"showFilters":false,
			"enableSorting": false,
			"globalFilter": false,
			"orderBy":"",
			"orderByReverse":false 
	};
	
	$scope.dimensions = [];
	$scope.breadcrumbs2 = [];
	
	 
	$scope.init = function(){ 
		$scope.endDate  = new Date();
		$scope.endDate.setDate($scope.endDate.getDate() -1) ;
		$scope.endDate.setHours(23,59,59,0);
		
		$scope.startDate  = new Date();
		$scope.startDate.setDate($scope.startDate.getDate() -1) ;
		$scope.startDate.setHours(0,0,0,0);
		
		$scope.path = $.url().attr('path');
		$scope.query = $.url().attr('query');
		$scope.currentUrl = $scope.path +"?"+ $scope.query;
		$scope.currentFilter = "";
		if($scope.query != ""){
			$scope.currentFilter = $.url().param('filter');
		}else{
			
		}
		
		$scope.filterParts = $scope.currentFilter.split("/");
		$scope.filterDepth = Math.max(0, ($scope.filterParts.length-1)/2); 
		$scope.frequency = 'YESTERDAY';
	}
	
	$scope.init();

	$scope.changeDate = function () {
		var sel = $( "#frequency option:selected" ).text();
		if(sel == 'YESTERDAY'){
			var startdate = new Date();
			startdate.setDate(startdate.getDate() - 1) ;
			startdate.setHours(0,0,0,0);
			$scope.startDate = startdate;
			var enddate = new Date();
			enddate.setDate(enddate.getDate() - 1) ;
			enddate.setHours(23,59,59,0)
			$scope.endDate = enddate;
		}else if(sel == 'TODAY'){
			var startdate = new Date();
			startdate.setHours(0,0,0,0)
			$scope.startDate = startdate;
			var enddate = new Date();
			enddate.setHours(23,59,59,0)
			$scope.endDate = enddate;
		}else if(sel == 'LAST7DAYS'){
			var startdate = new Date();
			startdate.setDate(startdate.getDate() - 8) ;
			startdate.setHours(0,0,0,0)
			$scope.startDate = startdate;
			var enddate = new Date();
			enddate.setDate(enddate.getDate() - 1) ;
			enddate.setHours(23,59,59,0)
			$scope.endDate = enddate;
		}else if(sel == 'CURRENTMONTH'){
			var startdate = new Date();
			startdate.setDate(1);
			startdate.setHours(0,0,0,0)
			$scope.startDate = startdate;
			var enddate = new Date();
			enddate.setHours(23,59,59,0);
			$scope.endDate = enddate;
		}else if(sel == 'LASTMONTH'){
            var startdate = new Date();
            startdate.setDate(-1);
            startdate.setDate(1);
            startdate.setHours(0,0,0,0)
            $scope.startDate = startdate;
            var enddate = new Date();
            enddate.setDate(0);
            enddate.setHours(23,59,59,0);
            $scope.endDate = enddate;
        }
	};
	
	
	$scope.navigateTo = function(dimension, dimChange, openinnewtab){
		if(dimension.disabled)
			return;
		  var method = method || "post"; 
		  var form = document.createElement("form");
		  form.setAttribute("method", method);  
		  form.setAttribute("action", dimension.href);
		  if(openinnewtab){
			  form.setAttribute("target", "_blank");
			  var orig = $scope.breadcrumbs2.slice();
		  }
		  
		  var bcrumbSize  = $scope.breadcrumbs2.length;
		  if(dimChange){
			  for( k = 1; k< bcrumbSize  ; k ++){
					if(($scope.breadcrumbs2[k].rootDim == dimension.rootDim) 
								&& ($scope.breadcrumbs2[k].dim == "" || $scope.breadcrumbs2[k].dim == dimension.dim) ){
						continue;
					}else {
						if(bcrumbSize == 3 ){
							$scope.breadcrumbs2.splice(2, 1);
						}else if(bcrumbSize == 5 ){
							$scope.breadcrumbs2.splice(4, 1);
						}else if(bcrumbSize == 7 ){
							$scope.breadcrumbs2.splice(6, 1);
						}
						break;
					}
						
			  } 
		  }
		  bcrumbSize = $scope.breadcrumbs2.length;
		  for( k = bcrumbSize-1 ; k > -1; k-- ){
				if($scope.breadcrumbs2[k].href == dimension.href){
					$scope.breadcrumbs2[k].disabled = true;
					$scope.breadcrumbs2.splice(k+1, bcrumbSize-k-1 );
				}else
					$scope.breadcrumbs2[k].disabled = false;
		  } 
		  if(bcrumbSize == $scope.breadcrumbs2.length)
			  $scope.breadcrumbs2.push(dimension.breadcrumb);
		  var bcrumbs = JSON.stringify($scope.breadcrumbs2);
		  var params = {"startDate": $scope.dateToStr($scope.startDate), "endDate":$scope.dateToStr($scope.endDate), 
				  			"breadcrumbs":bcrumbs , "frequency": $scope.frequency, "supplysourcetype": $scope.supplysourcetype, "site_hygiene": $scope.site_hygiene};
		  
		  for(var key in params) {
		        if(params.hasOwnProperty(key)) {
		            var hiddenField = document.createElement("input");
		            hiddenField.setAttribute("type", "hidden");
		            hiddenField.setAttribute("name", key);
		            hiddenField.setAttribute("value", params[key]);
		            form.appendChild(hiddenField);
		         }
		    }
		  
		  document.body.appendChild(form);
		  form.submit();
		  if(openinnewtab){
			  $scope.breadcrumbs2 = orig.slice();
		  }
	}

	function pad0(a) {
		if( a < 10){
			return '0'+a;
		}
	    return a;
	}
	
	$scope.strToDate = function(dateStr){
		var b = dateStr.split(" ");
		var c = b[0].split("-");
		var d = b[1].split(":");
		return (new Date(c[0],(c[1]-1),c[2],d[0],d[1],0));
	}
	
	$scope.dateToStr = function(date){
		return date.getFullYear()+'-'+pad0((date.getMonth()+1))+'-'+pad0(date.getDate()) +' ' +pad0(date.getHours())+':'+pad0(date.getMinutes())+':00';
	}
	
	$scope.initReport = function(type, path, breadcrumbs, params){ 
		$scope.reportFilters.reportType= type;
		var paramObj = {};
		if(params != "")
			paramObj = JSON.parse(params);
		if($scope.breadcrumbs2.length==0 && breadcrumbs=="" ){
			$scope.breadcrumbs2.push({"name": type, "href":$scope.currentUrl,  "disabled":false, 
												"rootDim":$scope.reportFilters.reportType, "dim":""});
		}else{
			$scope.breadcrumbs2 = JSON.parse( breadcrumbs );
		}
		if(params != ""  && typeof(paramObj.startDate) != undefined)
				$scope.startDate = $scope.strToDate(paramObj.startDate);
		if(params != ""  && typeof(paramObj.endDate) != undefined)
				$scope.endDate = $scope.strToDate(paramObj.endDate);
		if(params != ""  && typeof(paramObj.frequency) != undefined)
			$scope.frequency = paramObj.frequency;
		if(params != ""  && typeof(paramObj.supplysourcetype) != undefined)
			$scope.supplysourcetype = paramObj.supplysourcetype;
		if(params != ""  && typeof(paramObj.site_hygiene) != undefined)
			$scope.site_hygiene = paramObj.site_hygiene;
		 var bcrumbSize = $scope.breadcrumbs2.length;
		  for( k = bcrumbSize-1 ; k > -1; k-- ){
				if($scope.breadcrumbs2[k].href == $scope.currentUrl){
					$scope.breadcrumbs2[k].disabled = true; 
					$scope.breadcrumbs2[k].active = "active"; 
				}else
					$scope.breadcrumbs2[k].disabled = false;
					$scope.breadcrumbs2[k].active = ""; 
		  }
		
		 
		$scope.reportFilters.crumbs= path;
		$scope.reportFilters.loadDimData= true;
		$scope.initComplete = true;
		var bcrumbSize = $scope.breadcrumbs2.length;
		var reportpath = "";
		for( i=0; i<bcrumbSize;i++){
			reportpath=reportpath+"_"+$scope.breadcrumbs2[i].name;
		}
		$scope.reportFilters.reportpath= reportpath;
		$scope.reportFilters.supplysourcetype = $( "#supplysourcetype option:selected" ).text();
		$scope.reportFilters.site_hygiene = $( "#site_hygiene option:selected" ).text();
//		$scope.loadData($scope.reportFilters, $scope.tableData); 
	}
	
	$scope.getUrl = function(dimension){
		var filter = "";
		var filterParts = $scope.filterParts;
		var path = $scope.path;
		var depth = $scope.filterDepth;
		var currentFilter = $scope.currentFilter;
		
		if(depth == (dimension.depth+1)){
			filter = ""; 
			for(i= 1 ; i < filterParts.length-2; i++){
				filter +="/"+filterParts[i];
			}
			//if( dimension.fieldid != "stats")
				filter += "/"+dimension.fieldid+"/"+dimension.default;  
		}else if(depth == dimension.depth){
			filter = "";
			if(filterParts[filterParts.length-1] == "all"){
				for(i= 1 ; i < filterParts.length-2; i++){
					filter +="/"+filterParts[i];
				}
				//if( dimension.fieldid != "stats")
					filter += "/"+dimension.fieldid+"/"+dimension.default; 
			}else{
				//if( dimension.fieldid != "stats")
					filter +=  currentFilter+"/"+dimension.fieldid+"/"+dimension.default;
				//else
				//	filter +=  currentFilter;
			}
			
		}else if(depth == 0||depth < dimension.depth){
			//if( dimension.fieldid != "stats")
				filter +=  currentFilter+"/"+dimension.fieldid+"/"+dimension.default;
		}
		var url = path;
		if(filter != "")
			url =  path+"?filter="+filter;
		return url;
	}
	
	
	$scope.updateTableDataCSV = function(pageNo, pageItems, filterBy, filterByFields, orderBy, orderByReverse){
		$scope.getCSV = true;
		$scope.loadData($scope.reportFilters, $scope.tableData, pageNo, orderByReverse);
	}
	
	$scope.updateTableData = function(pageNo, pageItems, filterBy, filterByFields, orderBy, orderByReverse){
		$scope.getCSV = false; 
		$scope.loadData($scope.reportFilters, $scope.tableData, pageNo, orderByReverse);
	}
	
	$scope.loadData = function(reportFilter, tableData, pageNo, orderByReverse){
		if(!$scope.initComplete || typeof(reportFilter) == "undefined" )
			return;
		var dataUrl = "/reports/hierarchical/data"; 
		$('#preloader').removeClass("hidden"); 
		reportFilter.startDate = $scope.dateToStr($scope.startDate);
		reportFilter.endDate = $scope.dateToStr($scope.endDate);
		reportFilter.frequency = $scope.frequency;
		reportFilter.supplysourcetype = $scope.supplysourcetype;
		reportFilter.site_hygiene = $scope.site_hygiene;
		reportFilter.getCSV = $scope.getCSV;
		
		if(typeof(tableData) != "undefined"){
			if (typeof(pageNo) != "undefined"){
				reportFilter.pageNo = pageNo;
			}else{
				reportFilter.pageNo = 0;
			}
			reportFilter.pageSize = tableData.itemsPerPage;
			if (typeof(tableData.orderBy) != "undefined"){
				reportFilter.orderBy = tableData.orderBy;
			}
			reportFilter.orderByReverse = orderByReverse;
		}else{
			reportFilter.pageNo = 0;
			reportFilter.pageSize = 500;
		}
		$http.post(dataUrl, reportFilter).success( function(response){  
			if(typeof(response.dimensions) != "undefined"){
				if(response.dimensions.length >1 ) 
					$scope.showtabs = true;
				else
					$scope.showtabs = false;
				$scope.dimensions = [];
				for(j = 0; j < response.dimensions.length; j++){
					response.dimensions[j].href = $scope.getUrl(response.dimensions[j]);
					response.dimensions[j].breadcrumb = {"name": response.dimensions[j].name, "href":response.dimensions[j].href, "disabled":false};
					if($scope.currentUrl == response.dimensions[j].href){
						response.dimensions[j].active = "active";
						response.dimensions[j].disabled =  true;
					}else{
						response.dimensions[j].active = "";
						response.dimensions[j].disabled = false;
					}
						
					$scope.dimensions.push(response.dimensions[j]);
				}
				
				if(typeof(response.tableData) != "undefined"){
					$scope.tableData.totalItems = response.tableData.count;
					$scope.tableData.data = response.tableData.data;  
					$scope.updateColumnData($scope.tableData, response.tableData.column); 
				}else{
					if($scope.getCSV==false){
						$scope.tableData.totalItems = 0;
						$scope.tableData.data = []; 
						alert("No data found")
					}
				}
			}
			$('#preloader').addClass("hidden");
			if($scope.getCSV==true){
				 //window.open(response.downloadurl,'Download','_self');
				 window.location.href = response.downloadurl;
			 }
			$scope.getCSV = false;
		}).error(function(response){
			$('#preloader').addClass("hidden"); 
		}); 
	}
	
	$scope.updateColumnData = function(table, columns){
		if(table != null && columns != null ){
			var fields = [];
			var columnArray = [];
			var bsize=$scope.breadcrumbs2.length;
			for(i = 0; i<  columns.length ; i++){
				if(i==0 && bsize%2 != 0){
					fields.push("drilldown");
				}
				if(columns[i].visible){
					fields.push(  columns[i].field);
					columnArray.push({"id": columns[i].field, "title":columns[i].title});
				}
				if(columns[i].clickable)
					$scope.tableData.filterField = columns[i].field;
			}
			$scope.tableData.fields = fields;
			$scope.tableData.columns = columnArray;
		}
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
	
	$scope.updateFilter = function(type, value){
		$scope.reportFilters[type] = value;
	}
	$scope.drillDown = function(gridItem){
        var selValue = gridItem;
        var filterField = $scope.tableData.filterField;
        var filterValue = selValue[filterField];
        
        var dest = {};
        if(typeof(filterField) != "undefined"){
            var nameField = filterField.replace("_id", "_name");
            if($scope.query == ""){  
                var filter = "/"+filterField+"/"+filterValue;
                dest.href= $scope.path+"?filter="+filter; 
                
            }else{ 
                dest.href = $scope.currentUrl.replace("all", filterValue); 
            }
            dest.breadcrumb = {"name":selValue[nameField], "href":dest.href, "disabled":false, "rootDim":$scope.reportFilters.reportType, "dim":filterField};
            dest.disabled = false;
            $scope.navigateTo(dest,false,true);
        }
        $scope.drilldowncalled=true;
    }
	 
	$scope.$watch('tableData.selections', function(newValue, oldValue) {
		if(typeof(newValue) != "undefined" && newValue.length == 1 && true!=$scope.drilldowncalled){
			var selValue = $scope.tableData.selections[0];
			var filterField = $scope.tableData.filterField;
			var filterValue = selValue[filterField];
			
			var dest = {};
			if(typeof(filterField) != "undefined"){
				var nameField = filterField.replace("_id", "_name");
				if($scope.query == ""){  
					var filter = "/"+filterField+"/"+filterValue;
					dest.href= $scope.path+"?filter="+filter; 
					
				}else{ 
					dest.href = $scope.currentUrl.replace("all", filterValue); 
				}
				dest.breadcrumb = {"name":selValue[nameField], "href":dest.href, "disabled":false, "rootDim":$scope.reportFilters.reportType, "dim":filterField};
				dest.disabled = false;
				$scope.navigateTo(dest);
			}
		}
		$scope.drilldowncalled = false;
		
	},true);
	
	
});
