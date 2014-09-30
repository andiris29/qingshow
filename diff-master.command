#!/bin/bash
source /Users/i068020/Documents/_git/antSoftware/com.focosee.qingshow/environment.sh

cd $folder_qingshow
for name in $(git diff origin/master --name-only $1); do git difftool origin/master $1 $name & done
