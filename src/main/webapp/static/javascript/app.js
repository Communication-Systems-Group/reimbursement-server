angular.module('reimbursement.templates', ['cropping/cropping.tpl.html', 'csrfTestingPage/csrfTestingPage.tpl.html', 'dashboard/dashboard.tpl.html', 'login/login.tpl.html', 'signature/signature-pad.directive.tpl.html', 'signature/signature-qr-error.tpl.html', 'signature/signature-qr.tpl.html', 'signature/signature.tpl.html', 'spinner/spinner.directive.tpl.html']);

angular.module('cropping/cropping.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('cropping/cropping.tpl.html',
    '<div id="signatureCrop"> <h1 class="page-header"> <i class="fa fa-crop"></i> {{ \'reimbursement.cropSignature.title\' | translate }} </h1> <div class="page"> <spinner id="spinnerCroppingSubmit" label="{{ \'reimbursement.loading.label\' | translate }}"></spinner> <div> <img crop id="croppingImage" dimensions="dimensions" ng-src="{{ imageUri }}"> </div> <div class="buttons"> <button class="btn btn-primary" ng-click="submit()" ng-disabled="!hasDimensions"> <i class="fa fa-check"></i> {{ \'reimbursement.cropSignature.submit\' | translate }} </button> </div> </div> </div>');
}]);

angular.module('csrfTestingPage/csrfTestingPage.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('csrfTestingPage/csrfTestingPage.tpl.html',
    '<div id="login"> <h1 class="page-header"> <i class="fa fa-sign-in"></i> CSRF Testing Page </h1> <h3>Login</h3> <div class="alert alert-success" ng-show="loginSent && loginSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="loginSent && !loginSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <label for="inputUsername" class="col-sm-4 control-label">{{ \'reimbursement.login.usernameLabel\' | translate }}</label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputUsername" ng-model="form.username" placeholder="{{ \'reimbursement.login.usernameLabel\' | translate }}"> </div> </div> <div class="form-group"> <label for="inputPassword" class="col-sm-4 control-label">{{ \'reimbursement.login.passwordLabel\' | translate }}</label> <div class="col-sm-5"> <input type="password" class="form-control" id="inputPassword" ng-model="form.password" placeholder="{{ \'reimbursement.login.passwordLabel\' | translate }}"> </div> </div> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="submit()"> {{ \'reimbursement.login.submitLabel\' | translate }} </button> </div> </div> </form> <h3>Public getUsers</h3> <div class="alert alert-success" ng-show="getUsersSent && getUsersSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="getUsersSent && !getUsersSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="getUsers()"> get Public Users </button> </div> </div> </form> <h3>Private getUsers</h3> <div class="alert alert-success" ng-show="getPrivateUsersSent && getPrivateUsersSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="getPrivateUsersSent && !getPrivateUsersSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="getPrivateUsers()"> get Private Users </button> </div> </div> </form> <h3>Public sendString</h3> <div class="alert alert-success" ng-show="sendStringSent && sendStringSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="sendStringSent && !sendStringSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <label for="inputString" class="col-sm-4 control-label">String: </label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputString" ng-model="stringform.string" placeholder="some random string"> </div> </div> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="sendString()"> send String </button> </div> </div> </form> <h3>Public send CroppingDto</h3> <div class="alert alert-success" ng-show="sendCroppingDtoSent && sendCroppingDtoSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="sendCroppingDtoSent && !sendCroppingDtoSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <label for="inputCroppingDtoHeight" class="col-sm-4 control-label">Height: </label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputCroppingDtoHeight" ng-model="croppingDtoForm.height" placeholder="some random string"> </div> </div> <div class="form-group"> <label for="inputCroppingDtoWidth" class="col-sm-4 control-label">Width: </label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputCroppingDtoWidth" ng-model="croppingDtoForm.width" placeholder="some random string"> </div> </div> <div class="form-group"> <label for="inputCroppingDtoTop" class="col-sm-4 control-label">Top: </label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputCroppingDtoTop" ng-model="croppingDtoForm.top" placeholder="some random string"> </div> </div> <div class="form-group"> <label for="inputCroppingDtoLeft" class="col-sm-4 control-label">Left: </label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputCroppingDtoLeft" ng-model="croppingDtoForm.left" placeholder="some random string"> </div> </div> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="sendCroppingDto()"> send String </button> </div> </div> </form> </div>');
}]);

