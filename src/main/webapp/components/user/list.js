angular.module('teampot').
	controller('userListController', function($rootScope,$scope,GapiClient,$routeParams,ProjectService,UserService,NotifyService,RealtimeService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
		$scope.entityList = UserService.$list({project: $routeParams.projectId});
		var watchHandle = RealtimeService.registerWatch("User",function(){return UserService.$list({project: $routeParams.projectId});});
		RealtimeService.subscribe("User",$scope.entityList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
		$scope.removeMember = function(user){
			
			var itemIndex = $scope.entityList.items.indexOf(user);
			$scope.entityList.items.splice(itemIndex,1);
			
			NotifyService.info("Removing user "+user.email+" ...");
			
			GapiClient.client("teampot").exec("project.removeMember",{
				id: $routeParams.projectId,
				memberEmail: user.email
			}).$promise
				.then(function(){
					$rootScope.$apply(function(){
						NotifyService.info("User "+user.email+" removed");
					})
				})
				.catch(function(){
					$rootScope.$apply(function(){
						NotifyService.error("An error occurred when removing user "+user.email,function(){
							$scope.removeMember(user);
						});
						$scope.entityList.items.splice(itemIndex,0,user);
					});
				});
				
		}
		
	});