angular.module("ngGapi").
	provider('GapiClient', {
		
		clients: {},
		
		gapiDeferred: angular.injector(['ng']).get('$q').defer(),
		authDeferred: null,
		
		// FIXME: Instantiated on service $get. How to get an instance in the provider config function?
		_$rootScope: null,
		
		load: function(name,version,root){
			
			var thisProvider = this;
						
			if (!thisProvider.clients[name]) {
								
				var clientDeferred = thisProvider._get$q().defer();
				
				thisProvider.clients[name] = thisProvider._buildClientDraft(name,clientDeferred.promise);
				thisProvider.clients[name].$promise.then(function(){
					angular.extend(thisProvider.clients[name],thisProvider._getGapi().gapi().client[name]);
				});
				
				// when gapi is ready, load client
				thisProvider._getGapi().gapi$promise().then(function(){
					thisProvider._getGapi().gapi().client.load(name,version,function(){
						clientDeferred.resolve(thisProvider.clients[name]);
					},root);
				});

				
			}
			return thisProvider.clients[name];
		},
		
		_get$q: function(){
			return angular.injector(['ng']).get('$q');
		},
		
		_getGapi: function(){
			return angular.injector(['ngGapi']).get('Gapi');
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
					execResultDeferred.promise.then(function(){
						execResultDeferred.promise.$resolved = true;
						execResultDeferred.promise.$sucess = true;
					},function(){
						execResultDeferred.promise.$resolved = true;
						execResultDeferred.promise.$error = true;
					})
					
					var execStartPromise = thisProvider._get$q().all( thisProvider._getGapi().auth$promise() ? [clientPromise,thisProvider._getGapi().auth$promise()] : [clientPromise] );
					
					execStartPromise.then(function(){
						var client = thisProvider.clients[name];
						var method = traverse(client,methodName);
						var execCallback = function(response) {
							if (response && response.error) 
								 execResultDeferred.reject(response.error);
							 else
								 execResultDeferred.resolve(response);
						}
						// finally, exec method
						method(payload).execute(execCallback);
					});
					
					var execResult = thisProvider._buildExecResultDraft(payload,execResultDeferred.promise);
					execResult.$promise.then(function(result){
						thisProvider._$rootScope.$apply(function(){
							angular.extend(execResult,result);
						});
					});
					
					return execResult;
				},
				$promise: clientPromise
			};
		},
		
		_buildExecResultDraft: function(requestPayload,execResultPromise){
			var payload = !requestPayload || typeof requestPayload !== "object" ? {} : requestPayload;
			payload.$promise = execResultPromise;
			return payload
		},
		
		$get: function($rootScope) {
			
			var thisProvider = this;
			
			// FIXME: workaround to get and instance of $rootScope in the provider config function
			thisProvider._$rootScope = $rootScope;

			return {
				gapi: function(){return thisProvider._getGapi().gapi();},
				client: function(name,version,root){return thisProvider.load(name,version,root);}
			};
		}
	});