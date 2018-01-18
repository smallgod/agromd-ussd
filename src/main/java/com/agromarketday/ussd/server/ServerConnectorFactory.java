/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.server;

import com.agromarketday.ussd.logger.LoggerUtil;
import java.io.File;
import java.io.FileNotFoundException;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 *
 * @author smallgod
 */
public class ServerConnectorFactory {

    //PUT IN CONFIGS FILE
    /*
    public static final int HTTP_PORT = 6007;
    public static final int HTTPS_PORT = 6005;
    public static final int ADMIN_PORT = 6180;
    public static final int IDLE_TIME = 30000;
    public static final int REQUEST_HEADER_SIZE = 8192;
    public static final int RESPONSE_HEADER_SIZE = 8192;
    public static final int OUTPUT_BUFFER_SIZE = 32768;
    public static final String JETTY_HOME = System.getProperty("jetty.home", "../jetty-distribution/target/distribution");
    public static final String JETTY_DIST_KEYSTORE = "../../jetty-distribution/target/distribution/demo-base/etc/keystore";
    public static final String KEYSTORE_PATH = System.getProperty("example.keystore", JETTY_DIST_KEYSTORE);
    public static final String KEYSTORE_PASS = "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4";
    public static final String KEYSTORE_MGR_PASS = "OBF:1u2u1wml1z7s1z7a1wnl1u2g";

     */
    private final LoggerUtil logger;

    protected ServerConnectorFactory() {
        this.logger = new LoggerUtil(ServerConnectorFactory.class);
    }

    /**
     * collection of configuration information appropriate for http and
     * httpsConnector note: Add these configs before the server is started
     *
     * @param jettyServer
     * @param defaultHTTPConfig
     * @param OUTPUT_BUFFER_SIZE
     * @param REQUEST_HEADER_SIZE
     * @param RESPONSE_HEADER_SIZE
     * @param isSendServerVersion
     * @param isSendDateHeader
     * @return
     */
    protected HttpConfiguration createHTTPConfigs(
            Server jettyServer,
            HttpConfiguration defaultHTTPConfig,
            int OUTPUT_BUFFER_SIZE,
            int REQUEST_HEADER_SIZE,
            int RESPONSE_HEADER_SIZE,
            boolean isSendServerVersion,
            boolean isSendDateHeader
    ) {

        //check if server is started before adding configs
        if (!(jettyServer.isStarting() || jettyServer.isStarted())) {

            if (OUTPUT_BUFFER_SIZE > 0) {
                defaultHTTPConfig.setOutputBufferSize(OUTPUT_BUFFER_SIZE);
            }
            if (REQUEST_HEADER_SIZE > 0) {
                defaultHTTPConfig.setRequestHeaderSize(REQUEST_HEADER_SIZE);
            }
            if (RESPONSE_HEADER_SIZE > 0) {
                defaultHTTPConfig.setResponseHeaderSize(RESPONSE_HEADER_SIZE);
            }
            defaultHTTPConfig.setSendServerVersion(isSendServerVersion);
            defaultHTTPConfig.setSendDateHeader(isSendDateHeader);
            //config.setHeaderCacheSize(REQUEST_HEADER_CACHE);
        }

        return defaultHTTPConfig;
    }

    protected ServerConnector setConnectorConfigs(
            ServerConnector serverConnector,
            long IDLE_TIMEOUT
    ) {
        serverConnector.setIdleTimeout(IDLE_TIMEOUT);
        return serverConnector;
    }

    protected Connector createHttpConnector(
            Server jettyServer,
            HttpConfiguration httpConfig,
            final int HTTP_PORT
    ) {

        ServerConnector httpDefaultConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(httpConfig));
        httpDefaultConnector.setPort(HTTP_PORT);
        httpDefaultConnector.setName("http");

        return httpDefaultConnector;
    }

    protected Connector createHttpsConnector(
            Server jettyServer,
            HttpConfiguration httpConfig,
            final int HTTPS_PORT,
            final String KEYSTORE_PATH,
            final String KEYSTORE_PASS,
            final String KEYSTORE_MGR_PASS
    ) throws FileNotFoundException {

        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(HTTPS_PORT);
        httpConfig.addCustomizer(new SecureRequestCustomizer());

        File keystoreFile = new File(KEYSTORE_PATH);

        if (!keystoreFile.exists()) {
            //throw new FileNotFoundException(keystoreFile.getAbsolutePath());
        }

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword(KEYSTORE_PASS);
        sslContextFactory.setKeyManagerPassword(KEYSTORE_MGR_PASS);

        ServerConnector httpsConnector = new ServerConnector(jettyServer,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpConfig));
        httpsConnector.setPort(HTTPS_PORT);
        httpsConnector.setName("https");

        return httpsConnector;
    }

    protected Connector createAdminConnector(
            Server jettyServer,
            HttpConfiguration httpConfig,
            final int ADMIN_PORT) {

        ServerConnector adminConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(httpConfig));
        adminConnector.setPort(ADMIN_PORT);
        adminConnector.setName("admin");

        return adminConnector;
    }

}
