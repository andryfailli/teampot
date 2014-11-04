angular.module('teampot').
	controller('meetingEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,MeetingService,$timeout,NotifyService) {
		
		var isNew = $routeParams.meetingId ? false : true;
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.meeting = !isNew ? MeetingService.$get($routeParams.meetingId) : MeetingService.$new({project:$routeParams.projectId});
		
		function registerSaveWatch() {
			$scope.$watch("meeting",function(newEntity,oldEntity){
				if (!angular.equals(newEntity,oldEntity)) {
					$scope.saveDebounced();
				}
			},true)
		}
		
		if (isNew)
			registerSaveWatch();
		else
			$scope.meeting.$promise.then(registerSaveWatch);
		
		$scope.save = function() {
			$scope.meeting.$save().$promise
				.then(function(){
					NotifyService.info("Meeting saved");
				})
				.catch(function(){
					NotifyService.error("An error occurred while saving this meeting",$scope.save);
				});
		}
		
		$scope.saveDebounced = debounce($scope.save, 2000, false);
		
	});