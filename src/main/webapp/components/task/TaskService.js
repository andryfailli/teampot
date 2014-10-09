angular.module("teampot").
	factory('TaskService',function(GapiClient) {
		
		client = GapiClient.client("teampot");
		
		var service = {
			$new: function(entity){
				entity = entity || {}
				entity.$save = function(){
					return client.exec("task.save",entity);
				}
				return entity;
			},
			$get: function(key){
				var entity = client.exec("task.get",{key:key});
				entity.$promise.then(function(){
					service.$new(entity);
				});
				return entity;
			},
			$list: function(){
				var entityList = client.exec("task.list");
				entityList.$promise.then(function(){
					for (var i=0; i<entityList.items.length; i++) {
						service.$new(entityList.items[i]);
					}
				});
				return entityList;
			}
		};
		
		return service;
	});