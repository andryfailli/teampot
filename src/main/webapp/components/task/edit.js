angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,TaskService,$timeout) {
		
		var isNew = $routeParams.taskId ? false : true;
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.task = !isNew ? TaskService.$get($routeParams.taskId) : TaskService.$new({project:$routeParams.projectId});
		
		function registerSaveWatch() {
			$scope.$watch("task",function(newTask,oldTask){
				if (!angular.equals(newTask,oldTask)) {
					$scope.saveDebounced();
				}
			},true)
		}
		
		if (isNew)
			registerSaveWatch();
		else
			$scope.task.$promise.then(registerSaveWatch);
		
		$scope.save = function() {
			$scope.task.$save();
		}
		
		$scope.saveDebounced = debounce($scope.save, 2000, false);
		
	});