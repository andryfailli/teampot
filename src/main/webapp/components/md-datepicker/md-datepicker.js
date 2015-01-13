angular.module('material.components.datepicker', [
    'material.components.button',
	'material.components.whiteframe',
	'material.components.content',
	'material.components.icon',
	'datePicker'
])
.directive('mdDatepicker', MdDatepickerDirective);

function MdDatepickerDirective($mdUtil) {
	return {
		restrict: "E",
		replace: true,
		require: ['^?mdInputGroup'],
		scope: {
			fid : '@mdFid',
			format: '@',
			minView: '@',
			maxView: '@',
			model: '=ngModel',
			type: '@'
		},
		template: 
			'<div>'+
			'<div ng-show="value && !focused" layout="row" style="border-bottom: 1px solid rgba(0, 0, 0, 0.12); padding-top:2px;">'+
			'	<div class="datepicker-value" flex ng-click="focused=true;focusTextbox()"><span>{{value|date:format}}</span></div>'+
			'	<md-button ng-show="value" ng-click="value=null;focusTextbox()" style="padding:0;">'+
			'	 	<i class="mdi-cancel"></i>'+
			' 	</md-button>'+
			'</div>'+
			'<input id="{{fid}}" ng-hide="value && !focused" type="{{type ? type : defaultType}}" ng-model="value" ng-focus="onTextboxFocus()" ng-blur="onTextboxBlur()">'+
			'<div ng-show="needsPolyfillUI && focused" class="datepicker-container" ng-mouseenter="onMouseEnter()" ng-mouseleave="onMouseLeave()">'+
			'	<md-whiteframe class="md-whiteframe-z1" layout>'+
			'		<md-content>'+
			'			<div date-picker="value" min-view="{{minView}}" max-view="{{maxView}}"></div>'+
			'		</md-content>'+
			'	</md-whiteframe>'+
			'</div>'+
			'</div>',
			
		link: function(scope, element, attr, ctrls) {
			if (!ctrls[0]) return;
			var inputGroupCtrl = ctrls[0];
			
			function isHtml5InputTypeSupported(type) {
				type = type.toString().toLowerCase();
				var elem = document.createElement("input");
				elem.setAttribute("type", type);
				return elem.type == type;
			}
			
			scope.defaultType = "date";
			scope.needsPolyfillUI = !isHtml5InputTypeSupported(attr.type ? attr.type : scope.defaultType);			

			function hasValue(){
				return !angular.isUndefined(scope.value) && scope.value != null;
			}
			
			function datesEqual(date1,date2) {
				if (date1 === date2) return true;
				if (!date1 && !date2) return true;
				if ( (!date1 && date2) || (date1 && !date2) ) return false;
				
				if (typeof date1 === "string") date1 = parseInt(date1);
				if (typeof date2 === "string") date2 = parseInt(date1);
				
				if (typeof date1 === "number") date1 = new Date(date1);
				if (typeof date2 === "number") date2 = new Date(date2);
				
				return date1.getTime()===date2.getTime();
			}
			
		    scope.$watch("value",function(){
				inputGroupCtrl.setHasValue(hasValue());
			});
			inputGroupCtrl.setHasValue(hasValue());		
			
			// timestamp <---> Date translation
			scope.$watch("model",function(){
				if (!datesEqual(scope.value,scope.model))
					scope.value = scope.model ? new Date(parseInt(scope.model)) : scope.model;
			},true);
			scope.$watch("value",function(){
				if (!datesEqual(scope.value,scope.model))
					scope.model = scope.value ? scope.value.getTime() : scope.value;
			},true);
			
		    
			scope.focusTextbox = function(){
				setTimeout(function(){document.getElementById(scope.fid).focus()},0);
			}
			
			scope.deactivate = function(){
				scope.focused = false;
				inputGroupCtrl.setFocused(false);
			}
			
			scope.activate = function(){
				scope.focused = true;
				inputGroupCtrl.setFocused(true);
			}
			
			scope.onTextboxFocus = function(){
				scope.textboxFocused = true;
				scope.activate();
			}
			
			scope.onTextboxBlur = function(){
				scope.textboxFocused=false;
				if (!scope.textboxFocused && !scope.mouseEnter) scope.deactivate();
			}
			
			scope.onMouseEnter = function(){
				scope.mouseEnter = true;
			}
			
			scope.onMouseLeave = function(){
				scope.mouseEnter = false;
				if (!scope.textboxFocused && !scope.mouseEnter) scope.deactivate();
			}
		
		},
		
	}
}
MdDatepickerDirective.$inject = ["$mdUtil"];