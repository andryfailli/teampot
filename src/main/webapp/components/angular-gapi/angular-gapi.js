angular.module("ngGapi",[]).
	provider('Gapi', {
		
		gapi: null,
		clientId: null,
		scope: [],
		accesstype: 'online',
		token: null,
		
		libPromises: {},
		
		gapiDeferred: angular.injector(['ng']).get('$q').defer(),
		authDeferred: angular.injector(['ng']).get('$q').defer(),
		
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
		
		setAccessType: function(accesstype){
			this.accesstype = accesstype;
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
			
			var currentAuthTryDeferred = thisProvider._get$q().defer();

			// when gapi is ready, authorize
			thisProvider.gapiDeferred.promise.then(function(){
				
				var params = {
					clientid: thisProvider.clientId,
					scope: typeof thisProvider.scope === "object" ? thisProvider.scope.join(" ") : thisProvider.scope,
					cookiepolicy: 'single_host_origin',
					accesstype: thisProvider.accesstype,
					immediate: silent,
					callback: function(response){
						if (response && response.error) {
							currentAuthTryDeferred.reject(response.error);
						} else {
							thisProvider.token = response.access_token;
							thisProvider.code = response.code;
							currentAuthTryDeferred.resolve(response);
							thisProvider.authDeferred.resolve(response);
						}
						
					}
				}
				
				thisProvider.gapi.auth.signIn(params);
				
			});
				
			return currentAuthTryDeferred.promise;
			
		},
		
		logout: function(){
			var thisProvider = this;
			
			if (thisProvider.authDeferred) {
				// https://developers.google.com/+/web/signin/sign-out
				thisProvider.gapi.auth.signOut();
				thisProvider.authDeferred.reject('logout');
			}
			
			// re-init
			thisProvider.authDeferred = thisProvider._get$q().defer();
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
				load: function(name){return thisProvider.load(name);},
				authorize: function(silent){return thisProvider.authorize(silent);},
				logout: function(){return thisProvider.logout();}
			};
		}
	}).
	config(function(GapiProvider){
		
		window.ngGapiInit = function(){
			window.ngGapiInitSemaphore--;
			if (window.ngGapiInitSemaphore==0)
				GapiProvider.setGapi(window.gapi);
		}
				
		function loadScript(id,src) {
			
			var prefix = "ngGapiScript_";
			
			if (!window.ngGapiInitSemaphore) window.ngGapiInitSemaphore = 0;
			
			if (!document.getElementById(prefix+id)) {
				
				window.ngGapiInitSemaphore++;
				
				var scriptElement = document.createElement("script");
				scriptElement.setAttribute("src", src+"?onload=ngGapiInit");
				scriptElement.setAttribute("id", prefix+id);
				var parentElement = document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0];
				parentElement.appendChild(scriptElement);
			}
			
			
		}
		
		loadScript("jsapi","https://apis.google.com/js/platform.js");
		loadScript("client","https://apis.google.com/js/client:platform.js");
		
				
	});