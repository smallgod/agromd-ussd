package com.agromarketday.ussd.config;

/**
 *
 * @author smallgod
 */
public class JettyServerConfig {

//    String contextPath = sharedAppConfigsIF.getContextpath();
//    String webAppWarFile = sharedAppConfigsIF.getWebappwarfile();
//    int HTTP_PORT = sharedAppConfigsIF.getHttpport();
//    int HTTPS_PORT = sharedAppConfigsIF.getHttpsport();
//    int ADMIN_PORT = sharedAppConfigsIF.getAdminport();
//    int OUTPUT_BUFFER_SIZE = sharedAppConfigsIF.getOutputbuffersize();
//    int REQUEST_HEADER_SIZE = sharedAppConfigsIF.getRequestheadersize();
//    int RESPONSE_HEADER_SIZE = sharedAppConfigsIF.getResponseheadersize();
//    String KEYSTORE_PATH = sharedAppConfigsIF.getKeystorepass();
//    String KEYSTORE_PASS = sharedAppConfigsIF.getKeystorepass();
//    String KEYSTORE_MGR_PASS = sharedAppConfigsIF.getKeystoremanagerpass();
//    String[] WELCOME_FILES = (sharedAppConfigsIF.getWelcomefiles().trim()).split("\\s*,\\s*");
//    String resourceBase = sharedAppConfigsIF.getResourceDirAbsPath();
//    String webDescriptor = resourceBase + sharedAppConfigsIF.getWebxmlfile();
    private final String contextPath;
    private final String resourceBase;
    private final String webDescriptor;
    private final int httpPort;
    private String webAppWarFile;
    private String keyStorePath;
    private String keyStorePass;
    private String keyStoreMgrPass;
    private String[] welcomeFilesArray;
    private int httpsPort;
    private int adminPort;
    private int outputBufferSize;
    private int requestHeaderSize;
    private int responseHeaderSize;

    public JettyServerConfig(String contextPath, String resourceBase, String webDescriptor, int httpPort) {

        this.contextPath = contextPath;
        this.resourceBase = resourceBase;
        this.webDescriptor = webDescriptor;
        this.httpPort = httpPort;
    }

    /**
     * @return the contextPath
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * @return the webAppWarFile
     */
    public String getWebAppWarFile() {
        return webAppWarFile;
    }

    /**
     * @param webAppWarFile the webAppWarFile to set
     */
    public void setWebAppWarFile(String webAppWarFile) {
        this.webAppWarFile = webAppWarFile;
    }

    /**
     * @return the keyStorePath
     */
    public String getKeyStorePath() {
        return keyStorePath;
    }

    /**
     * @param keyStorePath the keyStorePath to set
     */
    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    /**
     * @return the keyStorePass
     */
    public String getKeyStorePass() {
        return keyStorePass;
    }

    /**
     * @param keyStorePass the keyStorePass to set
     */
    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    /**
     * @return the keyStoreMgrPass
     */
    public String getKeyStoreMgrPass() {
        return keyStoreMgrPass;
    }

    /**
     * @param keyStoreMgrPass the keyStoreMgrPass to set
     */
    public void setKeyStoreMgrPass(String keyStoreMgrPass) {
        this.keyStoreMgrPass = keyStoreMgrPass;
    }

    /**
     * @return the welcomeFilesArray
     */
    public String[] getWelcomeFilesArray() {
        return welcomeFilesArray;
    }

    /**
     * @param welcomeFilesArray the welcomeFilesArray to set
     */
    public void setWelcomeFilesArray(String[] welcomeFilesArray) {
        this.welcomeFilesArray = welcomeFilesArray;
    }

    /**
     * @return the resourceBase
     */
    public String getResourceBase() {
        return resourceBase;
    }

    /**
     * @return the webDescriptor
     */
    public String getWebDescriptor() {
        return webDescriptor;
    }

    /**
     * @return the httpPort
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * @return the httpsPort
     */
    public int getHttpsPort() {
        return httpsPort;
    }

    /**
     * @param httpsPort the httpsPort to set
     */
    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    /**
     * @return the adminPort
     */
    public int getAdminPort() {
        return adminPort;
    }

    /**
     * @param adminPort the adminPort to set
     */
    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    /**
     * @return the outputBufferSize
     */
    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    /**
     * @param outputBufferSize the outputBufferSize to set
     */
    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    /**
     * @return the requestHeaderSize
     */
    public int getRequestHeaderSize() {
        return requestHeaderSize;
    }

    /**
     * @param requestHeaderSize the requestHeaderSize to set
     */
    public void setRequestHeaderSize(int requestHeaderSize) {
        this.requestHeaderSize = requestHeaderSize;
    }

    /**
     * @return the responseHeaderSize
     */
    public int getResponseHeaderSize() {
        return responseHeaderSize;
    }

    /**
     * @param responseHeaderSize the responseHeaderSize to set
     */
    public void setResponseHeaderSize(int responseHeaderSize) {
        this.responseHeaderSize = responseHeaderSize;
    }
}
