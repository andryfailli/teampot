angular.module('teampot').
	controller('meetingListController', function($rootScope,$scope,$routeParams,$location,ProjectService,MeetingService,GapiClient,NotifyService,CONSTANTS,RealtimeService,$interval) {
		
		$scope.meetingList = MeetingService.$list($routeParams.projectId);
		var watchHandle = RealtimeService.registerWatch("Meeting",function(){return MeetingService.$list($routeParams.projectId);});
		RealtimeService.subscribe("Meeting",$scope.meetingList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
		$scope.filter_isToBeScheduled = function(meeting) {
			return !meeting.start;
		}	
		$scope.filter_isPast = function(meeting) {
			return meeting.start && meeting.past;
		}
		$scope.filter_isNotPast = function(meeting) {
			return meeting.start && !meeting.past;
		}
		
		$scope.poll_getHasVotedTrueFilter = function(proposedDate){
			return (function(vote){
				return vote.proposedDate.start == proposedDate.start && vote.proposedDate.end == proposedDate.end && vote.result == true;
			});
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
				if (meeting.poll.votes[i].user.id == $rootScope.currentUser.id && meeting.poll.votes[i].proposedDate.start == proposedDate.start && meeting.poll.votes[i].proposedDate.end == proposedDate.end)
					return i;
			}
			return -1;
		}
		
		$scope.poll_vote = function(meeting,proposedDate,result,$event) {
			
			if ($scope.voting) return;
			
			$scope.voting = true;
			
			var updatedMeeting = GapiClient.client("teampot").exec("meeting.pollVote",{
				id: meeting.id,
				proposedStartDate: proposedDate.start,
				proposedEndDate: proposedDate.end,
				result: result
			})
			
			updatedMeeting.$promise.then(function(){
				$scope.voting = false;
				NotifyService.info("Your vote has been recorded");
				
				var meetingIndex = $scope.meetingList.items.indexOf(meeting);
				$scope.meetingList.items[meetingIndex] = updatedMeeting;
				
			},function(){
				NotifyService.error("An error occurred while recording your vote",function(){$scope.poll_vote(meeting,proposedDate,result,$event);});
			});
			
			$event.stopPropagation();
		}

		$scope.getHangoutLink = function(meeting){
			if (meeting.hangoutLink) {
				var hasQueryParams = meeting.hangoutLink.indexOf("?")>-1;
				return meeting.hangoutLink+(hasQueryParams?"&":"?")+"gid="+CONSTANTS.HANGOUT_APPID+"&gd="+meeting.id;
			} else {
				var useHangoutApp = CONSTANTS.HANGOUT_APPID ? true : false;
				return "https://plus.google.com/hangouts/_/"+CONSTANTS.APPS_DOMAIN+"/teampot_"+meeting.id + (useHangoutApp ? "?gid="+CONSTANTS.HANGOUT_APPID+"&gd="+meeting.id : "");
			}
		}
		
		$scope.goto = function(meeting){
			$location.path("/project/"+$routeParams.projectId+"/meeting/"+meeting.id);
		}
		
	});