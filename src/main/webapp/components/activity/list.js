angular.module('teampot').
	controller('activityListController', function($scope,$routeParams,ProjectService,ActivityService) {
		
		$scope.activityList = ActivityService.$list($routeParams.projectId);
		
	});