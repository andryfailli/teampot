angular.module("teampot").
	factory('AlertService',function($mdDialog) {
		
		var service = {
			alert: function(message,title,evt){
				
				return $mdDialog.show(
					$mdDialog.alert()
					    .title(title)
					    .content(message)
					    .ariaLabel(title)
					    .ok("OK")
					    .targetEvent(evt)
				);
				
			},
		};
		
		return service;
	});