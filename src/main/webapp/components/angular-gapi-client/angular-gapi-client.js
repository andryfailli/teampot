angular.module("ngGapiClient",[]).
	provider('GapiClient', {
		
		gapi: null,
		clientId: null,
		apiKey: null,
		scope: [],
		clients: {},
		
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
		
		setApiKey: function(apiKey){
			this.apiKey = apiKey;
		},
		
		load: function(name,version){
			
			var thisProvider = this;
						
			if (!thisProvider.clients[name]) {
								
				var clientDeferred = thisProvider._get$q().defer();
				
				thisProvider.clients[name] = thisProvider._buildClientDraft(name,clientDeferred.promise);
				thisProvider.clients[name].$promise.then(function(){
					angular.extend(thisProvider.clients[name],thisProvider.gapi.client[name]);
				});
				
				// when gapi is ready, load client
				this.gapiDeferred.promise.then(function(){
					thisProvider._get$q().when(thisProvider.gapi.client.load(name,version)).then(clientDeferred.resolve,clientDeferred.reject,clientDeferred.notify);
				});
				
			}
			return thisProvider.clients[name];
		},
		
		authorize: function(silent){
			var thisProvider = this;
			
			if (!thisProvider.authDeferred) {
				thisProvider.authDeferred = thisProvider._get$q().defer();
				
				// when gapi is ready, authorize
				this.gapiDeferred.promise.then(function(){
					
					var params = {
						client_id: thisProvider.clientId,
						scope: thisProvider.scope,
						immediate: silent
					}
					
					thisProvider.gapi.auth.authorize(params,function(response){
						if (response && response.error) 
							thisProvider.authDeferred.reject(response.error);
						 else
							 thisProvider.authDeferred.resolve(response);
					});
					
				});
				
			}
			return thisProvider.authDeferred.promise;
			
		},
		
		_get$q: function(){
			return angular.injector(['ng']).get('$q');
		},
		
		_buildClientDraft: function(name,clientPromise){
			
			var thisProvider = this;
			
			return{
				exec: function(methodName,payload){
					
					function traverse(o,path) {
						var pieces = path.split(".");
						for (var i=0; i<pieces.length; i++)
							o = o[pieces[i]];
						return o;
					}
					
					var execResultDeferred = thisProvider._get$q().defer();
					
					var execStartPromise = thisProvider._get$q().all( thisProvider.authDeferred ? [thisProvider.authDeferred.promise] : [clientPromise,thisProvider.authDeferred.promise] );
					
					execStartPromise.then(function(){
						var client = thisProvider.clients[name];
						var method = traverse(client,methodName);
						var execCallback = function(response) {
							if (response && response.error) 
								 execResultDeferred.reject(response.error);
							 else
								 execResultDeferred.resolve(response);
						}
						payload ? method().execute(payload,execCallback) : method().execute(execCallback);
					});
					
					return {
						$promise: execResultDeferred.promise
					}
				},
				$promise: clientPromise
			};
		},
		
		$get: function() {
			
			var gapi = this.gapi;
			var clients = this.clients;

			return {
				gapi: function(){return gapi;},
				client: function(name){return clients[name];}
			};
		}
	}).
	config(function(GapiClientProvider){
		
		window.ngGapiClientInit = function(){
			GapiClientProvider.setGapi(window.gapi);
		}
		
		var scriptElement = document.createElement("script");
		scriptElement.setAttribute("src", "https://apis.google.com/js/client.js?onload=ngGapiClientInit");
		var parentElement = document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0];
		parentElement.appendChild(scriptElement);
				
	});