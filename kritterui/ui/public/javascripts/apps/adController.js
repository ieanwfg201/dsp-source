/** Controllers */
angular.module('app.ui.form.ctrls', [ 'multi-select' ]).
    controller('adController',function ($scope, $http) {

    	$scope.tier1categories_url = "/metadata/options/tier1categories";
    	$scope.tier2categories_url = "/metadata/options/tier2categories";
    	$scope.hygiene_list_url = "/metadata/options/hygienelist";
		$scope.mma_tier1_url = "/metadata/options/tier1mmaindustry";
		$scope.mma_tier2_url = "/metadata/options/tier2mmaindustry/";
    	
    	
        $scope.hygiene_list = $("#hygiene_list").val();
        
        $scope.categories_tier_1_list_msmodel = Object.create(MultiSelectModel);
    	$scope.categories_tier_1_list_msmodel.init($scope.tier1categories_url, 'valueArray', true);
    	$scope.categories_tier_2_list_msmodel = Object.create(MultiSelectModel);
    	$scope.categories_tier_2_list_msmodel.init($scope.tier2categories_url, 'valueArray', true);
    	
    	$scope.hygiene_list_msmodel = Object.create(MultiSelectModel);
    	$scope.hygiene_list_msmodel.init($scope.hygiene_list_url, 'valueArray', true);
    	
		$scope.mma_tier_1_list_msmodel = Object.create(MultiSelectModel);  
		$scope.mma_tier_1_list_msmodel.init($scope.mma_tier1_url, 'valueArray', true);
	
		$scope.mma_tier_2_list_msmodel = Object.create(MultiSelectModel);  
		$scope.mma_tier_2_list_msmodel.init($scope.mma_tier2_url, 'valueArray', true);
		$scope.mma_tier_1_list_msmodel.addDependent("mma_tier2", $scope.mma_tier_2_list_msmodel);
    	
    	$scope.impMacro_url = "/advertiser/creatives/metadata/creativemacros";
    	$scope.impMacro= $("#impMacro").val();;
    	$scope.impMacro_msmodel = Object.create(MultiSelectModel);  
    	$scope.impMacro_msmodel.init($scope.impMacro_url, 'valueArray', true);
    	
    	$scope.clickMacro_url = "/advertiser/creatives/metadata/creativemacros";
    	$scope.clickMacro= $("#clickMacro").val();;
    	$scope.clickMacro_msmodel = Object.create(MultiSelectModel);  
    	$scope.clickMacro_msmodel.init($scope.clickMacro_url, 'valueArray', true);
        
        $scope.selectedHygiene = [];
        $scope.tier1selectedCategories = [];
        $scope.tier2selectedCategories = [];
         
        $scope.hygieneOptions = [];
        $scope.tier1categoryOptions = [];
        $scope.tier2categoryOptions = [];
        $scope.initComplete = false;
    	$scope.showPartner = false;
        $scope.isMSOpen = false;
        
        $scope.init= function(){ 
        	$scope.filterList($scope.hygieneOptions, $scope.hygiene_list, $scope.selectedHygiene ); 
        	$scope.filterList($scope.tier1categoryOptions, $scope.categories_tier_1_list, $scope.tier1selectedCategories );
        	$scope.filterList($scope.tier2categoryOptions, $scope.categories_tier_2_list, $scope.tier2selectedCategories );
    		$scope.updateAdMarketPlace();
        	$scope.initComplete = true;
        }
    	$scope.initAdMarketPlace = function(id){
    		$scope.marketplace_id = id;
    		$scope.updateAdMarketPlace();
    	}
    	
    	$scope.updateAdMarketPlace = function(){
    		switch($scope.marketplace_id) {
    			case "CPD":
    				$scope.showPartner = true;
    				break;
                case "CPC":
                    $scope.showPartner = true;
                    break;
                case "CPM":
                    $scope.showPartner = true;
                    break;
    			default:
                    $scope.showPartner = false;
    				break;
    		} 
    	}
    	$scope.initCurrency = function(field, val){
    		$scope[field+"_obj"] = parseFloat(val);
    	};
		$scope.$watch("advertiser_bid_obj", function(newValue, oldValue) {
			if(newValue == oldValue){
				return;
			}
    		if($scope.advertiser_bid_obj != 0.005){
    			var a = $scope.advertiser_bid_obj;
    			$scope.internal_max_bid_obj = a;
    		}
    	});
    	        
//        $scope.updateCreative= function(){
//        	var creative = $("#creative").val();
//        	var tmp = creative.split(":");       	
//        	$scope.creative_id = parseInt(tmp[0]);
//        	$scope.creative_guid =  tmp[1];
//        };
        
        $scope.filterList = function(options, selectedItems, targetArray){
        	if(selectedItems !=""){
        		var items = [];
        		try {
        			items = JSON.parse(selectedItems); 
        		}
        		catch(err) {
        			items = [];
        		}
        		if($.isArray(items)){
        			if(options){
        				options.forEach(function(entry){
        	        		if($.inArray(parseInt(entry.value),items)>=0){
        	        			//targetArray.push(entry);
        	        			entry.selected = true;
        	        		}
        	        		
        	        	});
        			}
        			
        		}else{
        			options.forEach(function(entry){
    	        		if(entry.value in items){
    	        			var obj = items[entry.value];
    	        			var tmp = obj.split("-");
    	        			if(tmp.length==2){
    	        				entry.min=tmp[0];
        	        			entry.max =tmp[1];
    	        			} 
    	        			entry.selected = true;
    	        		}else{
    	        			entry.min="all";
    	        			entry.max ="all";
    	        		}
    	        		
    	        	});
        		}
	        	
        	}
        }
        
        $scope.initMultiSelect =  function(selectedItemArrayRef, selectItemStringRef, captureVersion) { 
       	 $scope.$watch(selectedItemArrayRef, function(oldvalue,newvalue ) { 
       		 if($scope.initComplete){
       			  
       			 if(oldvalue.length == newvalue.length)
       				 $scope.isMSOpen = true;
       			 var selections = [];  
       			 if(captureVersion)
       				selections = {}; 
       			 $scope[selectedItemArrayRef].forEach(function(entry) {
                 		
                 		if(captureVersion){
                 			selections[entry.value+""]=entry.min+"-"+entry.max;
                 		}else{
                 			selections.push(parseInt(entry.value));
                 		}
                 });
                 $scope[selectItemStringRef]= JSON.stringify(selections);
       		 }
       		
            }, captureVersion);      	
       };

        $scope.initMultiSelect('selectedHygiene',  "hygiene_list", false);
		$scope.initMultiSelect('tier1selectedCategories',  "categories_tier_1_list", false);
		$scope.initMultiSelect('tier2selectedCategories',  "categories_tier_2_list", false);
         
        
        $scope.fetchInitialOptions = function () {
        	var url = "/advertiser/ads/options/defaultOptions";

            $http.get(url).success( function(response){
            	$scope.hygieneOptions = response.hygieneOptions;
                $scope.tier1categoryOptions = response.tier1categoryOptions;
                $scope.tier2categoryOptions = response.tier2categoryOptions;
            	$scope.init();
            }); 
        };
        
    
        $scope.fetchInitialOptions();
        
    }).controller('adListController',function ($scope, $http, $modal) {
    	$scope.status= "Pending";
    	$scope.currentPage=0;
    	$scope.maxPages = 5;
    	$scope.pageSize = 10;
    	$scope.totalItems = 30;

    	$scope.init = function(){
    		$scope.status= $.url().param("status");
    		$scope.pageSize = $.url().param("pageSize");
    		$scope.currentPage =  $.url().param("pageNo");
    	}
    	
    	$scope.init();
    	
    	$scope.fetch = function (page) {
    		var url = "/advertiser/campaigns/list?status="+$scope.status+"&pageNo="+page+"&pageSize="+$scope.pageSize;
    		location= url;
    	};

    	$scope.open = function (size,  guid, id, action) {
    		var modalInstance = $modal.open({
    			templateUrl: '/advertiser/campaigns/workflow/form?id='+id+'&accountGuid='+guid +'&action='+action, 
    			controller:campaignWorkflowController,
    			size: size 
    		});

    		modalInstance.result.then(
    				function () {
    					//.$log.info('Modal ok: ');
    				}, 
    				function() {
    					//$log.info('Modal dismissed at: ' + new Date());
    				}
    		);
    	};  
    });
