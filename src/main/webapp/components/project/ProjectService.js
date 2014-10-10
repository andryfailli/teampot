angular.module("teampot").
	factory('ProjectService',function($rootScope,GapiClient) {
		
		client = GapiClient.client("teampot");
		
		var service = {
			$new: function(entity){
				entity = entity || {}
				entity.$save = function(){
					return client.exec("project.save",entity);
				}
				return entity;
			},
			$get: function(key){
				var entity = client.exec("project.get",{k:key});
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
					if (entityList.items) {
						for (var i=0; i<entityList.items.length; i++) {
							var entity = entityList.items[i];
							service.$new(entity);
							entity.$resolved = true;
						}
					}
					$rootScope.$apply();
				});
				return entityList;
			}
		};
		
		return service;
	});