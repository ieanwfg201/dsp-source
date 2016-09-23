angular.module('app.ui.form.ctrls', ['ngPasswordStrength','angularFileUpload']).
    controller('accountController',function ($scope, $http, $upload) {
    	$scope.showPaymentOptions = false;
    	$scope.showAdvOptions = true;
    	$scope.lat_lon_radius_file="#";
    	
    	 
    	
    	$scope.type_id = $("#type_id").attr("value"); 
    	if($scope.type_id=="directpublisher"){
    		$scope.showPaymentOptions = true;
        	$scope.showAdvOptions = false;
    	}
    	$scope.init= function(){ 
			$scope.handleFileChange();
		}
		$scope.handleFileChange = function(){
		var filePath =  $("input[name='qualificationUrl']").attr("value");
		if($scope['qualificationUrl']!=""){
					$scope['qualificationUrl_preview'] = true;
					$scope['qualificationUrl_preview_url'] = "/download?file="+encodeURI(filePath);
					var fileName = filePath.substring(filePath.lastIndexOf('/')+1);
					$scope['qualificationUrl_preview_label'] = fileName;
		}else{
			$scope['qualificationUrl_preview'] = false;
		}
		}
		
    	
		$scope.handleQualificationUpload = function($files, geoTargetingType){
		for (var i = 0; i < $files.length; i++) {
			var file = $files[i];
			$scope.upload = $upload.upload({
				url: '/accounts/qualificationimage-upload ',  
				data: {"targeting_type":geoTargetingType},
				file: file, // or list of files: $files for html5 only
			}).progress(function(evt) {
				$("#messagePlaceholder").html('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
			}).success(function(data, status, headers, config) {  
				$("input[name='"+geoTargetingType+"']").attr("value",data.path);
				$scope[geoTargetingType+"_preview_url"] = data.preview_url;
				$scope[geoTargetingType+"_preview"] = true;
				$scope[geoTargetingType+"_preview_label"] = data.preview_label;
				$scope[geoTargetingType+"_message"] = data.message; 
				$scope[geoTargetingType+"_md5"] = data.md5;
				$("#qualificationMD5").attr("value",data.md5);
			}).error(function(data, status, headers, config) { 
				$scope[geoTargetingType+"_preview"] = false;
				$scope[geoTargetingType+"_message"] = data.message; 
			}) 
		} 
	}
    	 		
});