angular.module('dashboard/dashboard.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('dashboard/dashboard.tpl.html',
    '<div id="dashboard"> <h1 class="page-header">{{ \'reimbursement.dashboard.title\' | translate }}</h1> {{ dashboard }} </div>');
}]);

angular.module('login/login.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('login/login.tpl.html',
    '<div id="login"> <h1 class="page-header"> <i class="fa fa-sign-in"></i> {{ \'reimbursement.login.title\' | translate }} </h1> <div class="alert alert-success" ng-show="loginSent && loginSuccess"><i class="fa fa-thumbs-o-up"></i> Very good Sir!</div> <div class="alert alert-danger" ng-show="loginSent && !loginSuccess"><i class="fa fa-thumbs-o-down"></i> Not good, duckhead...</div> <form class="form-horizontal"> <div class="form-group"> <label for="inputUsername" class="col-sm-4 control-label">{{ \'reimbursement.login.usernameLabel\' | translate }}</label> <div class="col-sm-5"> <input type="text" class="form-control" id="inputUsername" ng-model="form.username" placeholder="{{ \'reimbursement.login.usernameLabel\' | translate }}"> </div> </div> <div class="form-group"> <label for="inputPassword" class="col-sm-4 control-label">{{ \'reimbursement.login.passwordLabel\' | translate }}</label> <div class="col-sm-5"> <input type="password" class="form-control" id="inputPassword" ng-model="form.password" placeholder="{{ \'reimbursement.login.passwordLabel\' | translate }}"> </div> </div> <div class="form-group"> <div class="col-sm-9"> <button class="btn btn-primary pull-right" ng-click="submit()"> {{ \'reimbursement.login.submitLabel\' | translate }} </button> </div> </div> </form> </div>');
}]);

angular.module('signature/signature-pad.directive.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('signature/signature-pad.directive.tpl.html',
    '<div class="signaturePad"> <div class="pad"> <canvas ng-style="{width: signatureWidth, height: signatureHeight}"></canvas> </div> <div class="buttons"> <button class="btn btn-default" ng-click="clearSignature()"> <i class="fa fa-ban"></i> {{ "reimbursement.captureSignature.touch.reset" | translate }} </button> <button class="btn btn-primary" ng-click="submitSignature()"> {{ "reimbursement.captureSignature.nextStep" | translate }} </button> </div> </div>');
}]);

angular.module('signature/signature-qr-error.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('signature/signature-qr-error.tpl.html',
    '<div id="signatureCaptureQRError"> <div class="modal-header"> <div class="close" ng-click="dismiss()"></div> <h3 class="modal-title"> <i class="fa fa-warning"></i> {{ \'reimbursement.captureSignature.qr.error.title\' | translate }} </h3> </div> <div class="modal-body"> {{ \'reimbursement.captureSignature.qr.error.message\' | translate }} </div> <div class="modal-footer"> <button class="btn btn-primary" ng-click="dismiss()"> {{ \'reimbursement.captureSignature.qr.error.okayButton\' | translate }} </button> </div> </div>');
}]);

angular.module('signature/signature-qr.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('signature/signature-qr.tpl.html',
    '<div id="signatureCaptureQR"> <spinner id="spinnerSignatureQR" label="{{ \'reimbursement.loading.label\' | translate }}"></spinner> <div class="modal-header"> <a class="close" ng-click="dismiss()">&times;</a> <h3 class="modal-title">{{ \'reimbursement.captureSignature.qr.title\' | translate }}</h3> </div> <div class="modal-body"> {{ \'reimbursement.captureSignature.qr.info\' | translate }} <div class="qrCodeWrapper"> <div class="qrUrl">{{ qrUrl }}</div> <div class="or">{{ \'reimbursement.captureSignature.qr.or\' | translate }}</div> <qrcode data="{{ qrUrl }}" size="300"></qrcode> </div> </div> <div class="modal-footer"> <button class="btn btn-default" ng-click="dismiss()"> <i class="fa fa-times"></i> {{ \'reimbursement.captureSignature.qr.cancel\' | translate }} </button> <button class="btn btn-primary" ng-click="checkAndClose()"> <i class="fa fa-check"></i> {{ \'reimbursement.captureSignature.qr.capturedOnMobile\' | translate }} </button> </div> </div>');
}]);

