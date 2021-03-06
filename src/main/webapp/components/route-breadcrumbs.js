angular.module('routeBreadcrumbs',[]).
	factory('routeBreadcrumbs', ['$rootScope','$route','$routeParams','$interpolate','$interval',function($rootScope,$route,$routeParams,$interpolate,$interval){

		var service = {}
		
		var breadcrumbs = [];
		var routeParams = {};
		

		function update() {
			
			storeCurrentRouteParams();
			
			// clear breadcrumbs
			breadcrumbs.splice(0,breadcrumbs.length);

			
			if (!$route.current || !$route.current.$$route || !$route.current.$$route.id) return;
			
			var route = getRoute($route.current.$$route.id);
			
			while (route) {
				
				var routePath = route.originalPath;
				
				// replace route params in route path
				for (var i=0; i<route.keys.length;i++) {
					var paramInfo = route.keys[i];
					var paramValue = routeParams[route.id] ? routeParams[route.id][paramInfo.name] : "";
					routePath = routePath.replace( ":" + paramInfo.name + (paramInfo.optional?"?":"") , paramValue)
				}
				
				// add breadcrumb
				breadcrumbs.unshift({
					id: route.id,
					path: routePath,
					label: compileLabel(route.label ? route.label : route.id)
				})
				
				if (route.parent)
					route = getRoute(route.parent);
				else
					route = null;
			}
			
		}
		
		function compileLabel(label) {
			if ($route.current && $route.current.locals && $route.current.locals.$scope)
				return $interpolate(label)($route.current.locals.$scope);
			else
				return label;
		}
		

		function storeCurrentRouteParams() {
			if (!$route.current || !$route.current.$$route || !$route.current.$$route.id) return;
			var routeId = $route.current.$$route.id

			if (!routeParams[routeId]) routeParams[routeId]={};
			for (var key in $routeParams) {
				routeParams[routeId][key]=$routeParams[key];
			}
		}
		

		function getRoute(id) {
			for (var key in $route.routes)
				if ($route.routes[key].id === id) return $route.routes[key];
		}

		

		$rootScope.$on("$viewContentLoaded",function(){	
			// compile breadcrumb labels
			for (var i=0; i<breadcrumbs.length; i++) {
				var interpolateFn = $interpolate(breadcrumbs[i].label);
				$route.current.locals.$scope.$watch(interpolateFn,function(){
					update();
				});
			}
		});
		
		$rootScope.$on("$routeChangeSuccess",function(){			
			update();
		});
		
		// update now
		update();
	
		service.breadcrumbs = breadcrumbs;
		service.routeParams = function(routeId,params){
			if (!routeParams[routeId]) routeParams[routeId]={};
			angular.extend(routeParams[routeId],params);
			
			update();
		}
		
		return service;
		
	}]);