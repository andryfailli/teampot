angular.module('teampot').
	controller('taskEditController', function($scope,$routeParams,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectKey);
		$scope.task = $routeParams.taskKey ? TaskService.$get($routeParams.taskKey) : TaskService.$new();
		
	});