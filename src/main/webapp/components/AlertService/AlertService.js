angular.module("teampot").
	factory('AlertService',function($materialDialog) {
		
		var service = {
			alert: function(message,title){
				$materialDialog.show({
					templateUrl: '/components/AlertService/alert-modal.html',
					controller: function($scope,$materialDialog){
						$scope.title = title;
						$scope.message = message;
						
						$scope.close = function(){$materialDialog.hide();}
					}
				})
			},
		};
		
		return service;
	});