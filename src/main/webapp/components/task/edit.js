angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,TaskService,UserService,$timeout,NotifyService,RealtimeService) {
		
		var isNew = $routeParams.taskId ? false : true;
		
		$scope.task = !isNew ? TaskService.$get($routeParams.taskId) : TaskService.$new({project:$routeParams.projectId});
		
		$scope.task.$promise.then(function(){
			$scope.task_bkp = angular.copy($scope.task);
		})
		
		$scope.save = function() {
			if (angular.equals($scope.task_bkp,$scope.task)) return;
			
			$scope.task.$save().$promise
				.then(function(){
					RealtimeService.notify("Task",$scope.task);
					NotifyService.info("Task saved");
				})
				.catch(function(){
					NotifyService.error("An error occurred while saving this task",$scope.save);
				});
		}
		
		$scope.$on("$destroy",$scope.save);
		
		
		$scope.lookupUsers = function(q){
			return UserService.$list({q:q});
		}
		
		
		
	});