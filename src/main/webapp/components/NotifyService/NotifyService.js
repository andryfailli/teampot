angular.module("teampot").
	factory('NotifyService',function($mdToast) {
		
		var duration = 2000;
		var position = "bottom left";
		
		var service = {
			info: function(message,undoFn){
				$mdToast.show({
					controller: function($scope){
						$scope.message = message;
						if (angular.isFunction(undoFn)) {
							$scope.hasUndo = true;
							$scope.undo = function(){
								$mdToast.hide();
								undoFn();
							}
						}
					},
				    templateUrl: '/components/NotifyService/info-toast.html',
				    hideDelay: duration,
					position: position
			    });
			},
			error: function(message,retryFn){
				$mdToast.show({
					controller: function($scope){
						$scope.message = message;
						if (angular.isFunction(retryFn)) {
							$scope.hasRetry = true;
							$scope.retry = function(){
								$mdToast.hide();
								retryFn();
							}
						}
					},
				    templateUrl: '/components/NotifyService/error-toast.html',
				    hideDelay: duration*5,
					position: position
			    });
			}
		};
		
		return service;
	});