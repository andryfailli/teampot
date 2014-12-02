angular.module('teampot').
	controller('userListFabController', function($rootScope,$scope,$mdDialog,$route,ProjectService) {
				
		$scope.fabAdd = function(evt){
			$mdDialog.show({
				templateUrl: '/components/user/add-modal.html',
				controller: 'userAddModalController',
				targetEvent: evt,
			});
		}

	});