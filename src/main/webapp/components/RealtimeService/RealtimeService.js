angular.module("teampot").
	factory('RealtimeService',function($rootScope,$interval) {
		
		function subscribe(subject,entityList){
			
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
			
		};
		
		
		function notify(subject,entity) {
			$rootScope.$broadcast("realtime:"+subject,entity);
		};
		
		// FIXME: polling implementation doesn't scale well...try PubSub/Drive Realtime API
		function registerWatch(subject,retrieveFn){
			return $interval(function(){
				var entityList = retrieveFn();
				entityList.$promise.then(function(){
					for (var i=0; i<entityList.items.length; i++)
						notify(subject,entityList.items[i]);
				});
			},30000);
		};
		
		// FIXME: polling implementation doesn't scale well...try PubSub/Drive Realtime API
		function unregisterWatch(watchHandle){
			$interval.cancel(watchHandle);
		};
		
		return {
			subscribe: subscribe,
			notify: notify,
			registerWatch: registerWatch,
			unregisterWatch: unregisterWatch
		};
	});