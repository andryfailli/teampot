angular.module('teampot').
	controller('projectEditModalController', ['$rootScope','$scope','$materialDialog',function($rootScope,$scope,$materialDialog) {
		
		$scope.hide = function(){
			$materialDialog.hide();
		}

	}]);