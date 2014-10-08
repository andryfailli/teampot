angular.module('teampot').
	controller('projectListController', function($rootScope,$scope,GapiClient) {
		
		teampotClient = GapiClient.client("teampot");
		
		$scope.entityList = teampotClient.exec("project.list");
		
	});