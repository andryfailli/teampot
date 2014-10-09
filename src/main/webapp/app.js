angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngAria',
	'ngMaterial',
	'routeBreadcrumbs',
	'routeActive',
	'ngGapiClient',
	'angular.filter'
])
.config(function($routeProvider,GapiClientProvider) {

	$routeProvider
		.when('/dashboard', {
			id: 'dashboard',
			templateUrl: '/components/dashboard/dashboard.html',
			controller: 'dashboardController',
			sidebarTemplateUrl: '/components/sidebar/hello.html'
		})
		.when('/projects', {
			id: 'projects',
			label: 'Projects',
			templateUrl: '/components/project/list.html',
			controller: 'projectListController',
			sidebarTemplateUrl: '/components/sidebar/hello.html',
			fabTemplateUrl: '/components/project/list-fab.html',
		})
		.when('/project/:projectKey', {
			id: 'project',
			parent: 'projects',
			label: '{{project.name}}',
			templateUrl: '/components/project/view.html',
			controller: 'projectViewController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
		})
		.when('/project/:projectKey/tasks', {
			id: 'tasks',
			parent: 'project',
			label: 'Tasks',
			templateUrl: '/components/task/list.html',
			controller: 'taskListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			fabTemplateUrl: '/components/task/list-fab.html',
		})
		.otherwise({
			redirectTo : '/projects'
		});
		
	GapiClientProvider.setClientId("138057900615-7ei54320nap7588tr5g5t3tsf43d7otb.apps.googleusercontent.com");
	GapiClientProvider.setScope("https://www.googleapis.com/auth/userinfo.email");
	GapiClientProvider.load("teampot","v1","//"+window.location.host+"/_ah/api");

})
.run(function($rootScope){

	$rootScope.$on("$routeChangeStart", function(event, next, current) {
		$rootScope.appLoading = true;
		$rootScope.closeSidebar();
    });
	$rootScope.$on("$routeChangeSuccess", function(event, next, current) {
		$rootScope.appLoading = false;
		$rootScope.sidebarTemplateUrl = next.sidebarTemplateUrl;
		$rootScope.fabTemplateUrl = next.fabTemplateUrl;
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
	
	$rootScope.toggleSidebar = function(){
		$materialSidenav('left').toggle();
	}
	$rootScope.closeSidebar = function(){
		$materialSidenav('left').close();
	}
	$rootScope.openSidebar = function(){
		$materialSidenav('left').open();
	}

})