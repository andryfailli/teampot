angular.module('teampot').
	controller('projectViewController', function($scope,$routeParams,ProjectService) {
		
		$scope.entity = ProjectService.$get($routeParams.projectKey);
		
	});