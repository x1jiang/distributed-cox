rem cd bin
rem jar cvf ..\WebGlore.jar .\*
rem cd ..
keytool -genkey -alias webglore -keystore ca
keytool -export -keystore ca -alias webglore -file webglore.cert
jarsigner -keystore ca WebGlore.jar WebGlore