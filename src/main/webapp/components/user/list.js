angular.module('teampot').
	controller('userListController', function($rootScope,$scope,GapiClient,$routeParams,ProjectService,UserService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		
		$scope.entityList = UserService.$list($routeParams.projectId);
		
		$scope.removeMember = function(user){
			
			var itemIndex = $scope.entityList.items.indexOf(user);
			$scope.entityList.items.splice(itemIndex,1);
			
			GapiClient.client("teampot").exec("project.removeMember",{
				id: $routeParams.projectId,
				memberEmail: user.email
			}).$promise
				.then(function(){
					$rootScope.$apply(function(){
						//TODO: show toast
					})
				})
				.catch(function(){
					$rootScope.$apply(function(){
						//TODO: show toast
						$scope.entityList.items.splice(itemIndex,0,user);
					});
				});
				
		}
		
	});