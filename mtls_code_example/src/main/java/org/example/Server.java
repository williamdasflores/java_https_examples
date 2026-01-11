package org.example;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.logging.Logger;

public class Server {
    private static final String KEYSTORE_LOCATION = "serverKeystore.jks";
    private static final String TRUSTSTORE_LOCATION = "serverTruststore.jks";
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreFileInputStream = new FileInputStream("src/main/resources/certificates/server/"+KEYSTORE_LOCATION)) {
                keyStore.load(keyStoreFileInputStream, "<password>".toCharArray());
                keyManagerFactory.init(keyStore, "<password>".toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustStoreFileInputStream = new FileInputStream("src/main/resources/certificates/server/"+TRUSTSTORE_LOCATION)) {
                trustStore.load(trustStoreFileInputStream, "<password>".toCharArray());
                trustManagerFactory.init(trustStore);
            }

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8000);
            sslServerSocket.setNeedClientAuth(true);

            try {
                while(true) {
                    LOGGER.info("Waiting for client connection....\n");

                    SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                    LOGGER.info("Client connected!! \n");

                    InputStream inputStream = sslSocket.getInputStream();
                    OutputStream outputStream = sslSocket.getOutputStream();
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        String message = bufferedReader.readLine();
                        LOGGER.info("Received message from client: " + message + "\n");
                        String response = "Hello Client Side";
                        outputStream.write(response.getBytes());
                    } finally {
                        sslSocket.close();
                    }
                }
            } catch (SSLException e) {
                LOGGER.severe("SSLException " + e.getMessage());
            } finally {
                sslServerSocket.close();
            }
        } catch (Exception e) {
            LOGGER.severe("An error "+ e.getMessage());
        }
    }
}