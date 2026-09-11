@echo off
title Atualizacao do Controle de Corte

echo =====================================
echo      ATUALIZANDO A APLICACAO
echo =====================================

echo.
echo Criando backup do banco...

if not exist backups mkdir backups

for /f %%i in ('powershell -command "Get-Date -Format yyyy-MM-dd_HH-mm-ss"') do set DATA=%%i

docker exec mysql-container-controle-de-corte mysqldump -u root -p237081 controle-de-corte-bd > backups\backup_%DATA%.sql

IF %ERRORLEVEL% NEQ 0 (
    echo Erro ao criar backup.
    pause
    exit /b
)

echo.
echo Fazendo download da ultima versao...
git pull origin main

IF %ERRORLEVEL% NEQ 0 (
    echo Erro ao atualizar codigo.
    pause
    exit /b
)

echo.
echo Reiniciando containers...

docker compose down
docker compose up -d --build

IF %ERRORLEVEL% EQU 0 (
    echo.
    echo =====================================
    echo     ATUALIZACAO CONCLUIDA!
    echo =====================================
) ELSE (
    echo.
    echo =====================================
    echo     ERRO NA ATUALIZACAO
    echo =====================================
)

pause