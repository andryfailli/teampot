angular.module('teampot').
	controller('userListFabController', function($rootScope,$scope,$mdDialog,$route,ProjectService,PreloadService) {
		
		var modalTemplateUrl = '/components/user/add-modal.html';
		
		$scope.fabAdd = function(evt){
			$mdDialog.show({
				templateUrl: modalTemplateUrl,
				controller: 'userAddModalController',
				targetEvent: evt,
			});
		}
		
		// preload modal template
		PreloadService.preload(modalTemplateUrl);

	});