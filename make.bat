@echo off
if "%1"=="clean" goto clean

echo Compilando codigo...
if not exist bin mkdir bin
javac -encoding UTF-8 src/*.java -d bin

echo Ejecutando...
echo ----------------------------------------
java -cp bin App
goto end

:clean
echo Limpiando compilados...
if exist bin rmdir /s /q bin

:end