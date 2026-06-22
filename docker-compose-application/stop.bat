@echo off
title Encerrar Controle de Corte

echo =====================================
echo      PARANDO A APLICACAO...
echo =====================================

docker compose down

IF %ERRORLEVEL% EQU 0 (
    echo.
    echo =====================================
    echo      SISTEMA ENCERRADO COM SUCESSO!
    echo =====================================
) ELSE (
    echo.
    echo =====================================
    echo   ERRO AO ENCERRAR O SISTEMA
    echo =====================================
)

pause