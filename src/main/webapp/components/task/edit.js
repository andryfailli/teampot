angular.module('teampot').
	controller('taskEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,TaskService,UserService,$timeout,NotifyService) {
		
		var isNew = $routeParams.taskId ? false : true;
		
		$scope.task = !isNew ? TaskService.$get($routeParams.taskId) : TaskService.$new({project:$routeParams.projectId});
		
		$scope.save = function() {
			$scope.task.$save().$promise
				.then(function(){
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