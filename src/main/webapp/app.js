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

}])