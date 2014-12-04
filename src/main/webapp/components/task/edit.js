angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,TaskService,UserService,$timeout,NotifyService) {
		
		var isNew = $routeParams.taskId ? false : true;
		
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
			$scope.task.$save().$promise
				.then(function(){
					NotifyService.info("Task saved");
				})
				.catch(function(){
					NotifyService.error("An error occurred while saving this task",$scope.save);
				});
		}
		
		$scope.saveDebounced = debounce($scope.save, 2000, false);
		
		
		$scope.lookupUsers = function(q){
			return q ? UserService.$list({q:q}) : UserService.$list();
		}
		
		
		
	});