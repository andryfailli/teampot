angular.module('teampot').
	controller('meetingListController', function($rootScope,$scope,$routeParams,$location,ProjectService,MeetingService,GapiClient) {
		
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
		
		$scope.poll_hasVoted = function(meeting,proposedDate,result) {
			if (!meeting.poll.votes) return false;
			var voteIndex = $scope.poll_getVoteIndex(meeting,proposedDate);
			if (voteIndex>-1 && meeting.poll.votes[voteIndex].result == result)
				return true;
			else
				return false;
		}
		
		$scope.poll_getVoteIndex = function(meeting,proposedDate) {
			if (!meeting.poll.votes) return -1;
			for (var i=0; i<meeting.poll.votes.length; i++) {
				if (meeting.poll.votes[i].user.id == $rootScope.currentUser.id && meeting.poll.votes[i].proposedDate == proposedDate)
					return i;
			}
			return -1;
		}
		
		$scope.poll_vote = function(meeting,proposedDate,result,$event) {
			if (!meeting.poll.votes) meeting.poll.votes = [];
			
			var voteIndex = $scope.poll_getVoteIndex(meeting,proposedDate);
			if (voteIndex>-1) {
				meeting.poll.votes.splice(voteIndex,1);
			}
			
			meeting.poll.votes.push({
				user:$rootScope.currentUser,
				proposedDate:proposedDate,
				result:result
			});
			
			GapiClient.client("teampot").exec("meeting.pollVote",{
				id: meeting.id,
				proposedDate: proposedDate,
				result: result
			});
			
			$event.stopPropagation();
		}

		$scope.goto = function(meeting){
			$location.path("/project/"+$routeParams.projectId+"/meeting/"+meeting.id);
		}
		
	});