#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

# node-inspector --web-port=8084 &
# http://127.0.0.1:8084/debug?port=5858
#
# node
# node --debug
# node --debug-brk
node --debug $folder_qingshow/server-node/lib/httpserver/startup.js --app-server-port $app_server_port --mongodb-connect $mongodb_url,$mongodb_port,$mongodb_db --mongodb-auth $mongodb_user,$mongodb_password --uploads $folder_qingshow_uploads,$http_server_uploads
read -n 1