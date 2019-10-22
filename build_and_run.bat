@ECHO OFF
SET projectName=timelapse-ramping
ECHO Building the jar...
CALL gradlew clean shadowJar

if %ERRORLEVEL% EQU 0 (
    ECHO Bulding Docker image...
    CALL docker build -t %projectName% .
    if %ERRORLEVEL% EQU 0 (
        ECHO Running docker image...
        CALL docker run %projectName%
    ) ELSE (
        ECHO Could not build the docker image! Error code is %ERRORLEVEL%
    )
) ELSE (
    ECHO Could not build the project! Error code is %ERRORLEVEL%
)
ECHO Exiting from batch script...