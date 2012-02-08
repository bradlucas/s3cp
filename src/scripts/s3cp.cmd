@echo off
REM Place this batch script in the same folder as s3cp-cmdline-*.jar
REM Make sure java.exe is in your PATH env as well as the context path to the dir of this file.
java -jar s3cp-cmdline-*.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
