@echo off
call build.bat
if errorlevel 1 exit /b 1
if not exist build\test-classes mkdir build\test-classes
dir /s /b testes\*.java > build\test-sources.txt
javac -encoding UTF-8 -cp build\classes -d build\test-classes @build\test-sources.txt
if errorlevel 1 exit /b 1
java -ea -cp build\classes;build\test-classes br.uesb.compiladores.LexerSelfTest
if errorlevel 1 exit /b 1
echo Testes concluidos com sucesso.
