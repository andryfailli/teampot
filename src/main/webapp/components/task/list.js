angular.module('teampot').
	controller('taskListController', function($scope,$routeParams,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectKey);
		$scope.taskList = TaskService.$list();
		
	});