angular.module('teampot').
	controller('userAddModalController', function($rootScope,$scope,$materialDialog,$routeParams,GapiClient) {
		
		$scope.projectId = $routeParams.projectId;

		$scope.add = function(){
			
			var newUser = GapiClient.client("teampot").exec("project.addMember",{
				id: $routeParams.projectId,
				memberEmail: $scope.userEmail
			});
			
			newUser.$promise.then(function(){
				$rootScope.$apply(function(){
					$materialDialog.hide(newUser);
				})
			});
				
		}

	});