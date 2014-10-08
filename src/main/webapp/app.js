angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngAria',
	'ngMaterial',
	'routeBreadcrumbs',
	'routeActive',
	'ngGapiClient'
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
			reloadOnSearch: false
		})
		.when('/project/:projectKey', {
			id: 'project',
			parent: 'projects',
			label: 'My Test Project',
			templateUrl: '/components/project/view.html',
			controller: 'projectViewController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
		})
		.otherwise({
			redirectTo : '/projects'
		});
		
	GapiClientProvider.setClientId("906530262076-dojpmhig3e47pk4jf6u84t8e1g56dmbj.apps.googleusercontent.com");
	GapiClientProvider.setScope("https://www.googleapis.com/auth/userinfo.email");
	GapiClientProvider.load("teampot","v1","//"+window.location.host+"/_ah/api");

})
.run(function($rootScope){

	$rootScope.$on("$routeChangeStart", function(event, next, current) {
		$rootScope.appLoading = true;
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