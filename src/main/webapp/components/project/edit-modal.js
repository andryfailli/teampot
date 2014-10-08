angular.module('teampot').
	controller('projectEditModalController', function($rootScope,$scope,$materialDialog,targetProject) {
		
		$scope.project = targetProject;
		
		$scope.save = function(){
			
			$scope.project.$save().$promise.then(function(){
				$rootScope.$apply(function(){
					$materialDialog.hide($scope.project);
				})
				
				
			});			
			
		}

	});