angular.module('teampot').
	controller('taskListController', function($rootScope,$scope,$routeParams,$location,$timeout,ProjectService,TaskService,RealtimeService) {
		
		$scope.taskList = TaskService.$list($routeParams.projectId);
		var watchHandle = RealtimeService.registerWatch("Task",function(){return TaskService.$list($routeParams.projectId);});
		RealtimeService.subscribe("Task",$scope.taskList);
		
		$scope.$on("$destroy",function(){RealtimeService.unregisterWatch(watchHandle);});
		
		$scope.currentFilter = null;
		$scope.currentOrderBy = null;
		$scope.currentOrderByReverse = true;
		
		$scope.showTab_all = function(){
			$scope.currentFilter = null;
			$scope.currentOrderBy = "priority";
			$scope.currentOrderByReverse = true;
			$scope.tabSelected = 0;
		}
		
		$scope.showTab_upcoming = function(){
			$scope.currentFilter = function(item) {
				return !item.completed && item.dueDate;
			};
			$scope.currentOrderBy = "dueDate";
			$scope.currentOrderByReverse = false;
			$scope.tabSelected = 1;
		}
		
		$scope.showTab_mine = function(){
			$scope.currentFilter = function(item){
				return item.assignee && item.assignee.id == $rootScope.currentUser.id;
			};
			$scope.currentOrderBy = "priority";
			$scope.currentOrderByReverse = false;
			$scope.tabSelected = 2;
		}
		
		$scope.showTab_unassigned = function(){
			$scope.currentFilter = function(item){
				return !item.completed && !item.assignee;
			};
			$scope.currentOrderBy = "priority";
			$scope.currentOrderByReverse = false;
			$scope.tabSelected = 3;
		}
		
		
		$scope.selectNextTab = function(){
			if ($scope.tabSelected+1<4)
				$scope.tabSelected++;
		}
		$scope.selectPrevTab = function(){
			if ($scope.tabSelected-1>=0)
				$scope.tabSelected--;
		}
		$rootScope.$on("swipe-left",$scope.selectNextTab);
		$rootScope.$on("swipe-right",$scope.selectPrevTab);
		
		$scope.goto = function(task){
			$location.path("/project/"+$routeParams.projectId+"/task/"+task.id);
		}
		
		$scope.saveTaskAsync = function(task){
			$timeout(function(){
				task.$save();
			},1000);
		}
		
	});