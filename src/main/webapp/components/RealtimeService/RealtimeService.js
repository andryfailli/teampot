angular.module("teampot").
	factory('RealtimeService',function($rootScope) {
		
		var service = {
			subscribe: function(subject,entityList){
				
				function updateList(entity) {
					var found = false;
					for (var i=0; i<entityList.items.length; i++){
						if (entityList.items[i].id == entity.id) {
							angular.copy(entity,entityList.items[i]);
							found = true;
						}
					}
					if (!found) entityList.items.push(entity);
				}
				
				$rootScope.$on("realtime:"+subject,function(evt,entity){
					
					if (entityList.$promise)
						entityList.$promise.then(function(){
							$rootScope.$apply(function(){
								updateList(entity);
							});
						});
					else 
						$rootScope.$apply(function(){
							updateList(entity);
						});
					
				});
				
				
			},
			notify: function(subject,entity) {
				$rootScope.$broadcast("realtime:"+subject,entity);
			}
		};
		
		return service;
	});