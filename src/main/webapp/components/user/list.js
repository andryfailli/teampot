angular.module('teampot').
	controller('userListController', function($rootScope,$scope,GapiClient,$routeParams,ProjectService,UserService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
		$scope.entityList = UserService.$list();
		
	});