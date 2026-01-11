package org.example;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.logging.Logger;

public class Client {
    private static final String KEYSTORE_LOCATION = "clientKeystore.jks";
    private static final String TRUSTSTORE_LOCATION = "clientTruststore.jks";
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");

            try (FileInputStream keyStoreFileInputStream = new FileInputStream("src/main/resources/certificates/client/"+KEYSTORE_LOCATION)) {
                keyStore.load(keyStoreFileInputStream, "<password>".toCharArray());
                keyManagerFactory.init(keyStore, "<password>".toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustStoreFileInputStream = new FileInputStream("src/main/resources/certificates/client/"+TRUSTSTORE_LOCATION)) {
                trustStore.load(trustStoreFileInputStream, "<password>".toCharArray());
                trustManagerFactory.init(trustStore);
            }

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            try {
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("localhost", 8000);
                sslSocket.startHandshake();

                OutputStream outputStream = sslSocket.getOutputStream();
                String message = "Hello Server! I am a Client\n";
                outputStream.write(message.getBytes());
                LOGGER.info("Sent message to server: " + message);

                InputStream inputStream = sslSocket.getInputStream();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String response = bufferedReader.readLine();
                    LOGGER.info("Received message: " + response);
                } finally {
                    sslSocket.close();
                }
            } catch (SSLException e) {
                LOGGER.severe("SSLException: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.severe("An error ocurred on client side " + e.getMessage());
        }
    }
}
