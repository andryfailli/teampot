angular.module('teampot').
	controller('activityListController', function($scope,$routeParams,ProjectService,ActivityService) {
		
		$scope.activityList = ActivityService.$list($routeParams.projectId,0);
		
		$scope.loadMore = function(){
			$scope.loadingMore = true;
			
			$scope.activityListPage = $scope.activityListPage ? $scope.activityListPage : 0;
			
			var newPage = ActivityService.$list($routeParams.projectId,$scope.activityListPage+1);
			newPage.$promise.then(function(){
				$scope.$apply(function(){
					for (var i=0; i<newPage.items.length; i++) {
						$scope.activityList.items.push(newPage.items[i]);
					}
					$scope.loadingMore = false;
					if (newPage.items.length == 0) $scope.loadMoreEndReached = true;
					$scope.activityListPage++;
				})
			})
		}
		
	});