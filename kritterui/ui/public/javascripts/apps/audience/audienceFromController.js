/** Controllers */
angular.module('app.ui.form.ctrls', ['angularFileUpload', 'multi-select']).controller('afController', function ($scope, $http, $upload) {

    $scope.name = $("#name").val();
    $scope.name_msmodel = Object.create(MultiSelectModel);
    $scope.name_msmodel.init("", 'valueArray', true);
    $scope.name_msmodel.setUrl("/metadata/options/audience_tags_list");
    // $scope.name_msmodel.audienceOnOpen();
    $scope.name_msmodel.audienceOnOpen();

    $scope.name_msmodel.onClose();

});

function tree_selected() {



    var ids = [];
    var items = [];
    // if(document.getElementById('name').value.length==0){
    //     alert('输入为空！');
    //     document.getElementById('name').focus();
    //     return false;
    // }
 mdItems = $('#Madhouse').tree('selectedItems');
    if (mdItems.length != 0) {
        items.push(mdItems)
    }
    var qxItems = $('#QianXun').tree('selectedItems');
    if (qxItems.length != 0) {
        items.push(qxItems)
    }
    var tdItems = $('#TalkingData').tree('selectedItems');
    if (tdItems.length != 0) {
        items.push(tdItems)
    }
    var unItems = $('#UnionPay').tree('selectedItems');
    if (unItems.length != 0) {
        items.push(unItems)
    }
    var adItems = $('#Admaster').tree('selectedItems');
    if (adItems.length != 0) {
        items.push(adItems)
    }
    if (items.length > 1) {
        alert("Sorry,Cannot select multiple Sourse at the same time！！")
        return false
    }else if(items.length ==1){
        var GroupBy = _.groupBy(items[0], 'pCode');
        for (var gb in GroupBy) {
            var codea = GroupBy[gb].map(function (i) {
                return i.code
            })
            ids.push(codea)
        }
        $('#selected_tag_codes').val(JSON.stringify(ids));
        if(document.getElementById('name').value.length!=0){
            alert("Add Success！")
            }

    }else{
        alert("Sorry,Source cannnot be null")
        return false
    }
}