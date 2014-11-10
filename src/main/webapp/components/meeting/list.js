angular.module('teampot').
	controller('meetingListController', function($scope,$routeParams,$location,ProjectService,MeetingService) {
		
		$scope.meetingList = MeetingService.$list($routeParams.projectId);
		
		$scope.filter_isToBeScheduled = function(meeting) {
			return !meeting.timestamp;
		}	
		$scope.filter_isPast = function(meeting) {
			return meeting.timestamp && meeting.past;
		}
		$scope.filter_isNotPast = function(meeting) {
			return meeting.timestamp && !meeting.past;
		}

		$scope.goto = function(meeting){
			$location.path("/project/"+$routeParams.projectId+"/meeting/"+meeting.id);
		}
		
	});