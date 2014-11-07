angular.module('teampot').
	controller('meetingListController', function($scope,$routeParams,$location,ProjectService,MeetingService) {
		
		$scope.meetingList = MeetingService.$list($routeParams.projectId);
		
		$scope.goto = function(meeting){
			$location.path("/project/"+$routeParams.projectId+"/meeting/"+meeting.id);
		}
		
	});