angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngMaterial',
	'routeBreadcrumbs'
])
.config(function($routeProvider) {

	$routeProvider
		.when('/dashboard', {
			id: 'dashboard',
			templateUrl: '/components/dashboard/dashboard.html',
			sidebarTemplateUrl: '/components/sidebar/hello.html',
			controller: 'dashboardController',
			sidebar: false
		})
		.when('/project', {
			id: 'projects',
			label: 'Projects',
			templateUrl: '/components/project/list.html',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			controller: 'projectListController',
			sidebar: false
		})
		.when('/project/:projectKey', {
			id: 'project',
			parent: 'projects',
			label: 'My Test Project',
			templateUrl: '/components/project/view.html',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			controller: 'projectViewController',
			sidebar: true
		})
		.otherwise({
			redirectTo : '/projects'
		});

})
.run(function($rootScope){

	$rootScope.$on("$routeChangeStart", function(event, next, current) {
		$rootScope.appLoading = true;
    });
	$rootScope.$on("$routeChangeSuccess", function(event, next, current) {
		$rootScope.appLoading = false;
		$rootScope.sidebarTemplateUrl = next.sidebarTemplateUrl;
		$rootScope.routeId = next.id;
    });
    $rootScope.$on("$routeChangeError", function(event, next, current) {
    	$rootScope.appLoading = false;
    	current ? history.back() : $location.path("/");		
		alert("Unable to access this page");
		//TODO migliorare messaggio
    });
})
.controller('mainController',function($rootScope,$scope,$routeParams,$materialSidenav,routeBreadcrumbs){

	$scope.routeParams = $routeParams;

	$scope.breadcrumbs = routeBreadcrumbs.breadcrumbs;
	
	$scope.toggleSidebar = function(){
		$materialSidenav('left').toggle();
	}
	$scope.closeSidebar = function(){
		$materialSidenav('left').close();
	}
	$scope.openSidebar = function(){
		$materialSidenav('left').open();
	}

})