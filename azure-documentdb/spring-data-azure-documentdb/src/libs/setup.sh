# setup test resources
#!/bin/bash
resourcegroup=$1
dbname=$2

if [ -z "$CLIENT_ID" ]; then
    exit 0
fi
if [ -z "$CLIENT_KEY" ]; then
    exit 0
fi
if [ -z "$TENANT_ID" ]; then
    exit 0
fi

az login --service-principal -u $CLIENT_ID -p $CLIENT_KEY --tenant $TENANT_ID >> tmp.txt
documentDbUri=$(az cosmosdb create --name $dbname --resource-group $resourcegroup --kind GlobalDocumentDB --query documentEndpoint)
documentDbKey=$(az cosmosdb list-keys --name $dbname --resource-group $resourcegroup --query primaryMasterKey)

echo $documentDbKey
