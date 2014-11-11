angular.module('teampot').
	controller('fileListController', function($rootScope,$scope,$routeParams,$location,$sce,GapiClient) {
		
		$scope.filesLoading = true;
		
		$rootScope.currentProject.$promise.then(function(){
			
			// load iframe
			$scope.$apply(function(){
				$scope.iframeSrc = $sce.trustAsResourceUrl("https://drive.google.com/embeddedfolderview?id="+$rootScope.currentProject.folder+"#list");
			});
			
			// check if folder is empty
			GapiClient.client("drive").exec("children.list",{
				folderId: $rootScope.currentProject.folder,
				maxResults: 1
			}).$promise.then(function(result){
				$scope.$apply(function(){
					$scope.folderIsNotEmpty = result.items.length>0;
					$scope.folderIsEmpty = result.items.length==0;
					$scope.filesLoading = false;
				})
			})
			
		})
		
		$scope.iframeLoaded = function(){
			$scope.iframeready = true;
		}
		
	});