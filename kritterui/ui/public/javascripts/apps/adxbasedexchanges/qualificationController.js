/** Controllers */
angular.module('app.ui.form.ctrls', [ 'angularFileUpload' ]).
    controller('qualificationController',function ($scope, $http, $upload) {	
    $scope.qurl="#";
	$scope.init= function(){
            $scope.handleFileChange();
        }
        $scope.handleFileChange = function(){
        $scope['qurl_preview'] = false;
        var filePath =  $("input[name='qurl']").attr("value");
        if($scope['qurl']!=""){
                    $scope['qurl_preview'] = true;
                    $scope['qurl_preview_url'] = "/download?file="+encodeURI(filePath);
                    var fileName = filePath.substring(filePath.lastIndexOf('/')+1);
                    $scope['qurl_preview_label'] = fileName;
        }else{
            $scope['qurl_preview'] = false;
        }
        }
        
		$scope.handleQualificationUpload = function($files, geoTargetingType){
        for (var i = 0; i < $files.length; i++) {
            var file = $files[i];
            $scope.upload = $upload.upload({
                url: '/qualificationimage-upload ',
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
                $("#md5").attr("value",data.md5);
            }).error(function(data, status, headers, config) {
                $scope[geoTargetingType+"_preview"] = false;
                $scope[geoTargetingType+"_message"] = data.message;
            })
        }
    }
        
});
