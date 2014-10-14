angular.module('teampot').
	controller('fileListFabController', function($rootScope,$scope,$q,GapiPicker,TaskService) {
				
		$scope.fabAdd = function(evt){
			/*
			$q.all([Gapi.load("picker"),Gapi.auth$promise()]).then(function(pickerLib){
				
				var picker = new google.picker.PickerBuilder().
	              //addView(google.picker.ViewId.PHOTOS).
	              setOAuthToken(Gapi.token()).
	              setDeveloperKey(developerKey).
	              setCallback(pickerCallback).
	              build();
	          picker.setVisible(true);
				
			})*/
			
			GapiPicker.create().then(function(pickerBuilder){
				var picker = pickerBuilder.setCallback(function(data){
					// TODO: to be implemented
				}).build();
				
				picker.setVisible(true);
			})
			
		}

	});