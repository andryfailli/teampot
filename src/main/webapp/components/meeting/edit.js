angular.module('teampot').
	controller('meetingEditController', function($rootScope,$scope,$routeParams,debounce,ProjectService,MeetingService,$timeout,NotifyService,RealtimeService) {
		
		var isNew = $routeParams.meetingId ? false : true;
		
		$scope.meeting = !isNew ? MeetingService.$get($routeParams.meetingId) : MeetingService.$new({project:$routeParams.projectId});
		
		$scope.createPoll = function(){
			$scope.meeting.poll = {
				proposedDates: []
			};
			$scope.addProposedDate({start: $scope.meeting.start, end:$scope.meeting.end});
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
		
		$scope.addProposedDate = function(proposedDate){
			
			if (!proposedDate) {
			
				var newStart;
				var newEnd;
				
				if ($scope.meeting.poll.proposedDates.length>0) {
					var lastProposedDate = $scope.meeting.poll.proposedDates[$scope.meeting.poll.proposedDates.length-1];
					if (lastProposedDate.start)	newStart = lastProposedDate.start+86400000; //+1d
					if (lastProposedDate.end) newEnd = lastProposedDate.end+86400000; //+1d
				} else {
					newStart = (new Date()).getTime();
					newEnd = newStart+7200000; //+2h
				}
				
				proposedDate = {
					start:newStart,
					end:newEnd
				};
				
			}

			$scope.meeting.poll.proposedDates.push(proposedDate);
			
			$scope.$watch("meeting.poll.proposedDates["+($scope.meeting.poll.proposedDates.length-1)+"]",$scope.updateProposedDateEndDate,true);
		}
		
		$scope.updateProposedDateEndDate = function(proposedDate){
			
			if (!proposedDate || !proposedDate.start) return;
			
			var startDate = new Date(proposedDate.start);
			var endTime = new Date(proposedDate.end);
			var endDate = new Date(proposedDate.end);
			endDate.setFullYear(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
			
			proposedDate.end = endDate.getTime();
			
			if (proposedDate.end<=proposedDate.start) {
				// re-calculate end date
				proposedDate.end = proposedDate.start+7200000; //+2h
			}
		}
		
		$scope.updatePollStartEndDates = function(){
			
			if (!$scope.meeting.poll || !$scope.meeting.poll.proposedDates || $scope.meeting.poll.proposedDates.length==0) return;
			
			var minStart = +Infinity;
			
			for (var i=0; i<$scope.meeting.poll.proposedDates.length; i++) {
				var proposedDate = $scope.meeting.poll.proposedDates[i];
				if (proposedDate.start && proposedDate.start<minStart) minStart = proposedDate.start;
			}
			
			if (!$scope.meeting.poll.startDate || $scope.meeting.poll.startDate > minStart) {
				$scope.meeting.poll.startDate = minStart - 604800000; //-1w
			}
			if (!$scope.meeting.poll.endDate || $scope.meeting.poll.endDate > minStart) {
				$scope.meeting.poll.endDate = minStart - 86400000; //-1d
			}
			
		}
		
		$scope.meeting.$promise.then(function(){
			$scope.meeting_bkp = angular.copy($scope.meeting);
			
			$scope.$watch("meeting.poll.proposedDates",$scope.updatePollStartEndDates,true);
			
			$scope.$watch("meeting.start",function(){$scope.updateProposedDateEndDate($scope.meeting);},true);
			
		});
		
		$scope.save = function() {
			if (angular.equals($scope.meeting_bkp,$scope.meeting)) return;
			
			NotifyService.info("Saving meeting...");
			
			$scope.meeting.$save().$promise
				.then(function(){
					RealtimeService.notify("Meeting",$scope.meeting);
					NotifyService.info("Meeting saved");
				})
				.catch(function(){
					NotifyService.error("An error occurred while saving this meeting",$scope.save);
				});
		}
		
		$scope.$on("$destroy",$scope.save);
		
		
	});