@echo off
mode con: cols=150 lines=40
color 02
java -Xms512m -Xmx1024m -jar mainProgram.jar
@pause