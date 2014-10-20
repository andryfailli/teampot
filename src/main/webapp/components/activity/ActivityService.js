angular.module("teampot").
	factory('ActivityService',function($rootScope,GapiClient) {
		
		client = GapiClient.client("teampot");
		
		var service = {
			$new: function(entity){
				entity = entity || {}
				entity.$resolved = true;
				entity.$save = function(){
					return client.exec("activity.save",entity);
				}
				return entity;
			},
			$get: function(id){
				var entity = client.exec("activity.get",{id:id});
				entity.$resolved = false;
				entity.$promise.then(function(){
					service.$new(entity);
					entity.$resolved = true;
					$rootScope.$apply();
				});
				return entity;
			},
			$list: function(projectId){
				var entityList = client.exec("activity.list",{project:projectId});
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