angular.module('signature/signature.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('signature/signature.tpl.html',
    '<div id="signatureCapture"> <h1 class="page-header"><i class="fa fa-pencil-square-o"></i> {{ \'reimbursement.captureSignature.title\' | translate }}</h1> <div class="well">{{ \'reimbursement.captureSignature.info\' | translate }}</div> <div class="col-lg-8 col-lg-offset-2"> <ul class="nav nav-tabs"> <li role="captureMethod" ng-class="{active: showTouchInput}"> <a href ng-click="selectTouchTab()">{{ \'reimbursement.captureSignature.touch.tab\' | translate }}</a> </li> <li role="captureMethod" ng-class="{active: showUploadImage}"> <a href ng-click="selectUploadTab()">{{ \'reimbursement.captureSignature.upload.tab\' | translate }}</a> </li> </ul> <form id="signatureCaptureImage" ng-show="showUploadImage"> <spinner id="spinnerSignatureImage" label="{{ \'reimbursement.captureSignature.spinnerUploading\' | translate }}"></spinner> <div flow-init="{\n' +
    '				target: postSignaturePath,\n' +
    '				testChunks: false,\n' +
    '				singleFile: true,\n' +
    '				simultaneousUploads: 1\n' +
    '			}" flow-name="flow.image" flow-files-submitted="$flow.upload()" flow-upload-started="showSpinner(\'spinnerSignatureImage\')" flow-complete="getImageAndGoToNextPage()"> <div class="image-upload"> <div flow-drop flow-btn ng-class="dropClass" flow-drag-enter="dropClass=\'dragOver\'" flow-drag-leave="dropClass=\'\'"> <div class="center uploadButton"> <i class="fa fa-plus-circle"></i> {{ \'reimbursement.captureSignature.upload.centerText\' | translate }} </div> </div> </div> </div> </form> <form id="signatureCaptureTouch" ng-show="showTouchInput"> <spinner id="spinnerSignatureTouch" label="{{ \'reimbursement.captureSignature.spinnerUploading\' | translate }}"></spinner> <div ng-show="!Modernizr.touch && !forceSignaturePad"> <div class="alert alert-warning"> {{ \'reimbursement.captureSignature.touch.warning\' | translate }} <div class="buttons"> <button class="btn btn-warning" ng-click="forceSignaturePad = true"> <i class="fa fa-exclamation-circle"></i> {{ \'reimbursement.captureSignature.touch.tryAnyway\' | translate }} </button> <button class="btn btn-primary" ng-click="showQR()"> <i class="fa fa-tablet"></i> {{ \'reimbursement.captureSignature.touch.tryMobile\' | translate }} </button> </div> </div> </div> <div ng-show="Modernizr.touch || forceSignaturePad"> <div class="resetForceSignature" ng-show="forceSignaturePad" ng-click="forceSignaturePad = false"> <i class="fa fa-mobile"></i> </div> <signature-pad submit="submitTouch"></signature-pad> <div flow-init="{\n' +
    '					target: postSignaturePath,\n' +
    '					testChunks: false,\n' +
    '					singleFile: true,\n' +
    '					simultaneousUploads: 1\n' +
    '				}" flow-name="flow.touch" flow-upload-started="showSpinner(\'spinnerSignatureTouch\')" flow-complete="getImageAndGoToNextPage()"> </div> </div> </form> </div> </div>');
}]);

angular.module('spinner/spinner.directive.tpl.html', []).run(['$templateCache', function($templateCache) {
  $templateCache.put('spinner/spinner.directive.tpl.html',
    '<div class="spinner" ng-class="{showSpinner: showSpinner}"> <div class="whiteBackground"></div> <div class="spin"> <div class="rect rect1"></div> <div class="rect rect2"></div> <div class="rect rect3"></div> <div class="rect rect4"></div> <div class="rect rect5"></div> <div class="textLabel" ng-hide="label === \'\'"> {{ label }} </div> </div> </div>');
}]);

