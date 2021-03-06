angular.module('teampot').
	controller('userAddModalController', function($rootScope,$scope,$mdDialog,$routeParams,GapiClient,NotifyService,$route,UserService,RealtimeService) {
		
		$scope.projectId = $routeParams.projectId;
		
		$scope.lookupUsers = function(q){
			return UserService.$list({q:q});
		}

		$scope.add = function(){
			
			var newUser = GapiClient.client("teampot").exec("project.addMember",{
				id: $routeParams.projectId,
				memberEmail: $scope.user.email
			});
			
			NotifyService.info("Adding user "+$scope.user.email+" ...");
			$mdDialog.hide(newUser);
			
			newUser.$promise
				.then(function(){
					$rootScope.$apply(function(){
						RealtimeService.notify("User",$scope.user);
						NotifyService.info("User "+$scope.user.email+" added");
		            	$route.reload();
					})
				})
				.catch(function(){
					$rootScope.$apply(function(){
						NotifyService.error("An error occurred while adding "+$scope.user.email);
					})
				});
				
		}

	});