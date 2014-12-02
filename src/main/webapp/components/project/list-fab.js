angular.module('teampot').
	controller('projectListFabController', function($rootScope,$scope,$mdDialog,$location,ProjectService) {
				
		$scope.fabAdd = function(evt){
			$mdDialog.show({
				templateUrl: '/components/project/edit-modal.html',
				controller: 'projectEditModalController',
				targetEvent: evt,
				locals: {
					targetProject: ProjectService.$new()
				}
			}).then(function(result) {
				$location.path("/project/"+result.id);
			});
		}

	});