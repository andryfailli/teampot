'use strict';

angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngMaterial'
])
.controller('mainController',['$rootScope','$scope','$materialSidenav',function($rootScope,$scope,$materialSidenav){

	$scope.toggleSidebar = function(){
		$materialSidenav('left').toggle();
	}
	$scope.closeSidebar = function(){
		$materialSidenav('left').close();
	}
	$scope.openSidebar = function(){
		$materialSidenav('left').open();
	}

}])