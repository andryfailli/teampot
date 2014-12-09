angular.module('teampot').
	controller('meetingEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,MeetingService,$timeout,NotifyService) {
		
		var isNew = $routeParams.meetingId ? false : true;
		
		$scope.meeting = !isNew ? MeetingService.$get($routeParams.meetingId) : MeetingService.$new({project:$routeParams.projectId});
		
		$scope.createPoll = function(){
			$scope.meeting.poll = {
				proposedDates: [{start: $scope.meeting.start, end:$scope.meeting.end}]
			};
			$scope.addProposedDate();
			$scope.meeting.start = null;
			$scope.meeting.end = null;
		}
		
		$scope.removeProposedDate = function(index) {
			$scope.meeting.poll.proposedDates.splice(index,1);
			if ($scope.meeting.poll.proposedDates.length==1) {
				$scope.meeting.start = $scope.meeting.poll.proposedDates[0].start;
				$scope.meeting.end = $scope.meeting.poll.proposedDates[0].end
				$scope.meeting.poll = null;
			}
		}
		
		$scope.addProposedDate = function(){
			$scope.meeting.poll.proposedDates.push({
				start:null,
				end:null
			});
		}
		

		
		$scope.save = function() {
			$scope.meeting.$save().$promise
				.then(function(){
					NotifyService.info("Meeting saved");
				})
				.catch(function(){
					NotifyService.error("An error occurred while saving this meeting",$scope.save);
				});
		}
		
		$scope.$on("$destroy",$scope.save);
		
		
	});