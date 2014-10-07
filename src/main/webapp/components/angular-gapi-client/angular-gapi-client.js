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
			
			var clients = this.clients;
						
			if (!clients[name]) {
						
				var $q = angular.injector(['ng']).get('$q');
				
				var clientDeferred = $q.defer();
				
				clients[name] = {
					exec: function(methodName,payload){
						
						function traverse(o,path) {
							var pieces = path.split(".");
							for (var i=0; i<pieces.length; i++)
								o = o[pieces[i]];
							return o;
						}
						
						var execDeferred = $q.defer();
						clientDeferred.promise.then(function(){
							var client = clients[name];
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
					$promise: clientDeferred.promise
				};
				
				clients[name].$promise.then(function(){
					angular.extend(clients[name],gapi.client[name]);
				});
					
				this.gapiDeferred.promise.then(function(gapi){
					$q.when(gapi.client.load(name,version)).then(clientDeferred.resolve,clientDeferred.reject,clientDeferred.notify);
				});
				
				
			}
			return clients[name];
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