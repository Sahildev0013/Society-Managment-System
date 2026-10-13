@echo off
if exist "C:\Users\User\.gemini\antigravity\scratch\apache-maven-3.9.6\bin\mvn.cmd" (
    "C:\Users\User\.gemini\antigravity\scratch\apache-maven-3.9.6\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)
mvn %*
