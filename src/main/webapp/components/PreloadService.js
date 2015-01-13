angular.module("teampot").
	factory('PreloadService',function($http,$templateCache) {
		
		var service = {
			preload: function(url) {
				$http.get(url, { cache: $templateCache });
			}
		};
		
		return service;
	});