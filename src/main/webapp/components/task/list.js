angular.module('teampot').
	controller('taskListController', function($scope,$routeParams,$location,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.taskList = TaskService.$list($routeParams.projectId);
		
		$scope.goto = function(task){
			$location.path("/project/"+$routeParams.projectId+"/task/"+task.id);
		}
		
	});