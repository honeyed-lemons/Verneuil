@echo off
setlocal
for /f "tokens=2 delims=:." %%x in ('chcp') do set _codepage=%%x
chcp 65001>nul
cd D:\User\Documents\Projects\Verneuil\run
C:\Users\alex\.jdks\temurin-25.0.3\bin\java.exe @D:\User\Documents\Projects\Verneuil\build\moddev\dataRunClasspath.txt @D:\User\Documents\Projects\Verneuil\build\moddev\dataRunVmArgs.txt -Dfml.modFolders=verneuil%%%%D:\User\Documents\Projects\Verneuil\build\classes\java\main;verneuil%%%%D:\User\Documents\Projects\Verneuil\build\resources\main net.neoforged.devlaunch.Main @D:\User\Documents\Projects\Verneuil\build\moddev\dataRunProgramArgs.txt
if not ERRORLEVEL 0 (  echo Minecraft failed with exit code %ERRORLEVEL%  pause)
chcp %_codepage%>nul
endlocal