angular.module('teampot').
	controller('activityListController', function($scope,$routeParams,ProjectService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
	});