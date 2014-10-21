angular.module('teampot').
	controller('fileListFabController', function($rootScope,$scope,$q,GapiPicker,GapiClient,TaskService) {
				
		$scope.fabAdd = function(evt){
			
			GapiPicker.create().then(function(pickerBuilder){
				var picker = pickerBuilder.
					addView(new google.picker.View(google.picker.ViewId.DOCS)).
					addView(new google.picker.DocsUploadView()).
					setCallback(function(data){
						
						if (data.action == google.picker.Action.PICKED) {
				            var files = data.docs;
				            for (var i=0; i<files.length; i++) {
				            	// TODO: to be implemented: add file to project folder
							}
				            // TODO: refresh iframe
				        }
						
						
					}).build();
				
				picker.setVisible(true);
			})
			
		}

	});