angular.module("teampot").
	directive('tfFloat', function() {
	    return {
	      restrict: 'E',
	      replace: true,
	      scope : {
	        fid : '@?',
	        value : '='
	      },
	      compile : function() {
	        return {
	          pre : function(scope, element, attrs) {
	            // transpose `disabled` flag
	            if ( angular.isDefined(attrs.disabled) ) {
	              element.attr('disabled', true);
	              scope.isDisabled = true;
	            }
	
	            // transpose the `label` value
	            scope.label = attrs.label || "";
	            scope.fid = scope.fid || scope.label;
	
	            // transpose optional `type` and `class` settings
	            element.attr('type', attrs.type || "text");
	            element.attr('class', attrs.class );
	          }
	        }
	      },
	      template: function(element, attrs){
	    	  var tagName = attrs.type === "textarea" ? "material-textarea" : "material-input";
	        return'<material-input-group ng-disabled="{{isDisabled}}">' +
	          '<label for="{{fid}}">{{label}}</label>' +
	          '<'+tagName+' id="{{fid}}" ng-model="value">' +
	        '</material-input-group>'
	      }
	    };
	});