(function initializeLanguagesBeforeApplicationStart() {
	"use strict";

	deferredBootstrapper.bootstrap({
		element : document.body,
		module : 'reimbursement',
		resolve : {
			LANGUAGES : ['$http',
			function($http) {
				return $http.get('./languages/languages.json');
			}]

		}
	});
})();

var app = angular.module('reimbursement', ['reimbursement.templates', 'ui.router', 'ui.bootstrap', 'pascalprecht.translate', 'monospaced.qrcode', 'flow', 'ngCookies']);

app.constant("Modernizr", Modernizr);
app.config(['$stateProvider', '$urlRouterProvider', '$translateProvider', '$locationProvider', 'LANGUAGES', 'flowFactoryProvider' ,
function($stateProvider, $urlRouterProvider, $translateProvider, $locationProvider, LANGUAGES, flowFactoryProvider) {
	"use strict";

	for (var key in LANGUAGES) {
		if (LANGUAGES.hasOwnProperty(key)) {
			$translateProvider.translations(key, LANGUAGES[key]);
		}
	}
	$translateProvider.preferredLanguage('en');

	$stateProvider.state('signature', {
		url : "/signature",
		templateUrl : "signature/signature.tpl.html",
		controller : "SignatureController"
	}).state('cropping', {
		url : "/cropping",
		params : {
			imageUri : null
		},
		templateUrl : "cropping/cropping.tpl.html",
		controller : "CroppingController"
	}).state('dashboard', {
		url : "/dashboard",
		templateUrl : "dashboard/dashboard.tpl.html",
		controller : "DashboardController"
	}).state('login', {
		url : "/login",
		templateUrl : "login/login.tpl.html",
		controller: 'LoginController'
	}).state('csrfTestingPage', {
		url : "/csrfTestingPage",
		templateUrl : "csrfTestingPage/csrfTestingPage.tpl.html",
		controller: 'CsrfTestingPageController'
	});
	$urlRouterProvider.otherwise('/signature');

	$locationProvider.hashPrefix("!");
	
	// ng-Flow flow.js configuration
	var cookies;
	  angular.injector(['ngCookies']).invoke(function(_$cookies_) {
	    cookies = _$cookies_;
	  });
	flowFactoryProvider.defaults = {
			headers: function () { //file, chunk, isTest
				return {
				'X-XSRF-TOKEN': cookies['XSRF-TOKEN']
				};
			}
	    };

}]);
app.factory('base64BinaryConverterService', [

function() {
	"use strict";

	return {
		toBase64: function(binary, callback) {
			var fileReader = new window.FileReader();
			fileReader.onload = function() {
				callback(fileReader.result);
			};
			fileReader.readAsDataURL(binary);
		},
		toBinary: function(base64) {
			var type = base64.split(',')[0].split(':')[1].split(";base64")[0];
			var fileEnding = type.split('image/')[1];

			// taken from: http://stackoverflow.com/a/14988118/3233827
			var binaryData = window.atob(base64.split(',')[1]);
			var binaryLength = binaryData.length;
			var arrayBuffer = new window.ArrayBuffer(binaryLength);
			var uint8Array = new window.Uint8Array(arrayBuffer);

			for(var i=0; i<binaryLength; i++) {
				uint8Array[i] = binaryData.charCodeAt(i);
			}

			var blob = new window.Blob([uint8Array], {type: type});
			blob.lastModifiedDate = new Date();
			blob.name = new Date().toUTCString() + "." + fileEnding;

			var file = blob;
			return file;
		}
	};

}]);
app.factory('loginRestService', ['$http',

function($http) {
	"use strict";

	return {
		postLogin: function(data) {
			return $http({
				method: 'POST',
				url: 'http://localhost:8080/api/login',
				data: jQuery.param(data),
				headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			});
		}
	};

}]);

