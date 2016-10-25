/** Controllers */
angular.module('app.ui.form.ctrls', [ "angularFileUpload",    'multi-select'  ]).
controller('creativeController',function ($scope, $http, $modal, FileUploader) {
	$scope.format_id =  "";
	$scope.videoDemandType =  $("#videoDemandType").val();
	$scope.creativeId = $("#id").val();
	$scope.showTextForm = false;
	$scope.showHtmlForm = false;
	$scope.showBannerForm = false;
	$scope.showNativeForm = false;
	$scope.showVideoForm = false;
	$scope.showVideoWrapperForm = false;
	$scope.showDirectVideoForm = false;
	$scope.resource_uri_ids= $("#resource_uri_ids").val();
	$scope.native_icons= $("#native_icons").val();
	$scope.native_screenshots= $("#native_screenshots").val();
	$scope.direct_videos= $("#direct_videos").val();
	$scope.uploader = null;
	$scope.iconuploader = null;
	$scope.screenshotuploader = null;
	$scope.directvideouploader = null;
	$scope.banners= [];
	$scope.nativeicons= [];
	$scope.nativescreenshots= [];
	$scope.directvideos= [];

	$scope.creativeAttrOptions = [];
	$scope.creativeAttrList = [];
	$scope.creative_attr= $("#creative_attr").val();;
	$scope.creativeAttrInit = false;
	
	$scope.accountGuid = $("#account_guid").val();
	
	$scope.creative_attr_msmodel = Object.create(MultiSelectModel);  
	$scope.creative_attr_msmodel.init("", 'valueArray', true);
    
    $scope.creative_macro_url = "/advertiser/creatives/metadata/creativemacros";
    $scope.creative_macro= $("#creative_macro").val();;
    $scope.creative_macro_msmodel = Object.create(MultiSelectModel);  
    $scope.creative_macro_msmodel.init($scope.creative_macro_url, 'valueArray', true);

    $scope.vastTagMacro_url = "/advertiser/creatives/metadata/creativemacros";
    $scope.vastTagMacro= $("#vastTagMacro").val();;
    $scope.vastTagMacro_msmodel = Object.create(MultiSelectModel);  
    $scope.vastTagMacro_msmodel.init($scope.vastTagMacro_url, 'valueArray', true);
    
    $scope.trackingStr_url = "/advertiser/creatives/metadata/videotracking";
    $scope.trackingStr= $("#trackingStr").val();;
    $scope.trackingStr_msmodel = Object.create(MultiSelectModel);  
    $scope.trackingStr_msmodel.init($scope.trackingStr_url, 'valueArray', true);
	
	$scope.updateCreativeAttrs= function(){
		if($scope.format_id != "")
			$scope.creative_attr_msmodel.setUrl("/advertiser/creatives/metadata/creativeattrs/"+$scope.format_id);
    }
	
	$scope.updateCreativeType = function(){
		
		switch($scope.format_id) {
			case "1":
				$scope.showBannerForm = false;
				$scope.showTextForm = true;
				$scope.showHtmlForm = false;
				$scope.showNativeForm = false;
				$scope.showVideoForm = false;
				$scope.showVideoWrapperForm = false;
				$scope.showDirectVideoForm = false;
				break;
			
			case "2":
				$scope.showBannerForm = true;
				$scope.showTextForm = false;
				$scope.showHtmlForm = false;
				$scope.showNativeForm = false;
				$scope.showVideoForm = false;
				$scope.showVideoWrapperForm = false;
				$scope.showDirectVideoForm = false;
				$scope.loadBanners();
				break;
				
			case "3":
				$scope.showBannerForm = false;
				$scope.showTextForm = false;
				$scope.showHtmlForm = true;
				$scope.showNativeForm = false;
				$scope.showVideoForm = false;
				$scope.showVideoWrapperForm = false;
				$scope.showDirectVideoForm = false;
				break;
            case "4":
                $scope.showBannerForm = false;
                $scope.showTextForm = false;
                $scope.showHtmlForm = false;
                $scope.showNativeForm = false;
                $scope.showVideoForm = true;
                $scope.updateVideoDemandType();
                break;
            case "51":
                $scope.showBannerForm = false;
                $scope.showTextForm = false;
                $scope.showHtmlForm = false;
                $scope.showNativeForm = true;
                $scope.showVideoForm = false;
                $scope.showVideoWrapperForm = false;
				$scope.showDirectVideoForm = false;
                $scope.loadNativeicons();
                $scope.loadNativescreenshots();
                break;
			default: 
				break;
		}
		$scope.updateCreativeAttrs();
	}

	$scope.updateVideoDemandType = function(){
		$scope.showVideoWrapperForm = false; 
		if($scope.videoDemandType == "1") {
				$scope.showVideoWrapperForm = true;
				$scope.showDirectVideoForm = false;
		}
		if($scope.videoDemandType == "2") {
				$scope.showDirectVideoForm = true;
				$scope.showVideoWrapperForm = false;
				$scope.loadDirectvideos();
		}
	}

	$scope.removeBanner = function(banner){
		for(var i = $scope.banners.length - 1; i >= 0; i--) {
		    if($scope.banners[i].id === banner.id) {
		       $scope.banners.splice(i, 1);
		    }
		}
	};
	$scope.removeDirectvideos = function(directvideo){
        for(var i = $scope.directvideos.length - 1; i >= 0; i--) {
            if($scope.directvideos[i].id === directvideo.id) {
               $scope.directvideos.splice(i, 1);
            }
        }
    };
	$scope.removeNativeicons = function(nativeicon){
        for(var i = $scope.nativeicons.length - 1; i >= 0; i--) {
            if($scope.nativeicons[i].id === nativeicon.id) {
               $scope.nativeicons.splice(i, 1);
            }
        }
    };
    $scope.removeNativescreenshots = function(nativescreenshot){
        for(var i = $scope.nativescreenshots.length - 1; i >= 0; i--) {
            if($scope.nativescreenshots[i].id === nativescreenshot.id) {
               $scope.nativescreenshots.splice(i, 1);
            }
        }
    };
    
    $scope.$watch("banners.length", function(oldvalue,newvalue ) {  
        var selections = []; 
        $scope["banners"].forEach(function(entry) {
            selections.push(entry.id);
        });
        $scope.resource_uri_ids=selections.join();
    });   
	
	$scope.$watch("directvideos.length", function(oldvalue,newvalue ) {  
        var selections = []; 
        $scope["directvideos"].forEach(function(entry) {
            selections.push(entry.id);
        });
        $scope.direct_videos=selections.join();
    }); 
    
    $scope.$watch("nativeicons.length", function(oldvalue,newvalue ) {  
        var selections = []; 
        $scope["nativeicons"].forEach(function(entry) {
            selections.push(entry.id);
        });
        $scope.native_icons=selections.join();
    });   
     
    $scope.$watch("nativescreenshots.length", function(oldvalue,newvalue ) {  
        var selections = []; 
        $scope["nativescreenshots"].forEach(function(entry) {
            selections.push(entry.id);
        });
        $scope.native_screenshots=selections.join();
    });   
      
      $scope.previewBanner= function(bannerId){ 
    	  var modalInstance = $modal.open({
    	      templateUrl: bannerId, 
    	      size: 'large', 
    	    });		  
      }

      $scope.previewDirectvideo= function(directvideoId){ 
          var modalInstance = $modal.open({
              templateUrl: directvideoId, 
              size: 'large', 
            });       
      }

      $scope.previewNativeicon= function(nativeiconId){ 
          var modalInstance = $modal.open({
              templateUrl: nativeiconId, 
              size: 'large', 
            });       
      }
      $scope.previewNativescreenshot= function(nativescreenshotId){ 
          var modalInstance = $modal.open({
              templateUrl: nativescreenshotId, 
              size: 'large', 
            });       
      }
      
      $scope.loadBanners= function(){
    	  if($scope.creativeId!= -1){
    		  $url = "/advertiser/creatives/"+$scope.creativeId+"/banners";
        	  $http.get($url).success( function(response){
                 	$scope.banners = response;                  	
              }); 
    	  } 		  
      }
      
      $scope.loadDirectvideos= function(){
          if($scope.creativeId!= -1){
              $url = "/advertiser/creatives/"+$scope.creativeId+"/directvideos";
              $http.get($url).success( function(response){
                    $scope.directvideos = response;                      
              }); 
          }           
      }

      $scope.loadNativeicons= function(){
          if($scope.creativeId!= -1){
              $url = "/advertiser/creatives/"+$scope.creativeId+"/nativeicons";
              $http.get($url).success( function(response){
                    $scope.nativeicons = response;                      
              }); 
          }           
      }
      
      $scope.loadNativescreenshots= function(){
          if($scope.creativeId!= -1){
              $url = "/advertiser/creatives/"+$scope.creativeId+"/nativescreenshots";
              $http.get($url).success( function(response){
                    $scope.nativescreenshots = response;                      
              }); 
          }           
      }
      
      
      $scope.init = function(){
    	  $scope.format_id = $("#format_id").val();
    	  $scope.videoDemandType = $("#videoDemandType").val();
    	  $scope.updateCreativeType(); 
      }
       
       $scope.init();
       
       
    $scope.openBannerForm = function () {	
    	var modalInstance = $modal.open({
			templateUrl: "/template/load/advt.creative.multi_banner_uploader", 
			controller:bannerController,
			size: "medium",
			resolve: {
				creativeInfo: function () {
					var creativeInfo = {};
					creativeInfo.accountGuid = $scope.accountGuid;
					//bannerInfo.creativeid = $scope.accountGuid;
					return creativeInfo;
		        }
		    }
		});
		modalInstance.result.then(
				function (newBanners) {
					var selections = []; 
					for( i = 0 ; i< newBanners.length; i++){
						$scope.banners.push(newBanners[i]);
					}
					$scope.banners.forEach(function(entry) {
						selections.push(entry.id);
					});
					$scope.resource_uri_ids=selections.join();
					$("#resource_uri_ids").attr("value", $scope.resource_uri_ids );
				}, 
				function() {
				}
		);
		return false;
	};
		
	$scope.openDirectvideoForm = function () {  
        var modalInstance = $modal.open({
            templateUrl: "/template/load/advt.creative.multi_direct_video_uploader", 
            controller:directvideoController,
            size: "medium",
            resolve: {
                creativeInfo: function () {
                    var creativeInfo = {};
                    creativeInfo.accountGuid = $scope.accountGuid;
                    return creativeInfo;
                }
            }
        });
        modalInstance.result.then(
                function (newdirectvideos) {
                    var selections = []; 
                    for( i = 0 ; i< newdirectvideos.length; i++){
                        $scope.directvideos.push(newdirectvideos[i]);
                    }
                    $scope.directvideos.forEach(function(entry) {
                        selections.push(entry.id);
                    });
                    $scope.direct_videos=selections.join();
                    $("#direct_videos").attr("value", $scope.direct_videos );
                }, 
                function() {
                }
        );
        return false;
    };
	
	$scope.openNativeiconForm = function () {  
        var modalInstance = $modal.open({
            templateUrl: "/template/load/advt.creative.multi_icon_uploader", 
            controller:iconController,
            size: "medium",
            resolve: {
                creativeInfo: function () {
                    var creativeInfo = {};
                    creativeInfo.accountGuid = $scope.accountGuid;
                    //bannerInfo.creativeid = $scope.accountGuid;
                    return creativeInfo;
                }
            }
        });
        modalInstance.result.then(
                function (newIcons) {
                    var selections = []; 
                    for( i = 0 ; i< newIcons.length; i++){
                        $scope.nativeicons.push(newIcons[i]);
                    }
                    $scope.nativeicons.forEach(function(entry) {
                        selections.push(entry.id);
                    });
                    $scope.native_icons=selections.join();
                    $("#native_icons").attr("value", $scope.native_icons );
                }, 
                function() {
                }
        );
        return false;
    };
    $scope.openNativescreenshotForm = function () {  
        var modalInstance = $modal.open({
            templateUrl: "/template/load/advt.creative.multi_screenshot_uploader", 
            controller:screenshotController,
            size: "medium",
            resolve: {
                creativeInfo: function () {
                    var creativeInfo = {};
                    creativeInfo.accountGuid = $scope.accountGuid;
                    //bannerInfo.creativeid = $scope.accountGuid;
                    return creativeInfo;
                }
            }
        });
        modalInstance.result.then(
                function (newScreenshots) {
                    var selections = []; 
                    for( i = 0 ; i< newScreenshots.length; i++){
                        $scope.nativescreenshots.push(newScreenshots[i]);
                    }
                    $scope.nativescreenshots.forEach(function(entry) {
                        selections.push(entry.id);
                    });
                    $scope.native_screenshots=selections.join();
                    $("#native_screenshots").attr("value", $scope.native_screenshots );
                }, 
                function() {
                }
        );
        return false;
    }; 	
}).controller('creativeDisplayController',function ($scope, $http, $modal, $upload) {
      $scope.previewBanner= function(bannerId){ 
    	  var modalInstance = $modal.open({
    	      templateUrl: bannerId, 
    	      size: 'large', 
    	    });		  
      };
      $scope.previewDirectvideo= function(directvideoId){ 
          var modalInstance = $modal.open({
              templateUrl: directvideoId, 
              size: 'large', 
            });       
      }
      $scope.previewNativeicon= function(nativeiconId){ 
          var modalInstance = $modal.open({
              templateUrl: nativeiconId, 
              size: 'large', 
            });       
      }
      $scope.previewNativescreenshot= function(nativescreenshotId){ 
          var modalInstance = $modal.open({
              templateUrl: nativescreenshotId, 
              size: 'large', 
            });       
      }
       	
});

