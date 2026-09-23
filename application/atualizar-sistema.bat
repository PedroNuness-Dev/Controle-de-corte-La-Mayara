@echo off
setlocal enabledelayedexpansion
title Atualizacao do Controle de Corte

set COMPOSE_BAKE=false

echo =====================================
echo      ATUALIZANDO A APLICACAO
echo =====================================

if not exist backups mkdir backups
if not exist logs mkdir logs

for /f %%i in ('powershell -command "Get-Date -Format yyyy-MM-dd_HH-mm-ss"') do set DATA=%%i

echo.
echo Criando backup do banco...
docker exec mysql-container-controle-de-corte mysqldump -u root -p237081 controle-de-corte-bd > backups\backup_%DATA%.sql 2> logs\backup_erro.log

IF %ERRORLEVEL% NEQ 0 (
    echo Erro ao criar backup. Veja logs\backup_erro.log
    pause
    exit /b
)

echo.
echo Baixando ultima versao do GitHub...
git pull origin main > logs\git.log 2>&1

IF %ERRORLEVEL% NEQ 0 (
    echo Erro ao atualizar codigo. Veja logs\git.log
    pause
    exit /b
)

echo.
echo Construindo nova versao (sistema atual continua no ar)...

del /q logs\build_done.flag 2>nul
del /q logs\_run_build.bat 2>nul

(
echo @echo off
echo set COMPOSE_BAKE=false
echo docker compose build ^> logs\docker_build.log 2^>^&1
echo echo %%ERRORLEVEL%% ^> logs\build_done.flag
) > logs\_run_build.bat

start "" /b cmd /c logs\_run_build.bat

set dots=
:loop_build
if exist logs\build_done.flag goto :done_build
set dots=%dots%.
if "%dots%"=="...." set dots=
cls
echo =====================================
echo      ATUALIZANDO A APLICACAO
echo =====================================
echo.
echo Construindo nova versao, por favor aguarde%dots%
echo (sistema atual continua rodando normalmente)
timeout /t 1 /nobreak >nul
goto :loop_build

:done_build
set "BUILD_RESULT="
set /p BUILD_RESULT=<logs\build_done.flag
set "BUILD_RESULT=%BUILD_RESULT: =%"

IF NOT "%BUILD_RESULT%"=="0" (
    echo.
    echo =====================================
    echo     ERRO AO CONSTRUIR NOVA VERSAO
    echo     O sistema atual continua rodando normalmente.
    echo     Veja logs\docker_build.log
    echo =====================================
    pause
    exit /b
)

echo.
echo Build concluido! Trocando para a nova versao...

echo.
echo Parando containers antigos...
docker compose down > logs\docker_down.log 2>&1

del /q logs\up_done.flag 2>nul
del /q logs\_run_up.bat 2>nul

(
echo @echo off
echo set COMPOSE_BAKE=false
echo docker compose up -d ^> logs\docker_up.log 2^>^&1
echo echo %%ERRORLEVEL%% ^> logs\up_done.flag
) > logs\_run_up.bat

start "" /b cmd /c logs\_run_up.bat

set dots=
:loop_up
if exist logs\up_done.flag goto :done_up
set dots=%dots%.
if "%dots%"=="...." set dots=
cls
echo =====================================
echo      ATUALIZANDO A APLICACAO
echo =====================================
echo.
echo Subindo nova versao, por favor aguarde%dots%
timeout /t 1 /nobreak >nul
goto :loop_up

:done_up
set "UP_RESULT="
set /p UP_RESULT=<logs\up_done.flag
set "UP_RESULT=%UP_RESULT: =%"

IF "%UP_RESULT%"=="0" (
    echo.
    echo =====================================
    echo     ATUALIZACAO CONCLUIDA!
    echo =====================================
) ELSE (
    echo.
    echo =====================================
    echo     ERRO AO SUBIR OS CONTAINERS - codigo [%UP_RESULT%]
    echo     ATENCAO: os containers antigos foram parados.
    echo     Veja logs\docker_up.log e considere rodar:
    echo     docker compose up -d
    echo =====================================
)

pause