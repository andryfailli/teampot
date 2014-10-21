angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,TaskService,$timeout) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.task = $routeParams.taskId ? TaskService.$get($routeParams.taskId) : TaskService.$new({project:$routeParams.projectId});
		
		$scope.task.$promise.then(function(){
			$scope.$watch("task",function(newTask,oldTask){
				if (!angular.equals(newTask,oldTask)) {
					$scope.saveDebounced();
				}
			},true)
		})
		
		$scope.save = function() {
			$scope.task.$save();
		}
		
		$scope.saveDebounced = debounce($scope.save, 2000, false);
		
	});