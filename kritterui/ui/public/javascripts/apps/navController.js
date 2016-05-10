/** Controllers */
angular.module('app.directives', [])
.directive('collapsenav2', [function(){
	function link(scope, ele, attrs){
		$toplinks = ele.find('a');
		
        $lists = ele.find('ul').parent('li');  
        $lists.append('<i class="fa fa-caret-right icon-has-ul"></i>');
        $a = $lists.children('a');
        $listsRest = ele.children('li').not($lists);
        $aRest = $listsRest.children('a');

        app = $('#app');
        
        var currentpath  = $.url();
        var path = $.url().attr('path')
        var query = $.url().attr('query');
        if(query != ""){
        	path = path + "?" + query;
        }
		var pathComps = path.split("/");
		 

		angular.forEach($toplinks, function(value, key) {  
			var url = $(value).attr("href"); 
			if(url == path){
				$parent = $(value).parent('li').addClass("active");
				$parent = $parent.parent('ul');
				$parent = $parent.parent('li');
	            $lists.not( $parent ).removeClass('open').find('ul').slideUp();
	            $parent.toggleClass('open').find('ul').slideToggle(); 
			}
		}); 
		 
        $a.on('click', function(event) { 
            if ( app.hasClass('nav-min') )
            	return false;

            $this = $(this);
            $parent = $this.parent('li');
            $lists.not( $parent ).removeClass('open').find('ul').slideUp();
            $parent.toggleClass('open').find('ul').slideToggle();

            event.preventDefault();
        });

        $aRest.on('click', function(event){
            $lists.removeClass('open').find('ul').slideUp()
        });
 
        scope.$on('minNav:enabled', function(event){
        	 $lists.removeClass('open').find('ul').slideUp();
        });
    } 
	
	return {
	    restrict: 'A',
	    link: link
	};
}]);
