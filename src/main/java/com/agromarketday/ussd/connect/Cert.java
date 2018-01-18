/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.connect;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cellulant
 */

public class Cert {

    private static final Logger logging = LoggerFactory.getLogger(Cert.class);

    static public void trustHttpsCertificates() throws Exception {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        //Create a trust manager that does not validate certificate chains:
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }
            }//X509TrustManager
        };//TrustManager[]

        //Install the all-trusting trust manager:
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory sslsf = sc.getSocketFactory();
        //System.out.println(sf.getClass());
        //System.out.println(HttpsURLConnection.getDefaultSSLSocketFactory().getClass());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslsf);

        //avoid "HTTPS hostname wrong: should be <myhostname>" exception:
        HostnameVerifier hv = getHostNameVerifier();

        // trust the connection
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

    }//trustHttpsCertificates

    private static HostnameVerifier getHostNameVerifier() {

        //avoid "HTTPS hostname wrong: should be <myhostname>" exception:
        HostnameVerifier hv = new HostnameVerifier() {

            //String urlHostName = "https://10.31.30.126:443/LoadWSApp/LoadWebServiceV1";
            //String urlHodstsName = Props.getInstance().getSptransferURL();
            @Override
            public boolean verify(String urlHostName, SSLSession session) {

                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    logging.debug("Warning: URL host '" + urlHostName + "' is different from SSLSession host '" + session.getPeerHost() + "'.");
                }

                // double check to  make sure all localhost connections are allowed
                if (urlHostName.equalsIgnoreCase("localhost")) {
                    logging.info("Warning: URL host '" + urlHostName + "' connecting... '" + "'.");

                    return true;
                }

                logging.debug("Host URL: '" + urlHostName + "' && SSLSession host URL: '" + session.getPeerHost() + "'.");

                return true; //also accept different hostname (e.g. domain name instead of IP address)
            }

        };

        return hv;
    }
}
