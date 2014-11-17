#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

$folder_mongodb/bin/mongo --port $mongodb_port


###### Create user ######
# use qingshow
# db.addUser('qingshow', 'qingshow@mongo');

###### Shutdown ######
# Use CTRL-C
# 	When running the mongod instance in interactive mode (i.e. without --fork), issue Control-C to perform a clean shutdown.
#
# Use shutdownServer()
# 	Shut down the mongod from the mongo shell using the db.shutdownServer() method as follows
# use admin
# db.shutdownServer();
