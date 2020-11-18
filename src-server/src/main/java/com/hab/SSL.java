package com.hab;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

public class SSL {

    public static SSLEngine createEngine(boolean isHttp, String keyStore) throws Exception {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        // Load keys.
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fin = new FileInputStream(new File(keyStore + ".jks"));
        ks.load(fin, "derp12".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(ks, "derp12".toCharArray());

        // Create context.
        SSLContext sc = SSLContext.getInstance(isHttp ? "SSL" : "TLSv1.2");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLParameters sslParameters = sc.getSupportedSSLParameters();
        SSLEngine engine =  sc.createSSLEngine();

        if (!isHttp) {
            sslParameters.setCipherSuites(new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"});

            engine.setEnabledCipherSuites(new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"});
            engine.setSSLParameters(sslParameters);
        }

        return engine;
    }

}
