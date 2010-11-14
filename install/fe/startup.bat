
cd "%INSTALL_PATH"
rd felix-cache /s /q
md felix-cache
"%JAVA_HOME\bin\java" -jar bin\felix.jar
