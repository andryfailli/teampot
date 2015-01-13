angular.module('teampot').
	controller('projectListFabController', function($rootScope,$scope,$mdDialog,$location,ProjectService,PreloadService) {
				
		var modalTemplateUrl = '/components/project/edit-modal.html'
		
		$scope.fabAdd = function(evt){
			$mdDialog.show({
				templateUrl: modalTemplateUrl,
				controller: 'projectEditModalController',
				targetEvent: evt,
				locals: {
					targetProject: ProjectService.$new()
				}
			}).then(function(result) {
				$location.path("/project/"+result.id);
			});
		}
		
		// preload modal template
		PreloadService.preload(modalTemplateUrl);

	});