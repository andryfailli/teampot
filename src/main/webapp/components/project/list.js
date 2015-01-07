angular.module('teampot').
	controller('projectListController', function($rootScope,$scope,$location,GapiClient,ProjectService,TaskService,RealtimeService) {
		
		$scope.entityList = ProjectService.$list();
		var watchHandle = RealtimeService.registerWatch("Project",function(){return ProjectService.$list();});
		RealtimeService.subscribe("Project",$scope.entityList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
		$scope.taskList = TaskService.$listForUser($rootScope.currentUser.id);
		var watchHandle_tasks = RealtimeService.registerWatch("Task",function(){return TaskService.$listForUser($rootScope.currentUser.id);});
		RealtimeService.subscribe("Task",$scope.taskList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle_tasks);});
		
		$scope.goto = function(entity){
			$location.path("/project/"+entity.id);
		}
		
		$scope.gotoTask = function(project,task){
			$location.path("/project/"+project.id+"/task/"+task.id);
		}
		
	});