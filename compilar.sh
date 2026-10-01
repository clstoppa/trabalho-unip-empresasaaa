#!/bin/sh
# Compila o projeto (Linux/Mac). Requer JDK 8 ou superior.
cd "$(dirname "$0")"
mkdir -p bin
javac -encoding UTF-8 -d bin -cp "driverJDBC/sqlite-jdbc-3.36.0.3.jar" $(find src -name '*.java') && echo "Compilado em bin/"
