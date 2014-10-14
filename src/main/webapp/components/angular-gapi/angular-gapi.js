angular.module("ngGapi",[]).
	provider('Gapi', {
		
		gapi: null,
		clientId: null,
		scope: [],
		token: null,
		
		libPromises: {},
		
		gapiDeferred: angular.injector(['ng']).get('$q').defer(),
		authDeferred: null,
		
		setGapi: function(gapi) {
			this.gapi = gapi;
			this.gapiDeferred.resolve(gapi);
		},
		
		setClientId: function(clientId){
			this.clientId = clientId;
		},
		
		setScope: function(scope){
			this.scope = scope;
		},
		
		load: function(name){
			var thisProvider = this;
								
			if (!thisProvider.libPromises[name]) {
				
				var libDeferred = thisProvider._get$q().defer();
				
				thisProvider.libPromises[name] = libDeferred.promise;
				
				// when gapi is ready, load lib
				thisProvider.gapiDeferred.promise.then(function(){
					
					thisProvider.gapi.load(name, {
						'callback': function(){
							libDeferred.resolve(name);
						}
					});
					
				});
			}
			
			return thisProvider.libPromises[name];
		},
		
		authorize: function(silent){
			var thisProvider = this;
			
			if (!thisProvider.authDeferred) {
				thisProvider.authDeferred = thisProvider._get$q().defer();
				
				// when gapi is ready, authorize
				thisProvider.gapiDeferred.promise.then(function(){
					
					var params = {
						client_id: thisProvider.clientId,
						scope: thisProvider.scope,
						immediate: silent
					}
					
					thisProvider.gapi.auth.authorize(params,function(response){
						if (response && response.error) 
							thisProvider.authDeferred.reject(response.error);
						 else {
							 thisProvider.authDeferred.resolve(response);
							 thisProvider.token = response.access_token;
						 }
					});
					
				});
				
			}
			return thisProvider.authDeferred.promise;
			
		},
		
		_get$q: function(){
			return angular.injector(['ng']).get('$q');
		},
		
		
		$get: function() {
			var thisProvider = this;
			
			return {
				gapi: function(){return thisProvider.gapi;},
				gapi$promise: function(){return thisProvider.gapiDeferred.promise},
				auth$promise: function(){return thisProvider.authDeferred ? thisProvider.authDeferred.promise : null},
				token: function(){return thisProvider.token;},
				load: function(name){return thisProvider.load(name);}
			};
		}
	}).
	config(function(GapiProvider){
		
		window.ngGapiInit = function(){
			GapiProvider.setGapi(window.gapi);
		}
		
		var scriptElement = document.createElement("script");
		scriptElement.setAttribute("src", "https://apis.google.com/js/client.js?onload=ngGapiInit");
		var parentElement = document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0];
		parentElement.appendChild(scriptElement);
				
	});