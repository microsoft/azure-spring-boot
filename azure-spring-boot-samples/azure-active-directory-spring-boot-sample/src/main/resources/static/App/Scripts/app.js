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
                tenant: 'xxxorg.onmicrosoft.com',
                clientId: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
                extraQueryParameter: 'nux=1',
                cacheLocation: 'localStorage',
                postLogoutRedirectUri: 'http://localhost:8080/logout',
            },
            $httpProvider
        );

    }]);
