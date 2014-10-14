angular.module("ngGapi").
	provider('GapiPicker', {
			
		developerKey: null,
		
		setDeveloperKey: function(developerKey){
			this.developerKey = developerKey;
		},
		
		$get: function($q, Gapi) {
			var thisProvider = this;
			return {
				create: function(){
					var pickerBuilderDeferred = $q.defer();
					$q.all([Gapi.load("picker"),Gapi.auth$promise()]).then(function(){
						
						var pickerBuilder = new google.picker.PickerBuilder().
							setOAuthToken(Gapi.token()).
							setDeveloperKey(thisProvider.developerKey);
						
						pickerBuilderDeferred.resolve(pickerBuilder);
					});
					
					return pickerBuilderDeferred.promise;
				}
			}			
		}
	});