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
			value: '='
		},
		template: 
			'<div>'+
			'<div ng-show="value" layout="row" style="border-bottom: 1px solid rgba(0, 0, 0, 0.12); padding-top:2px;">'+
			'	<div class="datepicker-value" flex><span>{{value|date:format}}</span></div>'+
			'	<md-button ng-show="value" ng-click="value=null;focusTextbox()" style="padding:0;">'+
			'	 	<md-icon icon="/img/icons/ic_cancel_24px.svg"></md-icon>'+
			' 	</md-button>'+
			'</div>'+
			'<input id="{{fid}}" ng-hide="value" type="date" ng-model="value" ng-focus="onTextboxFocus()" ng-blur="onTextboxBlur()">'+
			'<div ng-show="focused" class="datepicker-container" ng-mouseenter="onMouseEnter()" ng-mouseleave="onMouseLeave()">'+
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

			function hasValue(){
				return !angular.isUndefined(scope.value) && scope.value != null;
			}
			
		    scope.$watch("value",function(){
				inputGroupCtrl.setHasValue(hasValue());
			});
			inputGroupCtrl.setHasValue(hasValue());		    
		    
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