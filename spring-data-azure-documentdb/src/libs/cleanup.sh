# cleanup test resources
#!/bin/bash
resourcegroup=$1
dbname=$2

if [ "$CLIENT_ID" == "" ]; then
    exit 0
fi
if [ "$CLIENT_KEY" == "" ]; then
    exit 0
fi
if [ "$TENANT_ID" == "" ]; then
    exit 0
fi

az login --service-principal -u $CLIENT_ID -p $CLIENT_KEY --tenant $TENANT_ID >> tmp.txt
az cosmosdb delete --name $dbname --resource-group $resourcegroup
