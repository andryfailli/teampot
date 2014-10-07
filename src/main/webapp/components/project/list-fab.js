angular.module('teampot').
	controller('projectListFabController', ['$rootScope','$scope','$materialDialog',function($rootScope,$scope,$materialDialog) {
				
		$scope.fabAdd = function(evt){
			$materialDialog.show({
				templateUrl: '/components/project/edit-modal.html',
				targetEvent: evt,
				controller: 'projectEditModalController',
			}).then(function(result) {
				alert('You said the information was "' + answer + '".');
			}, function() {
				alert('You cancelled the dialog.');
			});
		}

	}]);