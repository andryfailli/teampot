angular.module('teampot').
	controller('projectListController', function($rootScope,$scope,$location,$timeout,GapiClient,ProjectService,TaskService,RealtimeService) {
		
		$scope.entityList = ProjectService.$list();
		var watchHandle = RealtimeService.registerWatch("Project",function(){return ProjectService.$list();});
		RealtimeService.subscribe("Project",$scope.entityList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
		$scope.taskList = TaskService.$listToDoForUser($rootScope.currentUser.id);
		var watchHandle_tasks = RealtimeService.registerWatch("Task",function(){return TaskService.$listToDoForUser($rootScope.currentUser.id);});
		RealtimeService.subscribe("Task",$scope.taskList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle_tasks);});
		
		$scope.goto = function(entity){
			$location.path("/project/"+entity.id);
		}
		
		$scope.gotoTask = function(project,task){
			$location.path("/project/"+project.id+"/task/"+task.id);
		}
		
		$scope.saveTaskAsync = function(task){
			$timeout(function(){
				task.$save().$promise.then(function(){
					$scope.$apply(function(){
						var index = $scope.taskList.items.indexOf(task);
						if (index>=0) $scope.taskList.items.splice(index,1);
					});
				});
			},250);
		}
		
	});