angular.module('teampot').
	controller('meetingEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,MeetingService,$timeout,NotifyService) {
		
		var isNew = $routeParams.meetingId ? false : true;
		
		$scope.meeting = !isNew ? MeetingService.$get($routeParams.meetingId) : MeetingService.$new({project:$routeParams.projectId});
		
		$scope.createPoll = function(){
			$scope.meeting.poll = {
				proposedDates: [$scope.meeting.timestamp,null]
			};
		}
		
		$scope.removeProposedDate = function(index) {
			$scope.meeting.poll.proposedDates.splice(index,1);
			if ($scope.meeting.poll.proposedDates.length==1) {
				$scope.meeting.timestamp = $scope.meeting.poll.proposedDates[0];
				$scope.meeting.poll = null;
			}
		}
		
		
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