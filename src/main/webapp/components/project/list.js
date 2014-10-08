angular.module('teampot').
	controller('projectListController', function($rootScope,$scope,GapiClient,ProjectService) {
		
		$scope.entityList = ProjectService.$list();
		
	});