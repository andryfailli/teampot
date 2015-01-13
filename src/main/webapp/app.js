angular.module('teampot', [                      
	'ngRoute',
	'ngAnimate',
	'ngAria',
	'ngSanitize',
	'ngTouch',
	'ngMaterial',
	'routeBreadcrumbs',
	'routeActive',
	'iframeOnload',
	'ngGapi',
	'angular.filter',
	'monospaced.elastic',
	'fnDebounce',
	'material.components.typeahead',
	'material.components.datepicker'
])
.config(function($routeProvider,GapiProvider,GapiClientProvider,GapiPickerProvider) {

	var checkCurrentUserAuth = function($rootScope){return $rootScope.currentUser$promise};
	
	$routeProvider
		.when('/projects', {
			id: 'project-list',
			label: 'Projects',
			templateUrl: '/components/project/list.html',
			controller: 'projectListController',
			fabTemplateUrl: '/components/project/list-fab.html',
			resolve: {
				currentUser: checkCurrentUserAuth
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
			resolve: {
				currentUser: checkCurrentUserAuth
			}
		})
		.when('/project/:projectId/task/:taskId?', {
			id: 'task-edit',
			parent: 'task-list',
			label: '{{task.id ? "edit task" : "new task"}}',
			templateUrl: '/components/task/edit.html',
			controller: 'taskEditController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve: {
				currentUser: checkCurrentUserAuth
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
			resolve: {
				currentUser: checkCurrentUserAuth
			}
		})
		.when('/project/:projectId/discussions', {
			id: 'discussions',
			parent: 'project',
			label: 'Discussions',
			templateUrl: '/components/discussion/discussions.html',
			controller: 'discussionsController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve: {
				currentUser: checkCurrentUserAuth
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
			resolve: {
				currentUser: checkCurrentUserAuth
			}
		})
		.when('/project/:projectId/meeting/:meetingId?', {
			id: 'meeting-edit',
			parent: 'meeting-list',
			label: '{{meeting.id ? "edit meeting" : "new meeting"}}',
			templateUrl: '/components/meeting/edit.html',
			controller: 'meetingEditController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve: {
				currentUser: checkCurrentUserAuth
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
			resolve: {
				currentUser: checkCurrentUserAuth
			}
		})
		.when('/project/:projectId/activity', {
			id: 'activity',
			parent: 'project',
			label: 'Activity',
			templateUrl: '/components/activity/list.html',
			controller: 'activityListController',
			sidebarTemplateUrl: '/components/sidebar/menu.html',
			resolve: {
				currentUser: checkCurrentUserAuth
			}
		})
		.otherwise({
			redirectTo : '/projects'
		});
		
	GapiProvider.setClientId("138057900615-7ei54320nap7588tr5g5t3tsf43d7otb.apps.googleusercontent.com");
	GapiProvider.setScope([
       "https://www.googleapis.com/auth/userinfo.email",
       "https://www.googleapis.com/auth/userinfo.profile",
       "https://www.googleapis.com/auth/drive",
       "https://www.googleapis.com/auth/calendar",
       "https://www.googleapis.com/auth/admin.directory.user.readonly"
    ]);
	GapiProvider.setAccessType('offline');
	
	GapiPickerProvider.setDeveloperKey("AIzaSyC_VQ_C57vVOLMUxtMBr6bIwR2kNy8_H80");
	
	GapiProvider.load("picker");

	GapiClientProvider.load("teampot","v1","//"+window.location.host+"/_ah/api");
	GapiClientProvider.load("plus","v1");
	GapiClientProvider.load("drive","v2");

})
.run(function($rootScope,$routeParams,$location,$mdSidenav,Gapi,GapiClient,ProjectService,CONSTANTS,AlertService,$q,$interval){
	
	var currentUserDeferred = $q.defer();
	$rootScope.currentUser$promise = currentUserDeferred.promise;

	$rootScope.$on("$routeChangeStart", function(event, next, current) {
		$rootScope.appLoading = true;
		//$rootScope.closeSidebar();
    });
	$rootScope.$on("$routeChangeSuccess", function(event, next, current) {
		$rootScope.appLoading = false;
		$rootScope.viewError = false;
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
    	$rootScope.viewError = true;
    });
    
    
    $rootScope.onSwipeLeft = function($event){
    	$mdSidenav('left').isOpen() ? $rootScope.closeSidebar() : $rootScope.$emit("swipe-left");;
    }
    $rootScope.onSwipeRight = function($event){
    	//TODO: maybe a percentage value is better...
    	$event.changedTouches[0].screenX < 150 ? $rootScope.openSidebar() : $rootScope.$emit("swipe-right");
    }
    
    
    $rootScope.toggleSidebar = function(){
		$mdSidenav('left').toggle();
	}
	$rootScope.closeSidebar = function(){
		$mdSidenav('left').close();
	}
	$rootScope.openSidebar = function(){
		$mdSidenav('left').open();
	}
	
    
    $rootScope.signIn = function(silent){
    	$rootScope.signingIn = true;
    	Gapi.authorize(silent).then(function(data){
    		
    		if (!silent) {
    			$rootScope.currentUser = GapiClient.client("teampot").exec("user.auth",{code:data.code});
    		} else {
    			$rootScope.currentUser = GapiClient.client("teampot").exec("user.get",{id:"me"});
    		}
    		
    		$rootScope.currentUser.$promise
    			.then(function(data){
    				currentUserDeferred.resolve(data);
    				$rootScope.currentUser$promise.$resolved = true;
    				
    				$rootScope.signingIn = false;
    	    		$rootScope.signedIn = true;
    	    		
    	    		// Token expires in 3600s. Let's refresh it every 15m
    	    		$interval(function(){Gapi.authorize(true);},15*60);
    				
    			})
    			.catch(function(data){
    				currentUserDeferred.reject(data);
    				$rootScope.currentUser$promise.$resolved = false;
    				
    				$rootScope.signingIn = false;
    	    		$rootScope.signedIn = false;
    				
    			});
    		
    	},function(){
    		$rootScope.$apply(function(){
	    		$rootScope.signingIn = false;
	    		$rootScope.signedIn = false;
    		})
    	});
    }
    
    $rootScope.signIn(true);    
    
    $rootScope.currentUser$promise.catch(function(){
    	$location.path("/dashboard");
    	delete $rootScope.currentUser;
    	AlertService.alert("Sorry, TeamPot is not available for you (or your domain/organization).","TeamPot");
    })
    
})
.controller('mainController',function($rootScope,$scope,$routeParams,routeBreadcrumbs){

	$scope.routeParams = $routeParams;

	$scope.breadcrumbs = routeBreadcrumbs.breadcrumbs;

})