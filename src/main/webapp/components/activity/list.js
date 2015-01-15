angular.module('teampot').
	controller('activityListController', function($scope,$routeParams,ProjectService,ActivityService,CONSTANTS,UserService) {
		
		$scope.activityList = ActivityService.$list($routeParams.projectId,0);
		
		$scope.users = UserService.$list({project: $routeParams.projectId});
		
		$scope.activityList.$promise.then(function(){
			$scope.$apply(function(){
				if (!$scope.activityList.items || $scope.activityList.items.length < CONSTANTS.LIST_PAGE_SIZE) {
						$scope.loadMoreEndReached = true;
						return;
					}
			})
		})
		
		$scope.loadMore = function(){
			$scope.loadingMore = true;
			
			$scope.activityListPage = $scope.activityListPage ? $scope.activityListPage : 0;
			
			var newPage = ActivityService.$list($routeParams.projectId,$scope.activityListPage+1);
			newPage.$promise.then(function(){
				$scope.$apply(function(){
					$scope.loadingMore = false;
					if (!newPage.items || newPage.items.length < CONSTANTS.LIST_PAGE_SIZE) {
						$scope.loadMoreEndReached = true;
						return;
					}
					
					for (var i=0; i<newPage.items.length; i++) {
						$scope.activityList.items.push(newPage.items[i]);
					}
					
					$scope.activityListPage++;
				})
			})
		}
		
	});