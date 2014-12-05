angular.module('teampot').
	controller('taskListController', function($rootScope,$scope,$routeParams,$location,ProjectService,TaskService) {
		
		$scope.taskList = TaskService.$list($routeParams.projectId);
		
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
				return !item.completed == false && item.dueDate;
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
		
		$scope.goto = function(task){
			$location.path("/project/"+$routeParams.projectId+"/task/"+task.id);
		}
		
	});