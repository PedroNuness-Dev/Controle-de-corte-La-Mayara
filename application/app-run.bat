@echo off
title Controle de Corte

echo =====================================
echo      INICIANDO A APLICACAO...
echo =====================================

echo.
echo Parando containers antigos...
docker compose down

echo.
echo Subindo containers...
docker compose up -d

IF %ERRORLEVEL% EQU 0 (
    echo.
    echo =====================================
    echo   APLICACAO INICIADA COM SUCESSO!
    echo =====================================
) ELSE (
    echo.
    echo =====================================
    echo      ERRO AO INICIAR APLICACAO
    echo =====================================
)

pause