app.controller('LoginController', ['$scope', 'loginRestService',

function($scope, loginRestService) {
	"use strict";

	$scope.form = {
		username: null,
		password: null
	};

	$scope.loginSent = false;
	$scope.loginSuccess = false;
	$scope.submit = function() {
		loginRestService.postLogin($scope.form).then(function() {
			$scope.loginSuccess = true;
		}, function() {
			$scope.loginSuccess = false;
		})['finally'](function() {
			$scope.loginSent = true;
		});
	};

}]);

app.factory('csrfTestingPageRestService', ['$http',

function($http) {
	"use strict";

	return {
		postLogin: function(data) {
			return $http({
				method: 'POST',
				url: 'http://localhost:8080/api/login',
				data: jQuery.param(data),
				headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			});
		},
		getUsers: function() {
			return $http({
				method: 'GET',
				url: 'http://localhost:8080/testingpublic'
			});
		},
		getPrivateUsers: function() {
			return $http({
				method: 'GET',
				url: 'http://localhost:8080/testingprivate'
			});
		},
		sendString: function(data) {
			return $http({
				params: data,
				method: 'POST',
				url: 'http://localhost:8080/testingpublic/string'
			});
		},
		sendCroppingDto: function(data) {
			return $http({
				method: 'POST',
				url: 'http://localhost:8080/testingpublic/croppingdto',
				data: data,
				headers: {'Content-Type': "application/json"}
			});
		}
		
	};

}]);

app.controller('CsrfTestingPageController', ['$scope', 'csrfTestingPageRestService',

function($scope, csrfTestingPageRestService) {
	"use strict";

	$scope.form = {
		username: null,
		password: null
	};
	
	$scope.stringform = {
		string: null
	};
	
	$scope.croppingDtoForm = {
		width: null,
		height: null,
		top: null,
		left: null	
	};

	$scope.loginSent = false;
	$scope.loginSuccess = false;
	$scope.submit = function() {
		csrfTestingPageRestService.postLogin($scope.form).then(function() {
			$scope.loginSuccess = true;
		}, function() {
			$scope.loginSuccess = false;
		})['finally'](function() {
			$scope.loginSent = true;
		});
	};
	

	
	$scope.getUsersSent = false;
	$scope.getUsersSuccess = false;
	$scope.getUsers = function (){
		csrfTestingPageRestService.getUsers().then(function() {
			$scope.getUsersSuccess = true;
		}, function() {
			$scope.getUsersSuccess = false;
		})['finally'](function() {
			$scope.getUsersSent = true;
		});
	};
	
	$scope.	getPrivateUsersSent = false;
	$scope.	getPrivateUsersSuccess = false;
	$scope.	getPrivateUsers = function (){
		csrfTestingPageRestService.	getPrivateUsers().then(function() {
			$scope.getPrivateUsersSuccess = true;
		}, function() {
			$scope.getPrivateUsersSuccess = false;
		})['finally'](function() {
			$scope.getPrivateUsersSent = true;
		});
	};
	
	$scope.sendStringSent = false;
	$scope.sendStringSuccess = false;
	$scope.sendString = function (){
		csrfTestingPageRestService.sendString($scope.stringform).then(function() {
			$scope.sendStringSuccess = true; 
		}, function() {
			$scope.sendStringSuccess = false;
		})['finally'](function() {
			$scope.sendStringSent = true;
		});
	};
	
	$scope.sendCroppingDtoSent = false;
	$scope.sendCroppingDtoSuccess = false;
	$scope.sendCroppingDto = function (){
		csrfTestingPageRestService.sendCroppingDto($scope.croppingDtoForm).then(function() {
			$scope.sendCroppingDtoSuccess = true; 
		}, function() {
			$scope.sendCroppingDtoSuccess = false;
		})['finally'](function() {
			$scope.sendCroppingDtoSent = true;
		});
	};

}]);

