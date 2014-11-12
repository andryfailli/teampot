angular.module('teampot').
	controller('userListFabController', function($rootScope,$scope,$materialDialog,$route,ProjectService) {
				
		$scope.fabAdd = function(evt){
			$materialDialog.show({
				templateUrl: '/components/user/add-modal.html',
				controller: 'userAddModalController',
				targetEvent: evt,
			});
		}

	});