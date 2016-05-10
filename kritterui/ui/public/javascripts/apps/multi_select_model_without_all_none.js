  var MultiSelectModel =  {		
		options:[],
		selections:[],
		async:false,
		reloadOptions:true, 
		scope: null,
		value:"",
		initialized: false,
		optionsUrl:"",
		parentValue:"",
		valueMode:'valueArray',//'objectWithVersion','valueArray', 'commaSeparatedList'
		isNumeric: true,//false
		showNone:false,
		visible:true,
		
		dependents: null,
		
		init: function( optionsUrl, valueMode, isNumeric, showNone){
			this.optionsUrl = optionsUrl; 
			
			this.valueMode = valueMode;
			this.isNumeric = isNumeric;
			this.dependents = [];
			if(showNone === undefined)
				this.showNone = false;
			else
				this.showNone = showNone;
			this._initOptions();
		},
		
		setUrl: function(optionsUrl){
			this.optionsUrl = optionsUrl; 
			this.reloadOptions = true;
			this.options=[];
			this.selections=[];
		},
		
		_initOptions : function(){
//				this.options =  [{"label":"All", "value":"all", "selected" : true}];
		}, 
		
		setOptions: function(options){
			if(options != null){
				this.options  = angular.copy(options);
//				this.options.unshift({"label":"All", "value":"all", "selected":false});
				this.reloadOptions = false;
			}else{
				this._initOptions();
			}
		},
		
		onItemClick :function(item){
		},
		
		addDependent: function(key, value){
			if(this.dependents == null)
				this.dependents = [];
			this.dependents.push(value);
		}, 
		
		setInitialValue:function(selections){
			if(selections== null || typeof(selections) == "undefined"){
				
			}else{
				if(selections.length == 1){
					 if(selections[0] =="none"){
						 this.options[1].selected = true;
					 	 this.options[0].selected = false;
					 }  
					 else  if(selections[0] =="all"){
						 this.options[0].selected = true;
						 this.options[1].selected = false;
					 }
				}
				this.value = JSON.stringify(selections);
				if(this.value == "[]" || this.value == "{}"){
					this.updateDependents("all");
				}else{
					if($.isArray(selections))
						this.updateDependents(selections.join());
					else
						this.updateDependents(this._objectToIdArray(selections));
				} 
				 
				
				if(this.value != "[]" && this.value != "{}" && this.value != ""  && this.value != "null" 
										&& this.value != "[\"none\"]" && this.value != "[\"all\"]" ){
					this.loadOptions();
				}	
			}
		}, 
		
		_objectToIdArray: function(obj){
			var tmp = [];
			for (var prop in obj) {
				tmp.push(prop);
			}
			return tmp.join();
		},
		
		_populateValuesFromArray:function(){			
			if(this.value == "[]" ){
				this.options[0].selected = true;
				return;
			}
			var items =  JSON.parse(this.value);  
			var isNumeric = this.isNumeric;
			
			this.options.forEach(function(entry){
				if(entry.value !="" && entry.value != "all" && entry.value != "none"){
					if(isNumeric){
						if($.inArray(parseInt(entry.value),items)>=0){ 
							entry.selected = true; 
						}
					}else{
						if($.inArray( entry.value ,items)>=0){ 
							entry.selected = true; 
						}
					} 
				}      	        		
			}); 
		}, 
		
		_populateValuesFromObject:function(){			
			if(this.value == "{}" ){
				this.options[0].selected = true;
				return;
			}
			var items =  JSON.parse(this.value);  
			var isNumeric = this.isNumeric;
			
			this.options.forEach(function(entry){
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
		}, 
		
		_populateValuesFromCSL:function(){
			if(this.value == "all" ){
				this.options[0].selected = true;
				return;
			}else if(this.value == "none" ){
				this.options[1].selected = true;
				return;
			}
			var items =  this.value.split(",");  
			var isNumeric = this.isNumeric;
			
			this.options.forEach(function(entry){
				if(entry.value !="" && entry.value != "all" && entry.value != "none"){
					if(isNumeric){
						if($.inArray(parseInt(entry.value),items)>=0){ 
							entry.selected = true; 
						}
					}else{
						if($.inArray( entry.value ,items)>=0){ 
							entry.selected = true; 
						}
					} 
				}      	        		
			}); 
		}, 
		
		_populateSelectedValues:function(){			 
			if(this.valueMode == 'valueArray'){
				this._populateValuesFromArray();
			}else if(this.valueMode == 'objectWithVersion'){
				this._populateValuesFromObject();
			}else if(this.valueMode == 'commaSeparatedList'){
				this._populateValuesFromCSL();
			}
			
		},
		
		loadOptions:function(){
			if(this.reloadOptions){
				var url = this.optionsUrl;
				if(this.isDependent && this.parentValue != "none"){
					url += this.parentValue;
				}else if(this.isDependent && this.parentValue == "none") {
					
				}
				$('#preloader').removeClass("hidden");  
				$.ajax({
					url: url,
				    context: this,
				    async : this.async,
				    success: function(data) {
				    	this.setOptions(data); 
				    	this._populateSelectedValues(data);
						this.reloadOptions = false; 
						$('#preloader').addClass("hidden");
				    },
				    error: function(){
				    	$('#preloader').addClass("hidden"); 
				    }
				}); 
				
			}
		},  
 
		reset:function(parentValue){
			if(parentValue == "none")
				this.visible = false;
			else
				this.visible = true;
			if(this.parentValue != parentValue ){
				this.parentValue = parentValue 
				this.reloadOptions = true;
				this.isDependent = true;
			}			
		}, 
		
		onOpen:function(){ 
			this.loadOptions();
		}, 
		
		valueCSV:function(){
			var selectionsArray = []; 
			var isNumeric = this.isNumeric;
			
			if(this.selections.length == 1 && (this.selections[0].value == 'all' || this.selections[0].value == 'none')){	 
					if(this.selections[0].value == 'all' )
						return "all";
					else if(this.selections[0].value == 'none' )
						return "none";					 				 
			}
			
			this.selections.forEach(function(entry) {  
				if( isNumeric)
					selectionsArray.push( parseInt(entry.value));
				else
					selectionsArray.push( entry.value );
			});
			return selectionsArray.join();
		},
		
		updateDependents:function(value){
			if(this.dependents === undefined || this.dependents == null){
				
			}else{
				for (var i=0; i< this.dependents.length; i++) {
					if(value === undefined)
						this.dependents[i].reset(this.valueCSV());
					else
						this.dependents[i].reset(value);
				}
			}	
		}, 
		checkvalid:function(obj){
			if(obj.value != "all" && obj.value != "none" )
				return true;
			return false;
		},
		
		onClose:function(){ 
			var selectedValues = []; 
			if( this.valueMode == "objectWithVersion")
				selectedValues = {};
			var valueMode = this.valueMode;
			var isNumeric = this.isNumeric;
			var showNone = this.showNone;
			if(this.selections.length == 1 && (this.selections[0].value == 'all' || this.selections[0].value == 'none')){
				if( valueMode == "objectWithVersion"){
					if(this.selections[0].value == 'all' ){
						this.value = "{}";
					}else
						this.value = "";
				}else if( valueMode == "valueArray"){
					if(this.selections[0].value == 'all' ){
							this.value = "[]";
					}else{
							this.value = "";
					} 
				} else{
					if(this.selections[0].value == 'all' ){
						this.value = "all";
					}else
						this.value = "none";
				}
			}else {
				this.selections.forEach(function(entry) { 				
					if( valueMode == "objectWithVersion"){
						selectedValues[entry.value+""]=entry.min+"-"+entry.max;
					}else{
						if( isNumeric)
							selectedValues.push( parseInt(entry.value));
						else
							selectedValues.push( entry.value );
					} 
				});
				if(this.valueMode == "objectWithVersion" || this.valueMode == "valueArray"){
					this.value= JSON.stringify(selectedValues);
				} else{
					this.value= selectedValues.join();
				} 
			}		
			this.updateDependents();		
		}		
}