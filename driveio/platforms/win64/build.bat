@echo off
rem ====================================================================
rem
rem  Build script : driveIO shared library
rem
rem --------------------------------------------------------------------
rem
rem  The following environment variables must be defined:
rem    JAVA_HOME : the location of the JDK
rem
rem ====================================================================

rem : Confine changes to environment variables to this file
setlocal

rem : Directories
set SRC_DIR=src
set OBJ_DIR=obj
set BIN_DIR=bin
set JNI_INC_DIR1=%JAVA_HOME%\include
set JNI_INC_DIR2=%JNI_INC_DIR1%\win32

rem : Files
set EXEC=%BIN_DIR%\driveio.dll

rem : Tools
set CC=cl
set CC_FLAGS=/c /EHsc /D "UNICODE" /TP /MD /O2 /I "%JNI_INC_DIR1%" /I "%JNI_INC_DIR2%"
set LD=link
set LD_FLAGS=/dll

rem : Delete existing output directories
if exist %BIN_DIR% (rmdir /Q /S %BIN_DIR%)
if exist %OBJ_DIR% (rmdir /Q /S %OBJ_DIR%)

rem : Create output directories
mkdir %BIN_DIR%
mkdir %OBJ_DIR%

rem : Compile
@echo on
%CC% %CC_FLAGS% /Fo%OBJ_DIR%\Error.obj %SRC_DIR%\Exception.cc
%CC% %CC_FLAGS% /Fo%OBJ_DIR%\Utils.obj %SRC_DIR%\Utils.cc
%CC% %CC_FLAGS% /Fo%OBJ_DIR%\Volume.obj %SRC_DIR%\Volume.cc
%CC% %CC_FLAGS% /Fo%OBJ_DIR%\uk_blankaspect_driveio_DriveIO.obj %SRC_DIR%\uk_blankaspect_driveio_DriveIO.cc

rem : Link
%LD% %LD_FLAGS% /out:%EXEC% %OBJ_DIR%\*
