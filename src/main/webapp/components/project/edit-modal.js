angular.module('teampot').
	controller('projectEditModalController', function($rootScope,$scope,$materialDialog) {
		
		$scope.save = function(){
			$materialDialog.hide($scope.project);
		}

	});