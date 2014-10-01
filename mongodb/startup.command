#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

$folder_mongodb/bin/mongod --dbpath=$folder_qingshow/mongodb/db --port $mongodb_port --auth
