angular.module('teampot').
	controller('fileListFabController', function($rootScope,$scope,$q,$route,$routeParams,GapiPicker,GapiClient,ProjectService,NotifyService) {
				
		$scope.project = ProjectService.$get($routeParams.projectId);
		
		$scope.fabAdd = function(evt){
			
			GapiPicker.create().then(function(pickerBuilder){
				var picker = pickerBuilder.
					addView(new google.picker.View(google.picker.ViewId.DOCS)).
					addView(new google.picker.DocsUploadView()).
					enableFeature(google.picker.Feature.MULTISELECT_ENABLED).
					setCallback(function(data){
						
						if (data.action == google.picker.Action.PICKED) {
							
							var promises = [];
							
				            var files = data.docs;
				            for (var i=0; i<files.length; i++) {
				            	promises.push(GapiClient.client("drive").exec("files.update",{fileId:files[i].id,addParents:$scope.project.folder}).$promise);
							}

				            $q.all(promises)
					            .then(function(){
					            	NotifyService.info("File \""+files[0].name+"\" "+(files.length>1 ? "(and other "+(files.length-1)+" files)" : "")+" added.");
					            })
					            .catch(function(){
					            	NotifyService.error("An error occurred while adding selected files");
					            })
					            .finally(function(){
					            	//TODO: enhancement: reload only the iframe, not the whole view.
					            	$route.reload();
					            });
				        }
						
						
					}).build();
				
				picker.setVisible(true);
			})
			
		}

	});