@echo off
del *.jar
cls
"C:\Program Files\Java\jdk1.7.0_40\bin\javac" ChooseSystem.java
::"C:\Program Files\Java\jdk1.7.0_40\bin\java" ChooseSystem
"C:\Program Files\Java\jdk1.7.0_40\bin\jar" cvfm ChooseSystem.jar manifest *.class
del *.class
::"C:\Program Files\Java\jdk1.7.0_40\bin\java" -jar ChooseSystem.jar
::"C:\Program Files\Java\jdk1.7.0_40\bin\java" -jar ChooseSystem.jar -putty "D:\Applications\PuTTY\putty.exe" -tightvnc "C:\Program Files\TightVNC\tvnviewer.exe"
