/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.dev.security;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;

import org.guinsoo.security.CipherFactory;
import org.guinsoo.util.StringUtils;

/**
 * Tool to generate source code for the SecureSocketFactory. First, create a
 * keystore using:
 * <pre>
 * keytool -genkey -alias guinsoo -keyalg RSA -dname &quot;cn=Guinsoo&quot;
 *     -validity 25000 -keypass guinsoopass -keystore guinsoo.keystore
 *     -storepass guinsoopass
 * </pre>
 * Then run this application to generate the source code. Then replace the code
 * in the function SecureSocketFactory.getKeyStore as specified
 */
public class SecureKeyStoreBuilder {

    private SecureKeyStoreBuilder() {
        // utility class
    }

    /**
     * This method is called when executing this application from the command
     * line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        String password = CipherFactory.KEYSTORE_PASSWORD;
        KeyStore store = CipherFactory.getKeyStore(password);
        printKeystore(store, password);
    }

    private static void printKeystore(KeyStore store, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CertificateEncodingException {
        System.out.println("KeyStore store = KeyStore.getInstance(\""
                + store.getType() + "\");");
        System.out.println("store.load(null, password.toCharArray());");
        // System.out.println("keystore provider=" +
        //         store.getProvider().getName());
        Enumeration<String> en = store.aliases();
        while (en.hasMoreElements()) {
            String alias = en.nextElement();
            Key key = store.getKey(alias, password.toCharArray());
            System.out.println(
                    "KeyFactory keyFactory = KeyFactory.getInstance(\""
                    + key.getAlgorithm() + "\");");
            System.out.println("store.load(null, password.toCharArray());");
            String pkFormat = key.getFormat();
            String encoded = StringUtils.convertBytesToHex(key.getEncoded());
            System.out.println(
                    pkFormat + "EncodedKeySpec keySpec = new "
                    + pkFormat + "EncodedKeySpec(getBytes(\""
                    + encoded + "\"));");
            System.out.println(
                    "PrivateKey privateKey = keyFactory.generatePrivate(keySpec);");
            System.out.println("Certificate[] certs = {");
            for (Certificate cert : store.getCertificateChain(alias)) {
                System.out.println(
                        "  CertificateFactory.getInstance(\""+cert.getType()+"\").");
                String enc = StringUtils.convertBytesToHex(cert.getEncoded());
                System.out.println(
                        "        generateCertificate(new ByteArrayInputStream(getBytes(\""
                        + enc + "\"))),");
                // PublicKey pubKey = cert.getPublicKey();
                // System.out.println("    pubKey algorithm=" +
                //         pubKey.getAlgorithm());
                // System.out.println("    pubKey format=" +
                //         pubKey.getFormat());
                // System.out.println("    pubKey format="+
                //         Utils.convertBytesToString(pubKey.getEncoded()));
            }
            System.out.println("};");
            System.out.println("store.setKeyEntry(\"" + alias
                    + "\", privateKey, password.toCharArray(), certs);");
        }
    }

//     private void listCipherSuites(SSLServerSocketFactory f) {
//        String[] def = f.getDefaultCipherSuites();
//        for (int i = 0; i < def.length; i++) {
//            System.out.println("default = " + def[i]);
//        }
//        String[] sup = f.getSupportedCipherSuites();
//        for (int i = 0; i < sup.length; i++) {
//            System.out.println("supported = " + sup[i]);
//        }
//    }

}
