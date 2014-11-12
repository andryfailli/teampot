angular.module('teampot').
	controller('userAddModalController', function($rootScope,$scope,$materialDialog,$routeParams,GapiClient,NotifyService,$route) {
		
		$scope.projectId = $routeParams.projectId;

		$scope.add = function(){
			
			var newUser = GapiClient.client("teampot").exec("project.addMember",{
				id: $routeParams.projectId,
				memberEmail: $scope.userEmail
			});
			
			NotifyService.info("Adding user "+$scope.userEmail+" ...");
			$materialDialog.hide(newUser);
			
			newUser.$promise
				.then(function(){
					$rootScope.$apply(function(){
						NotifyService.info("User "+$scope.userEmail+" added");
						//TODO: enhancement: append the item to the list.
		            	$route.reload();
					})
				})
				.catch(function(){
					$rootScope.$apply(function(){
						NotifyService.error("An error occurred while adding "+$scope.userEmail,$scope.add);
					})
				});
				
		}

	});