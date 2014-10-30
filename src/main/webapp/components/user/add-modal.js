angular.module('teampot').
	controller('userAddModalController', function($rootScope,$scope,$materialDialog,$routeParams,GapiClient) {
		
		$scope.projectId = $routeParams.projectId;

		$scope.add = function(){
			
			GapiClient.client("teampot").exec("project.addUser",{userEmail:$scope.userEmail});
			
			$rootScope.$apply(function(){
				$materialDialog.hide($scope.project);
			})
				
		}

	});