package com.microsoft.azure.keyvault.spring;

import com.microsoft.aad.adal4j.AsymmetricKeyCredential;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.*;

/**
 * Created by umar on 23/2/18.
 */
public class AzureKeyVaultCredentialViaCertificate extends KeyVaultCredentials {

    private static final long DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS = 60L;
    private String clientId;
    private final String pfxPath;
    private final String pfxPassword;
    private long timeoutInSeconds;

    public AzureKeyVaultCredentialViaCertificate(String clientId, String pfxPath,String pfxPassword, long timeoutInSeconds) {
        this.clientId = clientId;
        this.pfxPath = pfxPath;
        this.pfxPassword = pfxPassword;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public AzureKeyVaultCredentialViaCertificate(String clientId, String pfxPath,String pfxPassword) {
        this(clientId,pfxPath,pfxPassword, DEFAULT_TOKEN_ACQUIRE_TIMEOUT_IN_SECONDS);
    }
    @Override
    public String doAuthenticate(String authorization, String resource, String scope) {
        try {
            KeyCert certificateKey = readPfx(pfxPath, pfxPassword);
            PrivateKey privateKey = certificateKey.getKey();
            AuthenticationContext context = new AuthenticationContext(authorization, false, Executors.newSingleThreadExecutor());
            AsymmetricKeyCredential asymmetricKeyCredential = AsymmetricKeyCredential.create(clientId, privateKey, certificateKey.getCertificate());
            final Future<AuthenticationResult> future = context.acquireToken(resource, asymmetricKeyCredential, null);
            AuthenticationResult result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            return result.getAccessToken();
        } catch (InterruptedException|ExecutionException|IOException|CertificateException|NoSuchAlgorithmException|
                UnrecoverableKeyException|NoSuchProviderException|KeyStoreException|TimeoutException e) {
            throw new RuntimeException("KeyVault Authentication Failed: "+e.getMessage());
        }
    }

    public KeyCert readPfx(String path,String password) throws NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

        try(FileInputStream stream = new FileInputStream(path)){
            final KeyStore store = KeyStore.getInstance("pkcs12", "SunJSSE");
            store.load(stream, password.toCharArray());
            KeyCert keyCert = new KeyCert();
            Enumeration<String> aliases = store.aliases();

            while(aliases.hasMoreElements()){
                String alias = aliases.nextElement();
                X509Certificate certificate = (X509Certificate) store.getCertificate(alias);
                PrivateKey key = (PrivateKey)store.getKey(alias,password.toCharArray());
                keyCert.setCertificate(certificate);
                keyCert.setKey(key);
            }
            return keyCert;
        }
    }

    public class KeyCert {

        private X509Certificate certificate;
        private PrivateKey key;

        public X509Certificate getCertificate() {
            return certificate;
        }

        public void setCertificate(X509Certificate certificate) {
            this.certificate = certificate;
        }

        public PrivateKey getKey() {
            return key;
        }

        public void setKey(PrivateKey key) {
            this.key = key;
        }
    }
}
