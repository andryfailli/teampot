angular.module('teampot').
	controller('activityListController', function($scope,$routeParams,ProjectService,ActivityService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
		$scope.activityList = ActivityService.$list($routeParams.projectId);
		
	});