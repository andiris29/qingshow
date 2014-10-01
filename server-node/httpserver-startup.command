#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

# node-inspector --web-port=8084 &
# http://127.0.0.1:8084/debug?port=5858
#
# node
# node --debug
# node --debug-brk
node --debug-brk $folder_qingshow/server-node/lib/httpserver/startup.js --http-server-port $http_server_port --mongodb-connect $mongodb_url,$mongodb_port,$mongodb_db --mongodb-auth $mongodb_user,$mongodb_password
read -n 1