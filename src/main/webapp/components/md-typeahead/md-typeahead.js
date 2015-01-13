angular.module('material.components.typeahead', [
    'material.components.button',
	'material.components.whiteframe',
	'material.components.content',
	'material.components.list',
	'material.components.divider',
	'material.components.icon'
])
.directive('mdTypeahead', MdTypeaheadDirective);

function MdTypeaheadDirective($mdUtil) {
	return {
		restrict: "E",
		replace: true,
		require: ['^?mdInputGroup'],
		scope: {
			fid : '@mdFid',
			selectedItemTemplateUrl: '@',
			itemTemplateUrl: '@',
			source: '=',
			value: '=ngModel'
		},
		template: 
			'<div>'+
			'<div ng-show="value" layout="row" style="border-bottom: 1px solid rgba(0, 0, 0, 0.12); padding-top:2px;">'+
			'	<div ng-include src="selectedItemTemplateUrl" flex></div>'+
			'	<md-button ng-click="value=null;queryString=null;focusTextbox()" style="padding:0; margin-left:4px">'+
			'	 	<i class="mdi-cancel"></i>'+
			' 	</md-button>'+
			'</div>'+
			'<input id="{{fid}}" ng-hide="value" type="text" ng-model="queryString" ng-model-options="{debounce:300}" ng-change="getItems()" ng-focus="onTextboxFocus()" ng-blur="onTextboxBlur()">'+
			'<div class="typeahead-container" ng-show="!value && showSuggest" ng-mouseenter="onMouseEnter()" ng-mouseleave="onMouseLeave()">'+
			'	<md-whiteframe class="md-whiteframe-z1" layout>'+
			'		<md-content>'+
			'		    <md-list>'+
			'		      <md-item class="md-item-padded" ng-repeat="item in items.items track by $index">'+
			'		        <md-item-content ng-click="selectItem(item)" layout="row" layout-align="center left" ng-mouseover="activeItemInfo.index=$index" ng-class="{active:activeItemInfo.index==$index}">'+		        
			'		        	<div ng-include src="itemTemplateUrl"></div>'+
			'		        </md-item-content>'+
			'		        <md-divider ng-if="!$last"></md-divider>'+
			'		      </md-item>'+
			'		    </md-list>'+
			'		  </md-content>'+
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
		    
			scope.activeItemInfo = {
				index: -1
			};
			
			
			
			scope.getItems = function(){
				scope.items = scope.source(scope.queryString);
			}
			
			scope.selectItem = function(item) {
				scope.value = item;
			}
			
			scope.focusTextbox = function(){
				setTimeout(function(){document.getElementById(scope.fid).focus()},0);
			}
			
			scope.deactivate = function(){
				scope.queryString="";
				scope.showSuggest = false;
				inputGroupCtrl.setFocused(false);
			}
			
			scope.activate = function(){
				scope.queryString="";
				inputGroupCtrl.setFocused(true);
				scope.showSuggest = true;
				scope.getItems();
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
				scope.activeItemInfo.index = -1;
				if (!scope.textboxFocused && !scope.mouseEnter) scope.deactivate();
			}
		
		},
		
	}
}
MdTypeaheadDirective.$inject = ["$mdUtil"];