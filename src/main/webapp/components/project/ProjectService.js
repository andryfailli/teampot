angular.module("teampot").
	factory('ProjectService',function($rootScope,GapiClient,$q) {
		
		client = GapiClient.client("teampot");
		
		var service = {
			$new: function(entity){
				entity = entity || {}
				entity.$resolved = true;
				
				var deferred = $q.defer();
				deferred.resolve(entity);
				entity.$promise = deferred.promise;
				
				entity.$save = function(){
					return client.exec("project.save",entity);
				}
				return entity;
			},
			$get: function(id){
				var entity = client.exec("project.get",{id:id});
				entity.$resolved = false;
				entity.$promise.then(function(){
					service.$new(entity);
					entity.$resolved = true;
					$rootScope.$apply();
				});
				return entity;
			},
			$list: function(){
				var entityList = client.exec("project.list");
				entityList.$resolved = false;
				entityList.$promise.then(function(){
					entityList.$resolved = true;
					if (!entityList.items) entityList.items = [];
					for (var i=0; i<entityList.items.length; i++) {
						var entity = entityList.items[i];
						service.$new(entity);
						entity.$resolved = true;
					}
					$rootScope.$apply();
				});
				return entityList;
			}
		};
		
		return service;
	});