angular.module('teampot').
	controller('projectListController', ['$rootScope','$scope','$materialDialog',function($rootScope,$scope,$materialDialog) {
		
		//TODO: implement backend call
		$scope.entities = [
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 1"
			},
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 2"
			},
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 3"
			},
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 4"
			},
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 5"
			},
			{
				key: "IbnoluiVliuBKBHJTYfvYKJBILH",
				name: "My test project 6"
			}
		];
		
	}]);