var DataSourceTree = function (options) {
    this._data = options.data;
    this._delay = options.delay;
}
DataSourceTree.prototype.data = function (options, callback) {
    var self = this;
    var $data = null;

    if (!("name" in options) && !("type" in options)) {
        $data = this._data;//the root tree
        callback({data: $data});
        return;
    }
    else if ("type" in options && options.type == "folder") {
        if ("additionalParameters" in options && "children" in options.additionalParameters)
            $data = options.additionalParameters.children;
        else $data = {}//no data
    }

    if ($data != null)//this setTimeout is only for mimicking some random delay
        setTimeout(function () {
            callback({data: $data});
        }, parseInt(Math.random() * 500) + 200);

    //we have used static data here
    //but you can retrieve your data dynamically from a server using ajax call
    //checkout examples/treeview.html and examples/treeview.js for more info
};
var MultiSelectModel = {
    options: [],
    selections: [],
    async: false,
    reloadOptions: true,
    scope: null,
    value: "",
    initialized: false,
    optionsUrl: "",
    parentValue: "",
    valueMode: 'valueArray',//'objectWithVersion','valueArray', 'commaSeparatedList'
    isNumeric: true,//false
    showNone: false,
    visible: true,

    dependents: null,

    init: function (optionsUrl, valueMode, isNumeric, showNone) {
        this.optionsUrl = optionsUrl;

        this.valueMode = valueMode;
        this.isNumeric = isNumeric;
        this.dependents = [];
        if (showNone === undefined)
            this.showNone = false;
        else
            this.showNone = showNone;
        this._initOptions();
    },

    setUrl: function (optionsUrl) {
        this.optionsUrl = optionsUrl;
        this.reloadOptions = true;
        this.options = [];
        this.selections = [];
    },

    _initOptions: function () {
        if (this.showNone)
            this.options = [{"label": "All", "value": "all"}, {"label": "None", "value": "none", "selected": true}];
        else
            this.options = [{"label": "All", "value": "all", "selected": true}];
    },

    setOptions: function (options) {
        if (options != null) {
            this.options = angular.copy(options);
            if (this.showNone)
                this.options.unshift({"label": "None", "value": "none", "selected": false});
            this.options.unshift({"label": "All", "value": "all", "selected": false});
            this.reloadOptions = false;
        } else {
            this._initOptions();
        }
    },

    onItemClick: function (item) {
        if (item.value == "all") {
            for (var i = 1; i < this.options.length; i++) {
                this.options[i].selected = false;
            }
        } else if (item.value == "none") {
            this.options[0].selected = false;
            for (var i = 2; i < this.options.length; i++) {
                this.options[i].selected = false;
            }
        } else {
            if (this.selections.length > 1 && (this.options[0].selected || this.options[1].selected)) {

                this.options[0].selected = false;
                if (this.showNone)
                    this.options[1].selected = false;
            }
        }
    },

    addDependent: function (key, value) {
        if (this.dependents == null)
            this.dependents = [];
        this.dependents.push(value);
    },

    setInitialValue: function (selections) {
        if (selections == null || typeof(selections) == "undefined") {

        } else {
            if (selections.length == 1) {
                if (selections[0] == "none") {
                    this.options[1].selected = true;
                    this.options[0].selected = false;
                }
                else if (selections[0] == "all") {
                    this.options[0].selected = true;
                    this.options[1].selected = false;
                }
            }
            this.value = JSON.stringify(selections);
            if (this.value == "[]" || this.value == "{}") {
                this.updateDependents("all");
            } else {
                if ($.isArray(selections))
                    this.updateDependents(selections.join());
                else
                    this.updateDependents(this._objectToIdArray(selections));
            }


            if (this.value != "[]" && this.value != "{}" && this.value != "" && this.value != "null"
                && this.value != "[\"none\"]" && this.value != "[\"all\"]") {
                this.loadOptions();
            }
        }
    },

    _objectToIdArray: function (obj) {
        var tmp = [];
        for (var prop in obj) {
            tmp.push(prop);
        }
        return tmp.join();
    },

    _populateValuesFromArray: function () {
        if (this.value == "[]") {
            this.options[0].selected = true;
            return;
        }

        try {
            var items = JSON.parse(this.value);
        }
        catch (err) {

        }
        var isNumeric = this.isNumeric;

        this.options.forEach(function (entry) {
            if (entry.value != "" && entry.value != "all" && entry.value != "none") {
                if (isNumeric) {
                    if ($.inArray(parseInt(entry.value), items) >= 0) {
                        entry.selected = true;
                    }
                } else {
                    if ($.inArray(entry.value, items) >= 0) {
                        entry.selected = true;
                    }
                }
            }
        });
    },

    _populateValuesFromObject: function () {
        if (this.value == "{}") {
            this.options[0].selected = true;
            return;
        }
        var items = JSON.parse(this.value);
        var isNumeric = this.isNumeric;

        this.options.forEach(function (entry) {
            if (entry.value in items) {
                var obj = items[entry.value];
                var tmp = obj.split("-");
                if (tmp.length == 2) {
                    entry.min = tmp[0];
                    entry.max = tmp[1];
                }
                entry.selected = true;

            } else {
                entry.min = "all";
                entry.max = "all";
            }
        });
    },

    _populateValuesFromCSL: function () {
        if (this.value == "all") {
            this.options[0].selected = true;
            return;
        } else if (this.value == "none") {
            this.options[1].selected = true;
            return;
        }
        var items = this.value.split(",");
        var isNumeric = this.isNumeric;

        this.options.forEach(function (entry) {
            if (entry.value != "" && entry.value != "all" && entry.value != "none") {
                if (isNumeric) {
                    if ($.inArray(parseInt(entry.value), items) >= 0) {
                        entry.selected = true;
                    }
                } else {
                    if ($.inArray(entry.value, items) >= 0) {
                        entry.selected = true;
                    }
                }
            }
        });
    },

    _populateSelectedValues: function () {
        if (this.valueMode == 'valueArray') {
            this._populateValuesFromArray();
        } else if (this.valueMode == 'objectWithVersion') {
            this._populateValuesFromObject();
        } else if (this.valueMode == 'commaSeparatedList') {
            this._populateValuesFromCSL();
        }

    },

    loadOptions: function () {
        if (this.reloadOptions) {
            var url = this.optionsUrl;
            if (this.isDependent && this.parentValue != "none") {
                url += this.parentValue;
            } else if (this.isDependent && this.parentValue == "none") {

            }
            $('#preloader').removeClass("hidden");
            $.ajax({
                url: url,
                context: this,
                async: this.async,
                success: function (data) {
                    this.setOptions(data);
                    this._populateSelectedValues(data);
                    this.reloadOptions = false;
                    $('#preloader').addClass("hidden");
                },
                error: function () {
                    $('#preloader').addClass("hidden");
                }
            });

        }
    },

    audienceLoadOptions: function () {
        var selected_radio = $('#selected_source').val();
        //   $('#Fruit').each(function () {
        // if($('#Fruit2').val()==selected_radio)
        //     $('#Fruit2').attr("checked","checked");
        switch (selected_radio) {
            case "010":
                $('#Fruit1').attr("checked", "checked");
                break;
            case "011":
                $('#Fruit2').attr("checked", "checked");
                break;
            case "012":
                $('#Fruit3').attr("checked", "checked");
                break;
            case "018":
                $('#Fruit4').attr("checked", "checked");
                break;
            case "019":
                $('#Fruit5').attr("checked", "checked");
                break;
        }
        //      })
        if (this.reloadOptions) {
            var url = this.optionsUrl;
            if (this.isDependent && this.parentValue != "none") {
                url += this.parentValue;
            } else if (this.isDependent && this.parentValue == "none") {

            }
            $('#preloader').removeClass("hidden");
            $.ajax({
                url: url,
                context: this,
                async: this.async,
                success: function (data) {
                    this.setOptions(data);
                    this._populateSelectedValues(data);
                    if ($('#selected_children').val() != "") {
                        var selectedStr = $('#selected_children').val().replace(/\[/gm, '').replace(/\]/gm, '').replace(/\"/gm, '');
                        var selectedArr = selectedStr.split(',');
                    }
                    // 上面的数据，通逗号分割成数组arr1，
                    // 找到呗选中的source
                    // 遍历被选中的source的所有子tag，如果子tag的code在arr1中，设置其additionalParameters中的item-selected字段设为true。

                    var jsonStr = data[0].label
                    jsonStr = jsonStr.replace(": null", "")
                    var jsonData = jsonStr = eval("(" + jsonStr + ")")
                    for (var source in jsonData) {
                        if (selected_radio == jsonData[source].sourceId) {
                            for (var parent in jsonData[source].parentMap) {
                                var parentObj = jsonData[source].parentMap[parent];
                                var childrens = parentObj.additionalParameters.children;
                                for (var children_name in childrens) {
                                    for (var selected_code in selectedArr) {
                                        console.log(selected_code);
                                        if (selectedArr[selected_code] == childrens[children_name].code) {
                                            $(parentObj.additionalParameters).attr("item-selected", true);
                                            $(childrens[children_name].additionalParameters).attr("item-selected", true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    var madhouse = jsonData[0].parentMap
                    var treeDataSource1 = new DataSourceTree({data: madhouse});
                    var qianxun = jsonData[1].parentMap
                    var treeDataSource2 = new DataSourceTree({data: qianxun});

                    var talkingData = jsonData[2].parentMap
                    var treeDataSource3 = new DataSourceTree({data: talkingData});

                    var unionPay = jsonData[3].parentMap
                    var treeDataSource4 = new DataSourceTree({data: unionPay});

                    var tree_data = jsonData[4].parentMap
                    var treeDataSource5 = new DataSourceTree({data: tree_data});

                    this.reloadOptions = false;
                    $('#preloader').addClass("hidden");


                    $('#Madhouse').ace_tree({
                        dataSource: treeDataSource1,
                        multiSelect: true,
                        loadingHTML: '<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
                        'open-icon': 'icon-minus',
                        'close-icon': 'icon-plus',
                        'selectable': true,
                        'selected-icon': 'icon-ok',
                        'unselected-icon': 'icon-remove'
                    });

                    $('#QianXun').ace_tree({
                        dataSource: treeDataSource2,
                        multiSelect: true,
                        loadingHTML: '<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
                        'open-icon': 'icon-minus',
                        'close-icon': 'icon-plus',
                        'selectable': true,
                        'selected-icon': 'icon-ok',
                        'unselected-icon': 'icon-remove'
                    });
                    $('#TalkingData').ace_tree({
                        dataSource: treeDataSource3,
                        multiSelect: true,
                        loadingHTML: '<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
                        'open-icon': 'icon-minus',
                        'close-icon': 'icon-plus',
                        'selectable': true,
                        'selected-icon': 'icon-ok',
                        'unselected-icon': 'icon-remove'
                    });
                    $('#UnionPay').ace_tree({
                        dataSource: treeDataSource4,
                        multiSelect: true,
                        loadingHTML: '<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
                        'open-icon': 'icon-minus',
                        'close-icon': 'icon-plus',
                        'selectable': true,
                        'selected-icon': 'icon-ok',
                        'unselected-icon': 'icon-remove'
                    });
                    $('#Admaster').ace_tree({
                        dataSource: treeDataSource5,
                        multiSelect: true,
                        loadingHTML: '<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
                        'open-icon': 'icon-minus',
                        'close-icon': 'icon-plus',
                        'selectable': true,
                        'selected-icon': 'icon-ok',
                        'unselected-icon': 'icon-remove'
                    });

                },

                error: function () {
                    $('#preloader').addClass("hidden");
                }
            });

        }
    },

    reset: function (parentValue) {
        if (parentValue == "none")
            this.visible = false;
        else
            this.visible = true;
        if (this.parentValue != parentValue) {
            this.parentValue = parentValue
            this.reloadOptions = true;
            this.isDependent = true;
        }
    },

    onOpen: function () {
        this.loadOptions();
    },
    audienceOnOpen: function () {
        this.audienceLoadOptions();
    },

    valueCSV: function () {
        var selectionsArray = [];
        var isNumeric = this.isNumeric;

        if (this.selections.length == 1 && (this.selections[0].value == 'all' || this.selections[0].value == 'none')) {
            if (this.selections[0].value == 'all')
                return "all";
            else if (this.selections[0].value == 'none')
                return "none";
        }

        this.selections.forEach(function (entry) {
            if (isNumeric)
                selectionsArray.push(parseInt(entry.value));
            else
                selectionsArray.push(entry.value);
        });
        return selectionsArray.join();
    },

    updateDependents: function (value) {
        if (this.dependents === undefined || this.dependents == null) {

        } else {
            for (var i = 0; i < this.dependents.length; i++) {
                if (value === undefined)
                    this.dependents[i].reset(this.valueCSV());
                else
                    this.dependents[i].reset(value);
            }
        }
    },
    checkvalid: function (obj) {
        if (obj.value != "all" && obj.value != "none")
            return true;
        return false;
    },

    onClose: function () {
        var selectedValues = [];
        if (this.valueMode == "objectWithVersion")
            selectedValues = {};
        var valueMode = this.valueMode;
        var isNumeric = this.isNumeric;
        var showNone = this.showNone;
        if (this.selections.length == 1 && (this.selections[0].value == 'all' || this.selections[0].value == 'none')) {
            if (valueMode == "objectWithVersion") {
                if (this.selections[0].value == 'all') {
                    this.value = "{}";
                } else
                    this.value = "";
            } else if (valueMode == "valueArray") {
                if (this.selections[0].value == 'all') {
                    if (showNone)
                        this.value = "[\"all\"]";
                    else
                        this.value = "[]";
                } else {
                    if (showNone)
                        this.value = "[\"none\"]";
                    else
                        this.value = "";
                }
            } else {
                if (this.selections[0].value == 'all') {
                    this.value = "all";
                } else
                    this.value = "none";
            }
        } else {
            this.selections.forEach(function (entry) {
                if (valueMode == "objectWithVersion") {
                    selectedValues[entry.value + ""] = entry.min + "-" + entry.max;
                } else {
                    if (isNumeric)
                        selectedValues.push(parseInt(entry.value));
                    else
                        selectedValues.push(entry.value);
                }
            });
            if (this.valueMode == "objectWithVersion" || this.valueMode == "valueArray") {
                this.value = JSON.stringify(selectedValues);
            } else {
                this.value = selectedValues.join();
            }
        }
        this.updateDependents();
    }
}

