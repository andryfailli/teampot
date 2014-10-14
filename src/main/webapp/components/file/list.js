angular.module('teampot').
	controller('fileListController', function($scope,$routeParams,$location,$sce,ProjectService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.project.$promise.then(function(){
			$scope.$apply(function(){
				$scope.iframeSrc = $sce.trustAsResourceUrl("https://drive.google.com/embeddedfolderview?id="+$scope.project.folder+"#list");
			})
		})
		
		$scope.iframeLoaded = function(){
			$scope.iframeready = true;
		}
		
	});