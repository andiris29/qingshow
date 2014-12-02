#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

$folder_mongodb/bin/mongodump -db $mongodb_db --username $mongodb_user --password $mongodb_password --host $mongodb_url_remote:$mongodb_port --out $folder_qingshow/mongodb/_dump/$mongodb_url_remote
