angular.module('teampot').
	controller('discussionsController', function($scope,$routeParams,$location,$sce,ProjectService,CONSTANTS) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.project.$promise.then(function(){
			$scope.$apply(function(){
				//TODO: choose right domain!
				$scope.iframeSrc = $sce.trustAsResourceUrl("https://groups.google.com/forum/embed/?place=forum/"+$scope.project.machineName+"&domain="+CONSTANTS.APPS_DOMAIN+"&parenturl="+encodeURIComponent(window.location.href));
			})
		})
		
		$scope.iframeLoaded = function(){
			$scope.iframeready = true;
		}
		
	});