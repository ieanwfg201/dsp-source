/** Controllers */
angular.module('app.ui.form.ctrls', [  'angularFileUpload', 'multi-select' ]).
controller('tpController',function ($scope, $http, $upload) {

	$scope.checkvalid = function(obj){
		if(obj.value != "all" && obj.value != "none" )
			return true;
		return false;
	}
	
	$scope.setAccount = function(accountGuid){
		 $scope.accountGuid = accountGuid;
    }
	
	$scope.site_url = "/metadata/options/targeting_direct_sites/";
	$scope.ext_supply_attributes_by_pub_url = "/metadata/options/targeting_ext_site/";
	$scope.pub_url = "/metadata/options/directpublishers";
	$scope.exchange_url = "/metadata/options/exchanges";
	$scope.carrier_url = "/metadata/options/carriers/";
	$scope.connection_type_targeting_json_url = "/metadata/options/connection_type";
	$scope.brand_url = "/metadata/options/brands/";
	$scope.model_url = "/metadata/options/devices/";
	$scope.country_url = "/metadata/options/countries";
	$scope.device_url = "/metadata/options/devices/";
	$scope.browser_url = "/metadata/options/browsers";
	$scope.os_url = "/metadata/options/operating-systems";
	$scope.tier1categories_url = "/metadata/options/tier1categories";
	$scope.tier2categories_url = "/metadata/options/tier2categories";
	$scope.hour_list_url = "/metadata/options/hours";
	$scope.device_type_url = "/metadata/options/device_type";
	$scope.state_url = "/metadata/options/state/";
	$scope.city_url = "/metadata/options/city/";
	$scope.mma_tier1_url = "/metadata/options/tier1mmacategory";
	$scope.mma_tier2_url = "/metadata/options/tier2mmacategory/";
	$scope.adposition_list_url = "/metadata/options/adposition_list";
	$scope.channel_tier1_url = "/metadata/options/tier1channel";
	$scope.channel_tier2_url = "/metadata/options/tier2channel/";
	
	var path = $.url().attr('path');
    var pathComps = path.split("/");
    $scope.accountGuid = pathComps[2];  
	
	$scope.retargeting_url = "/metadata/options/retargeting_segment_by_adv/"+$scope.accountGuid;
	
	$scope.connection_type_targeting_json_msmodel = Object.create(MultiSelectModel);
	$scope.connection_type_targeting_json_msmodel.init($scope.connection_type_targeting_json_url, 'valueArray', true); 

    $scope.device_type_msmodel = Object.create(MultiSelectModel);  
    $scope.device_type_msmodel.init($scope.device_type_url, 'valueArray', true);
    
	//Country & Carrier
	$scope.country_json_msmodel = Object.create(MultiSelectModel);	
	$scope.country_json_msmodel.init($scope.country_url, 'valueArray', true);
	
	$scope.carrier_json_msmodel = Object.create(MultiSelectModel);
	$scope.carrier_json_msmodel.init($scope.carrier_url, 'valueArray', true); 

	$scope.state_json_msmodel = Object.create(MultiSelectModel);
	$scope.state_json_msmodel.init($scope.state_url, 'valueArray', true); 

	$scope.country_json_msmodel.addDependent("carriers", $scope.carrier_json_msmodel);
	$scope.country_json_msmodel.addDependent("state", $scope.state_json_msmodel);

	$scope.city_json_msmodel = Object.create(MultiSelectModel);
	$scope.city_json_msmodel.init($scope.city_url, 'valueArray', true); 
	$scope.state_json_msmodel.addDependent("city", $scope.city_json_msmodel);

	$scope.pub_list_msmodel = Object.create(MultiSelectModel);  
	$scope.pub_list_msmodel.init($scope.pub_url, 'valueArray', true);
	$scope.exchange_list_msmodel = Object.create(MultiSelectModel);  
	$scope.exchange_list_msmodel.init($scope.exchange_url, 'valueArray', true);
	
	//Sites and Hygiene
	$scope.site_list_msmodel = Object.create(MultiSelectModel);  
	$scope.site_list_msmodel.init($scope.site_url, 'valueArray', false, true);
	$scope.pub_list_msmodel.addDependent("sites", $scope.site_list_msmodel);
	
	$scope.ext_supply_attributes_msmodel = Object.create(MultiSelectModel);
	$scope.ext_supply_attributes_msmodel.init($scope.ext_supply_attributes_by_pub_url, 'valueArray', false, true);
	$scope.exchange_list_msmodel.addDependent("ext_supply_attributes", $scope.ext_supply_attributes_msmodel);

	
	$scope.categories_tier_1_list_msmodel = Object.create(MultiSelectModel);
	$scope.categories_tier_1_list_msmodel.init($scope.tier1categories_url, 'valueArray', true);
	$scope.categories_tier_2_list_msmodel = Object.create(MultiSelectModel);
	$scope.categories_tier_2_list_msmodel.init($scope.tier2categories_url, 'valueArray', true);
	
	$scope.hours_list_msmodel = Object.create(MultiSelectModel);
	$scope.hours_list_msmodel.init($scope.hour_list_url, 'valueArray', true);

    $scope.retargeting_msmodel = Object.create(MultiSelectModel);
    $scope.retargeting_msmodel.init($scope.retargeting_url, 'valueArray', true);
	
	//Browser
	$scope.browser_json_msmodel = Object.create(MultiSelectModel);
	$scope.browser_json_msmodel.init($scope.browser_url, 'objectWithVersion', true);
	
	//os, brand and model
	$scope.os_json_msmodel = Object.create(MultiSelectModel);
	$scope.os_json_msmodel.init($scope.os_url, 'objectWithVersion', true);
	
	$scope.brand_list_msmodel = Object.create(MultiSelectModel);
	$scope.brand_list_msmodel.init($scope.brand_url, 'valueArray', true);
	$scope.os_json_msmodel.addDependent("brands", $scope.brand_list_msmodel);
	
	$scope.model_list_msmodel = Object.create(MultiSelectModel);
	$scope.async = true;
	$scope.model_list_msmodel.init( $scope.model_url, 'valueArray', true);
	$scope.brand_list_msmodel.addDependent("models", $scope.model_list_msmodel);

	$scope.mma_tier_1_list_msmodel = Object.create(MultiSelectModel);  
	$scope.mma_tier_1_list_msmodel.init($scope.mma_tier1_url, 'valueArray', true, false);
	
	$scope.mma_tier_2_list_msmodel = Object.create(MultiSelectModel);  
	$scope.mma_tier_2_list_msmodel.init($scope.mma_tier2_url, 'valueArray', true);
	$scope.mma_tier_1_list_msmodel.addDependent("mma_tier2", $scope.mma_tier_2_list_msmodel);

	$scope.adposition_list_msmodel = Object.create(MultiSelectModel);  
	$scope.adposition_list_msmodel.init($scope.adposition_list_url, 'valueArray', true, false);
	
	$scope.channel_tier_1_list_msmodel = Object.create(MultiSelectModel);  
	$scope.channel_tier_1_list_msmodel.init($scope.channel_tier1_url, 'valueArray', true, false);
	
	$scope.channel_tier_2_list_msmodel = Object.create(MultiSelectModel);  
	$scope.channel_tier_2_list_msmodel.init($scope.channel_tier2_url, 'valueArray', true);
	$scope.channel_tier_1_list_msmodel.addDependent("channel_tier2", $scope.channel_tier_2_list_msmodel);

	$scope.custom_ip_file_id_set="#";
	$scope.lat_lon_radius_file="#";

	$scope.listUpdated = false;

	$scope.init= function(){ 
		$scope.handleGeoTargetingChange();
		$scope.handleSupplySourceChange();
		$scope.initComplete = true;
	}
	
	$scope.saveVersions = function(obj){
		$scope[obj].onClose();
	}

	$scope.handleGeotargetingFileUpload = function($files, geoTargetingType){
		for (var i = 0; i < $files.length; i++) {
			var file = $files[i];
			$scope.upload = $upload.upload({
				url: '/advertiser/targeting-profiles/upload-geotarget-data',  
				data: {"account_guid":$scope.accountGuid, "targeting_type":geoTargetingType},
				file: file, // or list of files: $files for html5 only
			}).progress(function(evt) {
				$("#messagePlaceholder").html('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
			}).success(function(data, status, headers, config) {  
				$("input[name='"+geoTargetingType+"']").attr("value",data.path);
				$scope[geoTargetingType+"_preview_url"] = data.preview_url;
				$scope[geoTargetingType+"_preview"] = true;
				$scope[geoTargetingType+"_preview_label"] = data.preview_label;
				$scope[geoTargetingType+"_message"] = data.message; 
			}).error(function(data, status, headers, config) { 
				$scope[geoTargetingType+"_preview"] = false;
				$scope[geoTargetingType+"_message"] = data.message; 
			}) 
		} 
	}



	$scope.handleGeoTargetingChange = function(){
		switch($scope.geo_targeting_type) {
			case "COUNTRY_CARRIER": 
				$scope.showCountrycarrierSelector = true;
				$scope.showzipcodeListSelector = false;
				$scope.showIplistSelector = false;
				break;
	
			case "ZIPCODE": 
				$scope.showCountrycarrierSelector = false;
				$scope.showzipcodeListSelector = true;
				$scope.showIplistSelector = false;
				var filePath =  $("input[name='zipcode_file_id_set']").attr("value");
				if(filePath!=""){
					$scope['zipcode_file_id_set_preview'] = true; 
					$scope['zipcode_file_id_set_preview_url'] = "/download?file="+encodeURI(filePath);
					var fileName = filePath.substring(filePath.lastIndexOf('/')+1);
					$scope['zipcode_file_id_set_preview_label'] = fileName;
				}else
					$scope['zipcode_file_id_set_preview'] = false;
	
				break;
	
			case "IP":
				$scope.showCountrycarrierSelector = false;
				$scope.showzipcodeListSelector = false;
				$scope.showIplistSelector = true;
				var filePath =  $("input[name='custom_ip_file_id_set']").attr("value");
				if($scope['custom_ip_file_id_set']!=""){
					$scope['custom_ip_file_id_set_preview'] = true;
					$scope['custom_ip_file_id_set_preview_url'] = "/download?file="+encodeURI(filePath);
					var fileName = filePath.substring(filePath.lastIndexOf('/')+1);
					$scope['custom_ip_file_id_set_preview_label'] = fileName;
				}else
					$scope['custom_ip_file_id_set_preview'] = false;
				break;
			default: 
				break;
		}
		var filePath =  $("input[name='lat_lon_radius_file']").attr("value");
		if($scope['lat_lon_radius_file']!=""){
					$scope['lat_lon_radius_file_preview'] = true;
					$scope['lat_lon_radius_file_preview_url'] = "/download?file="+encodeURI(filePath);
					var fileName = filePath.substring(filePath.lastIndexOf('/')+1);
					$scope['lat_lon_radius_file_preview_label'] = fileName;
		}else{
			$scope['lat_lon_radius_file_preview'] = false;
		}
	}
	
	$scope.handleSupplySourceChange = function(){
		switch($scope.supply_source) {
			case "EXCHANGE": 
				$scope.showDirPubSelector = false;
				$scope.showExchangeSelector = true;
				break;
			case "NETWORK": 
				$scope.showDirPubSelector = true;
				$scope.showExchangeSelector = false;
				break;
			case "EXCHANGE_NETWORK":
				$scope.showDirPubSelector = true;
				$scope.showExchangeSelector = true;
				break;
			default: 
				break;
		}		
	}
	
	$scope.$watch($scope.geo_targeting_type, function(newvalue,oldvalue ) { 
		$scope.handleGeoTargetingChange(); 
	});    
	$scope.$watch($scope.supply_source, function(newvalue,oldvalue ) { 
		$scope.handleSupplySourceChange(); 
	});    
	
	$scope.saveTP= function(){ 
		$scope.browser_json_msmodel.onClose(); 
		$('#browser_json').val($scope.browser_json_msmodel.value);
		$scope.os_json_msmodel.onClose();
		$('#os_json').val($scope.os_json_msmodel.value);
		var action_url = $('#tpForm').submit();  
	}
	

}).directive('versionselector', function() {
	return {
		restrict: 'AE',
		scope: {
			model: '= vsdata'
		},
		controller:function($scope, $compile, $http) {
			if($scope.model.metadata  === undefined){
				
			}else{
				$scope.minOptions = JSON.parse($scope.model.metadata).sort(); 
				$scope.minOptions.unshift("all");

				$scope.updateMaxOptions = function(index){
					$scope.maxOptions = $scope.minOptions.splice(index);
				}
			} 
		},
		templateUrl: '/assets/ngtemplates/version-selector.html'
	};
});; 
