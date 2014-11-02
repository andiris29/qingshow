#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

# node-inspector --web-port=8084 &
# http://127.0.0.1:8084/debug?port=5858
#
# node
# node --debug
# node --debug-brk
node $folder_qingshow/web/lib/startup.js --web-server-port $web_server_port
read -n 1