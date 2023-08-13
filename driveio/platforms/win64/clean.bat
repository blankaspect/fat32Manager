@echo off

rem : Directories
set OBJ_DIR=obj
set BIN_DIR=bin

rem : Delete output directories
if exist %BIN_DIR% (rmdir /Q /S %BIN_DIR%)
if exist %OBJ_DIR% (rmdir /Q /S %OBJ_DIR%)
