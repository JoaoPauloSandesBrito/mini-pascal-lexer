@echo off
setlocal
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist
dir /s /b src\*.java > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1
jar --create --file dist\MiniPascal-Lexico.jar --main-class br.uesb.compiladores.Main -C build\classes .
if errorlevel 1 exit /b 1
echo Compilacao concluida: dist\MiniPascal-Lexico.jar
