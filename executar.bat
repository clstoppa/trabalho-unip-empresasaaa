@echo off
cd /d "%~dp0"
java -cp "bin;driverJDBC\sqlite-jdbc-3.36.0.3.jar" principal.Main