var bannerController = function ($scope, $http,  $modalInstance, FileUploader, creativeInfo) {
	$scope.showFileSelect = false;
	$scope.creativeInfo = creativeInfo; 	
	$scope.bannerValidationUrl = "";  
	$scope.hasValidBanners = false; 
	$scope.hasInValidBanners = false;
	$scope.slotOptions = [];
	$scope.loadOptionsUrl = "/metadata/options/slot_options";
	
	$scope.loadSlotOptions = function(){
		$('#preloader').removeClass("hidden");  
		$http.get($scope.loadOptionsUrl).success( function(response){  
			for(  i= 0 ; i< response.length; i++){
				var parts = response[i].label.split(":");
				$scope.slotOptions.push({"name":parts[0], "description":parts[1]});
			}
			$('#preloader').addClass("hidden");  
		}).error(function(response){
			 
			$('#preloader').addClass("hidden");  
		});
	}
	
	$scope.loadSlotOptions();
	
	$scope.checkValidBanners = function(){
		$scope.hasValidBanners = false; 
		$scope.hasInValidBanners = false;
		for( i= 0 ; i<  $scope.uploader.queue.length; i++){
			item = $scope.uploader.queue[i];
			if(item.isSuccess){ 
				$scope.hasValidBanners = true;
			}else if(item.isError){ 
				$scope.hasInValidBanners = true;
			}
		}
	}
	
	$scope.uploader = new FileUploader({
        url:  "/advertiser/creatives/banner/upload/"+creativeInfo.accountGuid
    });  
	
	$scope.uploader.filters.push({
        name: 'imageFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    });
	
	$scope.$watch("uploader.queue.length", function(newValue, oldValue) {
		$scope.checkValidBanners();
	});

    // CALLBACKS

 
	$scope.uploader.onAfterAddingFile = function(fileItem) {
    	fileItem.slotOptions = {};
    	fileItem.slot="";
    	fileItem.showOptions = true;
    	fileItem.showError = false;
        console.info('onAfterAddingFile', fileItem);
        fileItem.upload();
    };
 
	$scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {   
		fileItem.slotOptions = response.slotOptions;
		fileItem.slot=response.slotOptions[0];
		fileItem.showOptions = true;
		fileItem.showError = false; 
		fileItem.thumbUrl = response.thumbUrl;
		fileItem.bannerUrl = response.bannerUrl; 
		$scope.checkValidBanners();
    };
    
    $scope.uploader.onErrorItem = function(fileItem, response, status, headers) {
    	fileItem.showOptions = false;
		fileItem.showError = true;
		fileItem.message = "Invalid Dimensions";
		$scope.checkValidBanners();
    };
 
//    $scope.uploader.onCompleteItem = function(fileItem, response, status, headers) {
//        //console.info('onCompleteItem', fileItem, response, status, headers);
//    };
//    $scope.uploader.onCompleteAll = function() {
//        //console.info('onCompleteAll');
//    };

    
	$scope.ok = function () {
		var saveBannerUrl = '/advertiser/creatives/banner/save';  
		var banners = [];
		var banner = {};
		var item = null;
		
		var hasValidBanners = false;
		$('#preloader').removeClass("hidden");  
		for( i= 0 ; i<  $scope.uploader.queue.length; i++){
			item = $scope.uploader.queue[i];
			if(item.isSuccess){
				banner = {};
				banner.account_guid = creativeInfo.accountGuid; 
				banner.slot_id = item.slot.value;
				var slotParts = item.slot.label.split("-");
				banner.slotName = slotParts[0];
				banner.slotDescription = slotParts[1];
				banner.resource_uri = item.bannerUrl; 
				banners.push(banner);
			} 
		}
		
		if($scope.hasInValidBanners){
			alert("There are invalid banners in the queue. Those will be ignored. ")
		}
		if($scope.hasValidBanners){
			$http.post(saveBannerUrl,{"banners":banners}).success( function(response){  
				$modalInstance.close(response.banners);		
				$('#preloader').addClass("hidden");  
			}).error(function(response){
				alert("Encountered a Problem saving banners to database");
				$('#preloader').addClass("hidden");  
			});
		}
		
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
		return false;
	};

};

var directvideoController = function ($scope, $http,  $modalInstance, FileUploader, creativeInfo) {
    $scope.showFileSelect = false;
    $scope.creativeInfo = creativeInfo;     
    $scope.directvideoValidationUrl = "";  
    $scope.hasValidDirectvideos = false; 
    $scope.hasInValidDirectvideos = false;
    $scope.slotOptions = [];
    $scope.loadOptionsUrl = "/metadata/options/slot_options ";
    
    $scope.loadSlotOptions = function(){
        $('#preloader').removeClass("hidden");  
        $http.get($scope.loadOptionsUrl).success( function(response){  
            for(  i= 0 ; i< response.length; i++){
                var parts = response[i].label.split(":");
                $scope.slotOptions.push({"name":parts[0], "description":parts[1]});
            }
            $('#preloader').addClass("hidden");  
        }).error(function(response){
             
            $('#preloader').addClass("hidden");  
        });
    }
    
    $scope.loadSlotOptions();
    
    $scope.checkValidDirectvideos = function(){
        $scope.hasValidDirectvideos = false; 
        $scope.hasInValidDirectvideos = false;
        for( i= 0 ; i<  $scope.directvideouploader.queue.length; i++){
            item = $scope.directvideouploader.queue[i];
            if(item.isSuccess){ 
                $scope.hasValidDirectvideos = true;
            }else if(item.isError){ 
                $scope.hasInValidDirectvideos = true;
            }
        }
    }
    
    $scope.directvideouploader = new FileUploader({
        url:  "/advertiser/creatives/directvideo/upload/"+creativeInfo.accountGuid
    });  
    
    $scope.directvideouploader.filters.push({
        name: 'imageFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|x-flv|x-mpegURL|MP2T|3gpp|quicktime|x-msvideo|x-ms-wmv|flv|mp4|m3u8|ts|3gp|mov|avi|wmv'.indexOf(type) !== -1;
        }
    });
    
    $scope.$watch("directvideouploader.queue.length", function(newValue, oldValue) {
        $scope.checkValidDirectvideos();
    });

    // CALLBACKS

 
    $scope.directvideouploader.onAfterAddingFile = function(fileItem) {
        fileItem.slotOptions = {};
        fileItem.slot="";
        fileItem.showOptions = true;
        fileItem.showError = false;
        console.info('onAfterAddingFile', fileItem);
        fileItem.upload();
    };
 
    $scope.directvideouploader.onSuccessItem = function(fileItem, response, status, headers) {   
        fileItem.slotOptions = response.slotOptions;
        fileItem.slot=response.slotOptions[0];
        fileItem.showOptions = true;
        fileItem.showError = false; 
        fileItem.thumbUrl = response.thumbUrl;
        fileItem.bannerUrl = response.bannerUrl; 
        $scope.checkValidDirectvideos();
    };
    
    $scope.directvideouploader.onErrorItem = function(fileItem, response, status, headers) {
        fileItem.showOptions = false;
        fileItem.showError = true;
        fileItem.message = "Invalid Dimensions";
        $scope.checkValidDirectvideos();
    };
 
//    $scope.directvideouploader.onCompleteItem = function(fileItem, response, status, headers) {
//        //console.info('onCompleteItem', fileItem, response, status, headers);
//    };
//    $scope.directvideouploader.onCompleteAll = function() {
//        //console.info('onCompleteAll');
//    };

    
    $scope.ok = function () {
        var saveDirectvideoUrl = '/advertiser/creatives/directvideo/save';  
        var directvideos = [];
        var directvideo = {};
        var item = null;
        
        var hasValidDirectvideos = false;
        $('#preloader').removeClass("hidden");  
        for( i= 0 ; i<  $scope.directvideouploader.queue.length; i++){
            item = $scope.directvideouploader.queue[i];
            if(item.isSuccess){
                directvideo = {};
                directvideo.account_guid = creativeInfo.accountGuid; 
                directvideo.video_size = item.slot.value;
                var slotParts = item.slot.label.split("-");
                directvideo.slotName = slotParts[0];
                directvideo.slotDescription = slotParts[1];
                directvideo.resource_uri = item.bannerUrl; 
                directvideos.push(directvideo);
            } 
        }
        
        if($scope.hasInValidDirectvideos){
            alert("There are invalid directvideos in the queue. Those will be ignored. ")
        }
        if($scope.hasValidDirectvideos){
            $http.post(saveDirectvideoUrl,{"directvideos":directvideos}).success( function(response){  
                $modalInstance.close(response.directvideos);     
                $('#preloader').addClass("hidden");  
            }).error(function(response){
                alert("Encountered a Problem saving directvideos to database");
                $('#preloader').addClass("hidden");  
            });
        }
        
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
        return false;
    };

};

var iconController = function ($scope, $http,  $modalInstance, FileUploader, creativeInfo) {
    $scope.showFileSelect = false;
    $scope.creativeInfo = creativeInfo;     
    $scope.iconValidationUrl = "";  
    $scope.hasValidIcons = false; 
    $scope.hasInValidIcons = false;
    $scope.slotOptions = [];
    $scope.loadOptionsUrl = "/metadata/options/icon_slot_options ";
    
    $scope.loadSlotOptions = function(){
        $('#preloader').removeClass("hidden");  
        $http.get($scope.loadOptionsUrl).success( function(response){  
            for(  i= 0 ; i< response.length; i++){
                var parts = response[i].label.split(":");
                $scope.slotOptions.push({"name":parts[0], "description":parts[1]});
            }
            $('#preloader').addClass("hidden");  
        }).error(function(response){
             
            $('#preloader').addClass("hidden");  
        });
    }
    
    $scope.loadSlotOptions();
    
    $scope.checkValidIcons = function(){
        $scope.hasValidIcons = false; 
        $scope.hasInValidIcons = false;
        for( i= 0 ; i<  $scope.iconuploader.queue.length; i++){
            item = $scope.iconuploader.queue[i];
            if(item.isSuccess){ 
                $scope.hasValidIcons = true;
            }else if(item.isError){ 
                $scope.hasInValidIcons = true;
            }
        }
    }
    
    $scope.iconuploader = new FileUploader({
        url:  "/advertiser/creatives/icon/upload/"+creativeInfo.accountGuid
    });  
    
    $scope.iconuploader.filters.push({
        name: 'imageFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    });
    
    $scope.$watch("iconuploader.queue.length", function(newValue, oldValue) {
        $scope.checkValidIcons();
    });

    // CALLBACKS

 
    $scope.iconuploader.onAfterAddingFile = function(fileItem) {
        fileItem.slotOptions = {};
        fileItem.slot="";
        fileItem.showOptions = true;
        fileItem.showError = false;
        console.info('onAfterAddingFile', fileItem);
        fileItem.upload();
    };
 
    $scope.iconuploader.onSuccessItem = function(fileItem, response, status, headers) {   
        fileItem.slotOptions = response.slotOptions;
        fileItem.slot=response.slotOptions[0];
        fileItem.showOptions = true;
        fileItem.showError = false; 
        fileItem.thumbUrl = response.thumbUrl;
        fileItem.bannerUrl = response.bannerUrl; 
        $scope.checkValidIcons();
    };
    
    $scope.iconuploader.onErrorItem = function(fileItem, response, status, headers) {
        fileItem.showOptions = false;
        fileItem.showError = true;
        fileItem.message = "Invalid Dimensions";
        $scope.checkValidIcons();
    };
 
//    $scope.iconuploader.onCompleteItem = function(fileItem, response, status, headers) {
//        //console.info('onCompleteItem', fileItem, response, status, headers);
//    };
//    $scope.iconuploader.onCompleteAll = function() {
//        //console.info('onCompleteAll');
//    };

    
    $scope.ok = function () {
        var saveIconUrl = '/advertiser/creatives/icon/save';  
        var nativeicons = [];
        var nativeicon = {};
        var item = null;
        
        var hasValidIcons = false;
        $('#preloader').removeClass("hidden");  
        for( i= 0 ; i<  $scope.iconuploader.queue.length; i++){
            item = $scope.iconuploader.queue[i];
            if(item.isSuccess){
                nativeicon = {};
                nativeicon.account_guid = creativeInfo.accountGuid; 
                nativeicon.icon_size = item.slot.value;
                var slotParts = item.slot.label.split("-");
                nativeicon.slotName = slotParts[0];
                nativeicon.slotDescription = slotParts[1];
                nativeicon.resource_uri = item.bannerUrl; 
                nativeicons.push(nativeicon);
            } 
        }
        
        if($scope.hasInValidIcons){
            alert("There are invalid icons in the queue. Those will be ignored. ")
        }
        if($scope.hasValidIcons){
            $http.post(saveIconUrl,{"nativeicons":nativeicons}).success( function(response){  
                $modalInstance.close(response.nativeicons);     
                $('#preloader').addClass("hidden");  
            }).error(function(response){
                alert("Encountered a Problem saving icons to database");
                $('#preloader').addClass("hidden");  
            });
        }
        
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
        return false;
    };

};

var screenshotController = function ($scope, $http,  $modalInstance, FileUploader, creativeInfo) {
    $scope.showFileSelect = false;
    $scope.creativeInfo = creativeInfo;     
    $scope.screenshotValidationUrl = "";  
    $scope.hasValidScreenshots = false; 
    $scope.hasInValidScreenshots = false;
    $scope.slotOptions = [];
    $scope.loadOptionsUrl = "/metadata/options/screenshot_slot_options ";
    
    $scope.loadSlotOptions = function(){
        $('#preloader').removeClass("hidden");  
        $http.get($scope.loadOptionsUrl).success( function(response){  
            for(  i= 0 ; i< response.length; i++){
                var parts = response[i].label.split(":");
                $scope.slotOptions.push({"name":parts[0], "description":parts[1]});
            }
            $('#preloader').addClass("hidden");  
        }).error(function(response){
             
            $('#preloader').addClass("hidden");  
        });
    }
    
    $scope.loadSlotOptions();
    
    $scope.checkValidScreenshots = function(){
        $scope.hasValidScreenshots = false; 
        $scope.hasInValidScreenshots = false;
        for( i= 0 ; i<  $scope.screenshotuploader.queue.length; i++){
            item = $scope.screenshotuploader.queue[i];
            if(item.isSuccess){ 
                $scope.hasValidScreenshots = true;
            }else if(item.isError){ 
                $scope.hasInValidScreenshots = true;
            }
        }
    }
    
    $scope.screenshotuploader = new FileUploader({
        url:  "/advertiser/creatives/screenshot/upload/"+creativeInfo.accountGuid
    });  
    
    $scope.screenshotuploader.filters.push({
        name: 'imageFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    });
    
    $scope.$watch("screenshotuploader.queue.length", function(newValue, oldValue) {
        $scope.checkValidScreenshots();
    });

    // CALLBACKS

 
    $scope.screenshotuploader.onAfterAddingFile = function(fileItem) {
        fileItem.slotOptions = {};
        fileItem.slot="";
        fileItem.showOptions = true;
        fileItem.showError = false;
        console.info('onAfterAddingFile', fileItem);
        fileItem.upload();
    };
 
    $scope.screenshotuploader.onSuccessItem = function(fileItem, response, status, headers) {   
        fileItem.slotOptions = response.slotOptions;
        fileItem.slot=response.slotOptions[0];
        fileItem.showOptions = true;
        fileItem.showError = false; 
        fileItem.thumbUrl = response.thumbUrl;
        fileItem.bannerUrl = response.bannerUrl; 
        $scope.checkValidScreenshots();
    };
    
    $scope.screenshotuploader.onErrorItem = function(fileItem, response, status, headers) {
        fileItem.showOptions = false;
        fileItem.showError = true;
        fileItem.message = "Invalid Dimensions";
        $scope.checkValidScreenshots();
    };
 
//    $scope.screenshotuploader.onCompleteItem = function(fileItem, response, status, headers) {
//        //console.info('onCompleteItem', fileItem, response, status, headers);
//    };
//    $scope.screenshotuploader.onCompleteAll = function() {
//        //console.info('onCompleteAll');
//    };

    
    $scope.ok = function () {
        var saveScreenshotUrl = '/advertiser/creatives/screenshot/save';  
        var nativescreenshots = [];
        var nativescreenshot = {};
        var item = null;
        
        var hasValidScreenshots = false;
        $('#preloader').removeClass("hidden");  
        for( i= 0 ; i<  $scope.screenshotuploader.queue.length; i++){
            item = $scope.screenshotuploader.queue[i];
            if(item.isSuccess){
                nativescreenshot = {};
                nativescreenshot.account_guid = creativeInfo.accountGuid; 
                nativescreenshot.ss_size = item.slot.value;
                var slotParts = item.slot.label.split("-");
                nativescreenshot.slotName = slotParts[0];
                nativescreenshot.slotDescription = slotParts[1];
                nativescreenshot.resource_uri = item.bannerUrl; 
                nativescreenshots.push(nativescreenshot);
            } 
        }
        
        if($scope.hasInValidScreenshots){
            alert("There are invalid screenshots in the queue. Those will be ignored. ")
        }
        if($scope.hasValidScreenshots){
            $http.post(saveScreenshotUrl,{"nativescreenshots":nativescreenshots}).success( function(response){  
                $modalInstance.close(response.nativescreenshots);     
                $('#preloader').addClass("hidden");  
            }).error(function(response){
                alert("Encountered a Problem saving screenshots to database");
                $('#preloader').addClass("hidden");  
            });
        }
        
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
        return false;
    };

};