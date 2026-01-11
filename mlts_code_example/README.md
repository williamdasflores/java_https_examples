Criar par de chaves para o Server  
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 90 -keystore serverKeystore.jks  
O comando acima irá gerar uma par de chaves RSA com tamanho de 2048 bits e armazenar num arquivo JKS(Java Key Store) com nome serverKeystore.jks. Será pedido senha para o arquivo jks

Criar par de chaves para o Client  
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -validity 90 -keystore clientKeystore.jks  
Cria par de chaves RSA parecido com o anterior, porém para o client.

Exporte o certificado público do Client    
keytool -exportcert -alias client -file client.crt -keystore clientKeystore.jks  

Importe o certificado público de Client para o serverTruststore.jks  
keytool -importcert -alias client -file client.crt -keystore serverTruststore.jks

Exporte o certificado público do Server  
keytool -exportcert -alias server -file server.crt -keystore serverKeystore.jks

Importe o certificado público do Server para clientTruststore.jks  
keytool -importcert -alias server -file server.crt -keystore clientTruststore.jks  

   

Para visualizar o conteúdo de uma keystore. Utilize o comando abaixo:  
keytool -list -keystore serverKeystore.jks



