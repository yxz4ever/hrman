@echo off
chcp 65001 > nul
echo 正在启动人力资源管理系统后端服务...
echo.
cd /d "%~dp0backend"
call mvn spring-boot:run -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
pause