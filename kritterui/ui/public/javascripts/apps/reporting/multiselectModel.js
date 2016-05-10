var MultiSelectModel =  {		
		options:[],
		selections:[],
		reloadOptions:true,
		http: null,
		scope: null,
		value:"",
		initialized: false,
		optionsUrl:"",
		parentValue:"",
		
		dependents: null,
		
		init: function( $http, optionsUrl){
			this.optionsUrl = optionsUrl;
			this.http = $http; 
			this.options =  [{"label":"All", "value":"all"}, {"label":"None", "value":"none"}];
		},
		
		setOptions: function(options){
			if(options != null){
				this.options = options;
				this.options.unshift({"label":"None", "value":"none"});
				this.options.unshift({"label":"All", "value":"all"});
				this.reloadOptions = false;
			}else{
				this.options =  [{"label":"All", "value":"all"}, {"label":"None", "value":"none"}];
			}
		},
		
		onItemClick :function(item){
			if(item.value=="all"){
				for( var i = 1; i < this.options.length; i++){
					this.options[i].selected = false;
				}
			}else if(item.value=="none"){
				this.options[0].selected = false;
				for( var i = 2; i < this.options.length; i++){
					this.options[i].selected = false;
				}
			}else{
				if(this.selections.length > 1 && (this.options[0].selected || this.options[1].selected)){
					this.options[0].selected = false;
					this.options[1].selected = false;
				}
			}
		},
		
		addDependent: function(key, value){
			if(this.dependents == null)
				this.dependents = [];
			this.dependents.push(value);
		}, 
		
		setInitialValue:function(selections){
			this.value = selections;
			var items = JSON.parse(selections);
			var anyOption = {"label" : "All", "value": "all", "selected":true};
			if($.isArray(items)){
				if(options){
					options.unshift(anyOption); 
					if(items.length == 0){
						anyOption.selected = true;
					}else{
						options.forEach(function(entry){
							if(entry.value !=""){
								if($.inArray(parseInt(entry.value),items)>=0){ 
									entry.selected = true;
								}
							}else{
								entry.selected = true;
							}       	        		
						});
					}					
				}

			}
		}, 
		
		loadOptions:function(){
			if(this.reloadOptions){
				var url = this.optionsUrl;
				if(this.isDependent){
					url += this.parentValue;
				}
				  
				$.ajax({
					url: url,
				    context: this,
				    success: function(data) {
				    	this.setOptions(data); 
						this.reloadOptions = false;
				    }
				}); 
			}
		},  
 
		reset:function(parentValue){
			if(this.parentValue != parentValue ){
				this.parentValue = parentValue 
				this.reloadOptions = true;
				this.isDependent = true;
			}
			
		}, 
		
		onOpen:function(){
			console.log("open");
			this.loadOptions();
		}, 
		
		onClose:function(){
			console.log("close");
			var selectedValues = [];
			this.selections.forEach(function(entry) { 
				selectedValues.push(entry.value);
			});
			this.value= selectedValues.join();
			
			for (var i=0; i< this.dependents.length; i++) {
				this.dependents[i].reset(this.value);
			}
			
		}		
}

 
 