app.factory('spinnerService', [

function() {
	"use strict";

	var cache = {};

	return {
		// private method for spinner directive
		_register: function(spinnerScope) {
			if(!spinnerScope.id) {
				throw new Error("A spinner must have an ID to register with the spinner service!");
			}
			cache[spinnerScope.id] = spinnerScope;
		},

		show: function(spinnerId) {
			if(cache.hasOwnProperty(spinnerId)) {
				var spinnerScope = cache[spinnerId];
				spinnerScope.showSpinner = true;
			}
		},

		hide: function(spinnerId) {
			if(cache.hasOwnProperty(spinnerId)) {
				var spinnerScope = cache[spinnerId];
				spinnerScope.showSpinner = false;
			}
		},

		// useful for global error handler
		hideAll: function() {
			for(var spinnerId in cache) {
				if(cache.hasOwnProperty(spinnerId)) {
					var spinnerScope = cache[spinnerId];
					spinnerScope.showSpinner = false;
				}
			}
		}
	};

}]);
app.directive('spinner', [

function() {
	"use strict";

	return {
		restrict : 'E',
		replace : true,
		templateUrl : 'spinner/spinner.directive.tpl.html',
		scope : {
			id : '@',
			label : "@?",
			showSpinner : "@?"
		},
		controller : ['$scope', '$attrs', 'spinnerService',
		function($scope, $attrs, spinnerService) {
			if ( typeof $scope.label === 'undefined') {
				$scope.label = '';
			}
			spinnerService._register($scope);
		}]

	};

}]);
app.factory("signatureRestService", ['$http',

function($http) {
	"use strict";

	//TODO change absolute file path
	return {
		getSignature : function() {
			return $http.get("http://localhost:8080/api/user/test-uuid/signature", {
				responseType : 'blob'
			});
		},
		postSignaturePath : function() {
			return "http://localhost:8080/api/user/test-uuid/signature";
		}
	};

}]);
app.controller('SignatureQRErrorController', ['$scope', '$modalInstance',

function($scope, $modalInstance) {
	"use strict";

	$scope.dismiss = $modalInstance.dismiss;

}]);
app.controller('SignatureQRController', ['$scope', '$modalInstance', '$modal', 'signatureRestService', 'spinnerService',

function($scope, $modalInstance, $modal, signatureRestService, spinnerService) {
	"use strict";

	$scope.qrUrl = window.location.href;
	$scope.dismiss = $modalInstance.dismiss;

	$scope.checkAndClose = function() {
		spinnerService.show('spinnerSignatureQR');

		var promise = signatureRestService.getSignature();
		promise.then(function(image) {
			$modalInstance.close(image);
		}, function() {
			$modal.open({
				templateUrl: 'signature/signature-qr-error.tpl.html',
				controller: 'SignatureQRErrorController',
				size: 'sm'
			});
		})['finally'](function() {
			spinnerService.hide('spinnerSignatureQR');
		});

	};

}]);
app.directive('signaturePad', ['$window', '$timeout', 'base64BinaryConverterService',

function($window, $timeout, base64BinaryConverterService) {
	"use strict";

	return {
		restrict : 'E',
		replace : false,
		templateUrl : 'signature/signature-pad.directive.tpl.html',
		scope : {
			submit : "="
		},
		link : function($scope, $element) {
			var canvas = $element.find("canvas");
			var signaturePad = new SignaturePad(canvas[0]);

			setCanvasSize();

			$scope.clearSignature = function() {
				signaturePad.clear();
				setCanvasSize();
			};

			$scope.submitSignature = function() {
				if (!signaturePad.isEmpty()) {
					var dataBase64 = signaturePad.toDataURL();
					var file = convertBase64ToFile(dataBase64);

					// TODO sebi | find out why it is necessary to have $timeout here.
					// removing it leads to "Error: [$rootScope:inprog] $apply already in progress"
					$timeout(function() {
						$scope.submit(file);
					});
				}
			};

			function convertBase64ToFile(dataBase64) {
				return base64BinaryConverterService.toBinary(dataBase64);
			}

			function setCanvasSize() {
				var width1 = parseInt(jQuery('#signatureCaptureImage').css('width'), 10);
				var width2 = parseInt(jQuery('#signatureCaptureTouch').css('width'), 10);

				var width = Math.max(width1, width2);
				var height = width * 1 / 3;

				canvas.attr('width', width);
				canvas.attr('height', height);
				$scope.signatureWidth = width;
				$scope.signatureHeight = height;
			}


			$window.addEventListener("resize", $scope.clearSignature, false);
			$window.addEventListener("orientationchange", $scope.clearSignature, false);
		}
	};

}]);
app.controller('SignatureController', ['$scope', '$state', '$modal', 'Modernizr', 'spinnerService', 'signatureRestService', 'base64BinaryConverterService',

function($scope, $state, $modal, Modernizr, spinnerService, signatureRestService, base64BinaryConverterService) {
	"use strict";

	$scope.postSignaturePath = signatureRestService.postSignaturePath();
	$scope.Modernizr = Modernizr;
	$scope.showUploadImage = false;
	$scope.showTouchInput = true;
	$scope.forceSignaturePad = false;
	$scope.flow = {};

	$scope.selectTouchTab = function() {
		$scope.showUploadImage = false;
		$scope.showTouchInput = true;
	};
	$scope.selectUploadTab = function() {
		$scope.showUploadImage = true;
		$scope.showTouchInput = false;
	};

	$scope.submitTouch = function(file) {
		$scope.flow.touch.addFile(file);
		$scope.flow.touch.upload();
	};

	$scope.showSpinner = function(spinnerId) {
		spinnerService.show(spinnerId);
	};

	$scope.showQR = function() {
		var modalInstance = $modal.open({
			templateUrl : 'signature/signature-qr.tpl.html',
			controller : 'SignatureQRController'
		});

		modalInstance.result.then(function(response) {
			base64BinaryConverterService.toBase64(response.data, goToNextPage);
		});
	};

	$scope.getImageAndGoToNextPage = function() {
		var fileWrapper = $scope.flow.image.files[0] || $scope.flow.touch.files[0];
		base64BinaryConverterService.toBase64(fileWrapper.file, goToNextPage);
	};

	function goToNextPage(base64Image) {
		spinnerService.hide('spinnerSignatureImage');
		spinnerService.hide('spinnerSignatureTouch');

		$state.go('cropping', {
			imageUri : base64Image
		});
	}

}]);
app.directive('crop', [

function() {
	"use strict";

	return {
		restrict: 'A',
		scope: {
			id: '=',
			dimensions: '='
		},
		link: function($scope, $element, attrs) {
			$scope.dimensions = {};

			function storeCoords(coords) {
				$scope.dimensions = {
					width: coords.w,
					height: coords.h,
					top: coords.y,
					left: coords.x
				};
				$scope.$apply();
			}

			var boxWidth = parseInt(jQuery($element).parent().css('width'), 10);

			jQuery('#' + attrs.id).Jcrop({
				onChange: storeCoords,
				onSelect: storeCoords,
				boxWidth: boxWidth
			});
		}
	};

}]);
app.factory('croppingRestService', ['$http',

function($http) {
	"use strict";

	return {
		postSignatureCropping : function(dimensions) {
			return $http.post("http://localhost:8080/user/test-uuid/signature/crop", dimensions);
		}
	};

}]);

