angular.module('app.ui.form.ctrls',  [  'trNgGrid' ] ).
controller('retargetingSegmentListController',function ($scope, $http, $modal) {
    $scope.status= "";
    $scope.accountGuid = "";

    $scope.init = function(){
        var path = $.url().attr('path');
        var pathComps = path.split("/");
        $scope.accountGuid = pathComps[2];
        $scope.retargetingSegmentListData =  Object.create(TableModel);
        $scope.retargetingSegmentListData.init("/entitylist" );
        $scope.retargetingSegmentListData.enableMultiSelect = true;
        $scope.retargetingSegmentListData.setFilterAttribute("entityType", "retargeting_segment" ); 
        $scope.retargetingSegmentListData.setFilterAttribute("accountGuid", $scope.accountGuid );     }

    $scope.init();

});

