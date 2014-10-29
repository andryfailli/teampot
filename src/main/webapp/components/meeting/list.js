angular.module('teampot').
	controller('meetingListController', function($scope,$routeParams,$location,ProjectService,MeetingService) {
		
		$scope.project = ProjectService.$get($routeParams.projectId);
		$scope.meetingList = MeetingService.$list($routeParams.projectId);
		
		$scope.goto = function(meeting){
			$location.path("/project/"+$routeParams.projectId+"/meeting/"+meeting.id);
		}
		
	});