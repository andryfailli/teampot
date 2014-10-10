angular.module('teampot').
	controller('taskListController', function($scope,$routeParams,$location,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectKey);
		$scope.taskList = TaskService.$list();
		
		$scope.goto = function(task){
			$location.path("/project/"+$routeParams.projectKey+"/task/"+task.key);
		}
		
	});