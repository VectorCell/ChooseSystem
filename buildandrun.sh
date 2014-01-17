rm *.jar
clear
javac ChooseSystem.java
#java ChooseSystem
jar cvfm ChooseSystem.jar manifest *.class
rm *.class
#java -jar ChooseSystem.jar
java -jar ChooseSystem.jar -putty "D:\Applications\PuTTY\putty.exe" -tightvnc "C:\Program Files\TightVNC\tvnviewer.exe"
