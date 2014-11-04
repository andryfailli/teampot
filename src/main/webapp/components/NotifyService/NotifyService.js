angular.module("teampot").
	factory('NotifyService',function($materialToast) {
		
		var duration = 2000;
		var position = "bottom left";
		
		var service = {
			info: function(message,undoFn){
				$materialToast.show({
					controller: function($scope){
						$scope.message = message;
						if (angular.isFunction(undoFn)) {
							$scope.hasUndo = true;
							$scope.undo = function(){
								$materialToast.hide();
								undoFn();
							}
						}
					},
				    templateUrl: '/components/NotifyService/info-toast.html',
					duration: duration,
					position: position
			    });
			},
			error: function(message,retryFn){
				$materialToast.show({
					controller: function($scope){
						$scope.message = message;
						if (angular.isFunction(retryFn)) {
							$scope.hasRetry = true;
							$scope.retry = function(){
								$materialToast.hide();
								retryFn();
							}
						}
					},
				    templateUrl: '/components/NotifyService/error-toast.html',
					duration: duration,
					position: position
			    });
			}
		};
		
		return service;
	});