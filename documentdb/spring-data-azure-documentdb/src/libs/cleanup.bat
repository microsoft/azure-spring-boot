@echo off
rem cleanup test resource

set clientId=%CLIENT_ID%
set clientKey=%CLIENT_KEY%
set tenantId=%TENANT_ID%

if "%clientId%" == "" (
goto :end
)
if "%clientKey%" == "" (
goto :end
)
if "%tenantId%" == "" (
goto :end
)

set resourcegroup=%1
set dbname=%2

if "%resourcegroup" == "" (
goto :end
)
if "%dbname" == "" (
goto :end
)

call az login --service-principal -u %clientId% -p %clientKey% --tenant %tenantId% >> tmp.txt
call az cosmosdb delete --name %dbname% --resource-group %resourcegroup%

:end
