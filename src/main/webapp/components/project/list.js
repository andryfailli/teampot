angular.module('teampot').
	controller('projectListController', function($rootScope,$scope,GapiClient,ProjectService,RealtimeService) {
		
		$scope.entityList = ProjectService.$list();
		
		$scope.entityList = ProjectService.$list();
		var watchHandle = RealtimeService.registerWatch("Project",function(){return ProjectService.$list();});
		RealtimeService.subscribe("Project",$scope.entityList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
	});