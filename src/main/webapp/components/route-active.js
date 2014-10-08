angular.module('routeActive',[]).
directive('routeActive',['$rootScope','$route',function($rootScope,$route){
	return {
		restrict: 'A',
		link: function(scope, element, attr) {


			function getRoute(id) {
				for (var key in $route.routes)
					if ($route.routes[key].id === id) return $route.routes[key];
			}


			function check() {

				var routeActiveAttr = scope.$eval(attr.routeActive);
				if (!routeActiveAttr) return;

				var isActive;
				var isEnabled;

				if (typeof routeActiveAttr === "string") {

					isActive = checkActive(routeActiveAttr);

				} else if (angular.isArray(routeActiveAttr)){

					for (var i = 0; i < routeActiveAttr.length; i++) {
						isActive = isActive || checkActive(routeActiveAttr[i]);
					};

				} else {
					throw "routeActive Error: invalid attribute";
				}


				isActive ? 	element.addClass("active") : element.removeClass("active");
				

			}
			
			function checkActive(routeActive) {
				if ($route.current.$$route.id && routeActive && $route.current.$$route.id === routeActive)
					return true;
				else
					return false;
			}
			
			// event listeners
			var unregFunctions = [];
			unregFunctions.push($rootScope.$on("$routeChangeSuccess",function(){
				check();
			}));
			
			// check now
			check();

			// cleanup
			element.on('$destroy', function() {
		        for (var i = 0; i < unregFunctions.length; i++) {
		        	unregFunctions[i]();
		        };
		    });

		}
	}
}]);