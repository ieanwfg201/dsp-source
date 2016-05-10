var TableModel =  {	
		  "enableMultiSelect":false,
		  "dataCache":[],  
		  "filteredData":[],
		  	"selections":[],
			"currentPage": 0, 
			"itemsPerPage":500,
			"totalItems":5000,
			"enableFilter":false,
			"enableSorting": false,
			"globalFilter": false,
			"orderBy":"",
			"orderByReverse":false,
			"filterByFields":{},
			"dataUrl":"",
			"cachedItemsPage":0,
			"cachedPageSize":500,
			"cacheSize":5000,
			"filterData":{"pageNumber":this.cachedItemsPage, "pageSize":this.cachedPageSize},
		 
		
		init: function( dataUrl ){
			this.dataUrl = dataUrl; 
		},
		
		setFilterAttribute : function(pname, pvalue){
				this.filterData[pname] = pvalue;
		}, 
		
		_applyFilters: function(){
			if(_.isEmpty(this.filterByFields)){
				var startIndex = this.currentPage*this.itemsPerPage;
				var endIndex = Math.min((this.currentPage+1)*this.itemsPerPage, this.dataCache.length);
				this.filteredData = this.dataCache.slice(startIndex, endIndex);
			}
				
			else{
				 this.filteredItemCount = 0;
				 this.filteredData =  _.filter(this.dataCache, function(item){ 
					 if(this.filteredItemCount == this.itemsPerPage)
						 return false;
					 	var filtered = true;
						for (var property in this.filterByFields ) {  
					    	var val = item[property];
					    	var filterVal = this.filterByFields[property];
					    	if(typeof(filterVal) != "undefined" && ( typeof(val) == "undefined" ||  (val.toLowerCase()).indexOf(filterVal.toLowerCase())<0))
					    			filtered =  false; 
						}  
						if(filtered)
							 this.filteredItemCount++;
						return filtered;
					 }, this);
			}
			
		},  
		
		updateData:function(currentPage, pageItems, filterBy, filterByFields, orderBy, orderByReverse){
			if(this.currentPage != currentPage || this.orderBy != orderBy || this.pageItems != pageItems){
				this.pageItems = pageItems;
				this.currentPage = currentPage;
				if( (this.dataCache.length/this.pageItems -1)<currentPage && this.dataCache.length != this.totalItems){
					if(typeof(this.filterData.pageNumber) == "undefined")
						this.filterData.pageNumber = 0;
					else
						this.filterData.pageNumber = this.filterData.pageNumber + 1;
					if(typeof(this.filterData.pageSize) == "undefined")
						this.filterData.pageSize = this.cachedPageSize; 
					this.loadData();
				}  
			} 
			this.filterByFields = filterByFields;
			this._applyFilters(); 
		},
		
		isItemChecked:function(item){ 
			if( this.selections.indexOf(item)>=0){
				return true;
			}else{
				return false;
			}
		},
		
		onSelectionChange:function(item){
			if(item.isSelected)
				this.selections.push(item);
			else{ 
				this.selections.splice(this.selections.indexOf(item),1);
			}
				
		},
		loadData:function(){ 
				$('#preloader').removeClass("hidden");  
				$.ajax({
					url: this.dataUrl,
					type: "POST",
					context:this,
				    data: JSON.stringify(this.filterData),
				    contentType: "application/json; charset=utf-8",
			        dataType: "json",
				    async : false,
				    success: function(data) {
				    	if(this.enableMultiSelect && typeof(data.list) != "undefined" && data.list.length >0 ){
				    		_.each(data.list, function(item){ 
				    			item.isSelected = false;
				    		}, this);
				    	}
				    	this.dataCache = this.dataCache.concat(data.list);
				    	this._applyFilters();
				    	this.totalItems = data.size;
						this.reloadOptions = false; 
						$('#preloader').addClass("hidden");
				    },
				    error: function(){
				    	$('#preloader').addClass("hidden"); 
				    }
				});  
		},  
		
		
 
		reset:function(parentValue){
			if(this.parentValue != parentValue ){
				this.parentValue = parentValue 
				this.reloadOptions = true;
				this.isDependent = true;
			}			
		}, 
		
		onOpen:function(){ 
			this.loadOptions();
		}, 
		
		 
		
		 
}