app.controller('CroppingController', ['$scope', '$stateParams', '$state', 'spinnerService', 'croppingRestService',

function($scope, $stateParams, $state, spinnerService, croppingRestService) {
	"use strict";

	if ($stateParams.imageUri === null) {
		// Illegal state: Cropping needs an image as parameter.
		$state.go("signature", {}, {
			location : "replace"
		});
	}
	$scope.imageUri = $stateParams.imageUri;

	$scope.dimensions = {};
	$scope.hasDimensions = false;

	$scope.$watch('dimensions', function() {
		if ( typeof $scope.dimensions.width === "undefined" || $scope.dimensions.width < 40 || $scope.dimensions.height < 30) {
			$scope.hasDimensions = false;
		} else {
			$scope.hasDimensions = true;
		}
	});

	$scope.submit = function() {
		if ($scope.hasDimensions) {
			spinnerService.show('spinnerCroppingSubmit');

			croppingRestService.postSignatureCropping($scope.dimensions).then(function() {
			goToNextPage();
			}, function() {
			// TODO sebi | exception?
			})['finally'](function() {
				spinnerService.hide('spinnerCroppingSubmit');
			});
		}
	};

	function goToNextPage() {
		$state.go('dashboard');
	}

}]);

app.controller('DashboardController', ['$scope',

function($scope) {
	"use strict";

	$scope.dashboard = "This is the dashboard :))";

}]);