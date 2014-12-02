angular.module('teampot').
	controller('projectEditModalController', function($rootScope,$scope,$mdDialog,targetProject) {
		
		$scope.project = targetProject;

		$scope.save = function(){
			$scope.loading = true;
			$scope.project.$save().$promise
				.then(function(){
					$rootScope.$apply(function(){
						$mdDialog.hide($scope.project);
					});
				}).catch(function(error){
					$rootScope.$apply(function(){
						$scope.loading = false;
						$scope.error = error;
					});
				});			
			
		}
		
		$scope.$watch("project.name",function(){
			delete $scope.error;
		});

	});