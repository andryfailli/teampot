angular.module('teampot').
	controller('projectListFabController', function($rootScope,$scope,$materialDialog,$location,ProjectService) {
				
		$scope.fabAdd = function(evt){
			$materialDialog.show({
				templateUrl: '/components/user/add-modal.html',
				controller: 'userAddModalController',
				targetEvent: evt,
			}).then(function(result) {
				$location.path("/project/"+result.id);
			});
		}

	});