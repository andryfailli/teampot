angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngAria',
	'ngSanitize',
	'ngMaterial',
	'routeBreadcrumbs',
	'routeActive',
	'iframeOnload',
	'ngGapi',
	'angular.filter',
	'monospaced.elastic',
	'fnDebounce'
])
.config(function($routeProvider,GapiProvider,GapiClientProvider,GapiPickerProvider) {

	$routeProvider
		.when('/dashboard', {
			id: 'dashboard',
			templateUrl: '/components/dashboard/dashboard.html',
			controller: 'dashboardController',
			sidebarTemplateUrl: '/components/sidebar/hello.html'
		})
		.when('/projects', {
			id: 'project-list',
			label: 'Projects',
			templateUrl: '/components/project/list.html',
			controller: 'projectListController',
			fabTemplateUrl: '/components/project/list-fab.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId', {
			id:'project',
			parent:'project-list',
			label:'{{currentProject.name}}',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			redirectTo : '/project/:projectId/tasks'
		})
		.when('/project/:projectId/tasks', {
			id: 'task-list',
			parent: 'project',
			label: 'Tasks',
			templateUrl: '/components/task/list.html',
			controller: 'taskListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			fabTemplateUrl: '/components/task/list-fab.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/task/:taskId?', {
			id: 'task-edit',
			parent: 'task-list',
			label: '{{task.id ? "edit task" : "new task"}}',
			templateUrl: '/components/task/edit.html',
			controller: 'taskEditController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/files', {
			id: 'file-list',
			parent: 'project',
			label: 'Files',
			templateUrl: '/components/file/list.html',
			controller: 'fileListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			fabTemplateUrl: '/components/file/list-fab.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/discussions', {
			id: 'discussions',
			parent: 'project',
			label: 'Discussions',
			templateUrl: '/components/discussion/discussions.html',
			controller: 'discussionsController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/meetings', {
			id: 'meeting-list',
			parent: 'project',
			label: 'Meetings',
			templateUrl: '/components/meeting/list.html',
			controller: 'meetingListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			fabTemplateUrl: '/components/meeting/list-fab.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/meeting/:meetingId?', {
			id: 'meeting-edit',
			parent: 'meeting-list',
			label: '{{meeting.id ? "edit meeting" : "new meeting"}}',
			templateUrl: '/components/meeting/edit.html',
			controller: 'meetingEditController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/members', {
			id: 'member-list',
			parent: 'project',
			label: 'Members',
			templateUrl: '/components/user/list.html',
			controller: 'userListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			fabTemplateUrl: '/components/user/list-fab.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.when('/project/:projectId/activity', {
			id: 'activity',
			parent: 'project',
			label: 'Activity',
			templateUrl: '/components/activity/list.html',
			controller: 'activityListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve:{
				userInfo: function($rootScope){return $rootScope.userInfo.$promise}
			}
		})
		.otherwise({
			redirectTo : '/projects'
		});
		
	GapiProvider.setClientId("60968053297-0eo4eum22rhp7iskadtj7aa0cfavovns.apps.googleusercontent.com");
	GapiProvider.setScope([
       "https://www.googleapis.com/auth/userinfo.email",
       "https://www.googleapis.com/auth/userinfo.profile",
       "https://www.googleapis.com/auth/plus.me",
       "https://www.googleapis.com/auth/drive",
       "https://www.googleapis.com/auth/admin.directory.group",
       "https://www.googleapis.com/auth/admin.directory.user.readonly"
    ]);
	GapiProvider.setAccessType('offline');
	
	GapiProvider.authorize(true);
	
	GapiPickerProvider.setDeveloperKey("AIzaSyAMYynuP3VI_Wj_LuUN8_rJq3zFpS9jGHg");
	
	GapiProvider.load("picker");
	
	GapiClientProvider.load("teampot","v1","//"+window.location.host+"/_ah/api");
	GapiClientProvider.load("plus","v1");
	GapiClientProvider.load("drive","v2");

})
.run(function($rootScope,$routeParams,Gapi,GapiClient,ProjectService,CONSTANTS,AlertService){

	$rootScope.$on("$routeChangeStart", function(event, next, current) {
		$rootScope.appLoading = true;
		//$rootScope.closeSidebar();
    });
	$rootScope.$on("$routeChangeSuccess", function(event, next, current) {
		$rootScope.appLoading = false;
		$rootScope.sidebarTemplateUrl = next.sidebarTemplateUrl;
		$rootScope.fabTemplateUrl = next.fabTemplateUrl;
		$rootScope.routeId = next.id;
		
		if ($routeParams.projectId) {
			if (!$rootScope.currentProject || $rootScope.currentProject.id != $routeParams.projectId) {
				$rootScope.currentProject = ProjectService.$get($routeParams.projectId)
			}
		} else {
			$rootScope.currentProject = null;
		}
		
    });
    $rootScope.$on("$routeChangeError", function(event, next, current) {
    	$rootScope.appLoading = false;
    	current ? history.back() : $location.path("/");		
		alert("Unable to access this page");
		//TODO migliorare messaggio
    });
    
    $rootScope.signIn = function(){
    	Gapi.authorize().then(function(data){
    		GapiClient.client("teampot").exec("user.auth",{code:data.code});
    	});
    }
    
    $rootScope.userInfo = GapiClient.client("plus").exec("people.get",{userId:'me'});
    $rootScope.userInfo.$promise.then(function(){
    	if ($rootScope.userInfo.domain != CONSTANTS.APPS_DOMAIN) {
    		delete $rootScope.userInfo;
    		AlertService.alert("Sorry, TeamPot is not available in your domain.","TeamPot");
    	}
    })
    
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