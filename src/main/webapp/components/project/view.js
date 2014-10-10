angular.module('teampot').
	controller('projectViewController', function($scope,$routeParams,ProjectService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
	});