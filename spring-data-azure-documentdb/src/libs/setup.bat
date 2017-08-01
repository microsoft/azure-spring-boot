@echo off
set clientId=%CLIENT_ID%
set clientKey=%CLIENT_KEY%
set tenantId=%TENANT_ID%

if "%clientId%" == "" (
goto :noSetup
)
if "%clientKey%" == "" (
goto :noSetup
)
if "%tenantId%" == "" (
goto :noSetup
)

set resourcegroup=%1
set dbname=%2

if "%resourcegroup" == "" (
goto :noSetup
)
if "%dbname" == "" (
goto :noSetup
)

call az login --service-principal -u %clientId% -p %clientKey% --tenant %tenantId% >> null
set createcmd='az cosmosdb create --name %dbname% --resource-group %resourcegroup% --kind GlobalDocumentDB --query documentEndpoint'

for /f "tokens=*" %%a in (%createcmd%) do (set documentdburi=%%a)

set listcmd='az cosmosdb list-keys --name %dbname% --resource-group %resourcegroup% --query primaryMasterKey'
for /f "tokens=*" %%a in (%listcmd%) do (set documentdbkey=%%a)
echo %documentdbkey%

goto :end

:noSetup
echo not to setup test resources
exit 0

:end