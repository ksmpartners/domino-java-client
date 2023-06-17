package com.dominodatalab.client;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Manager to allow internal Domino instances without valid SSL Certs to still work.
 */
public class TrustAllManager {

   private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {
      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
         return null;
      }

      @Override
      public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
      }

      @Override
      public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
      }
   }};

   public static SSLContext createSslContext() {
      SSLContext sslContext;
      try {
         sslContext = SSLContext.getInstance("TLS");
         sslContext.init(null, TRUST_ALL_CERTS, new SecureRandom());
      } catch (NoSuchAlgorithmException | KeyManagementException ex) {
         throw new RuntimeException(ex);
      }
      return sslContext;
   }
}