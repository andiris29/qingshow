@echo off
call %ANT_SOFTWARE%\environment.bat
%tool_ant%\bin\ant -f %folder_antSoftware%\com.focosee.chingshow\trunk\dev\web\build\build.xml
