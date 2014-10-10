angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.task = $routeParams.taskId ? TaskService.$get($routeParams.taskId) : TaskService.$new({project:$routeParams.projectId});
		
		$scope.$on("$destroy",function(){
			$scope.task.$save();
		});
		
	});