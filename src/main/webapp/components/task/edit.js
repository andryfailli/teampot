angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,ProjectService,TaskService) {
		
		$scope.project = ProjectService.$get($routeParams.projectKey);
		$scope.task = $routeParams.taskKey ? TaskService.$get($routeParams.taskKey) : TaskService.$new({project:$routeParams.projectKey});
		
		$scope.$on("$destroy",function(){
			$scope.task.$save();
		});
		
	});