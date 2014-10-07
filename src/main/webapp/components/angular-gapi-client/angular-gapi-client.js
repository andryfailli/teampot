angular.module("ngGapiClient",[]).
	provider('GapiClient', {
		
		gapi: null,
		clientId: null,
		apiKey: null,
		scope: [],
		clients: {},
		
		gapiDeferred: angular.injector(['ng']).get('$q').defer(),
		
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
		
		_get$q: function(){
			return angular.injector(['ng']).get('$q');
		},
		
		_buildClientDraft: function(name,promise){
			
			var thisProvider = this;
			
			return{
				exec: function(methodName,payload){
					
					function traverse(o,path) {
						var pieces = path.split(".");
						for (var i=0; i<pieces.length; i++)
							o = o[pieces[i]];
						return o;
					}
					
					var execDeferred = thisProvider._get$q().defer();
					promise.then(function(){
						var client = thisProvider.clients[name];
						var method = traverse(client,methodName);
						var execCallback = function(reponse) {
							if (reponse.error)
								execDeferred.reject(reponse.error);
							else
								execDeferred.resolve(response);
						}
						payload ? method().execute(payload,execCallback) : method().execute(execCallback);
					});
					
					return {
						$promise: execDeferred.promise
					}
				},
				$promise: promise
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