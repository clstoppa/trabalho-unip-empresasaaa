@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d bin -cp "driverJDBC\sqlite-jdbc-3.36.0.3.jar" @fontes.txt
del fontes.txt
echo Compilado em bin\
pause
