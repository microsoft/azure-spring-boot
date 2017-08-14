'use strict';
angular.module('todoApp', ['ngRoute', 'AdalAngular'])
    .config(['$routeProvider', '$httpProvider', 'adalAuthenticationServiceProvider', function ($routeProvider, $httpProvider, adalProvider) {

        $routeProvider.when("/Home", {
            controller: "homeCtrl",
            templateUrl: "/App/Views/Home.html",
        }).when("/TodoList", {
            controller: "todoListCtrl",
            templateUrl: "/App/Views/TodoList.html",
            requireADLogin: true,
        }).when("/UserData", {
            controller: "userDataCtrl",
            templateUrl: "/App/Views/UserData.html",
        }).otherwise({redirectTo: "/Home"});

        adalProvider.init(
            {
                instance: 'https://login.microsoftonline.com/',
                //tenant: '72f988bf-86f1-41af-91ab-2d7cd011db47',// <- microsoft.onmicrosoft.com
                tenant: 'yaweiworg.onmicrosoft.com',
                //clientId: '91a13120-1e25-4f9f-ab72-1b7dd7a72346',// <- microsoft.onmicrosoft.com
                clientId: 'b8a06830-2bd3-41c1-8044-123ebd907d57',// <- yaweiworg.onmicrosoft.com
                extraQueryParameter: 'nux=1',
                cacheLocation: 'localStorage', // enable this for IE, as sessionStorage does not work for localhost.
            },
            $httpProvider
        );

    }]);
