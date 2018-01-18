package com.agromarketday.ussd.config;

import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.sharedInterface.RemoteRequest;
import java.util.Map;

/**
 *
 * @author smallgod
 */
public class RemoteUnitConfig {

    private final Map<String, RemoteRequest> remoteUnits;

    public RemoteUnitConfig(Map<String, RemoteRequest> remoteUnits) {
        this.remoteUnits = remoteUnits;
    }

    public RemoteRequest getAdDisplayRemoteUnit() {

        return getRemoteUnits().get(NamedConstants.ADDISPLAY_UNIT_REQUEST);
    }

    public RemoteRequest getAdCentralRemoteUnit() {

        return getRemoteUnits().get(NamedConstants.CENTRAL_UNIT_REQUEST);
    }

    public RemoteRequest getAdDbManagerRemoteUnit() {

        return getRemoteUnits().get(NamedConstants.ADDBManager_UNIT_REQUEST);
    }

    public RemoteRequest getDSMBridgeRemoteUnit() {

        return getRemoteUnits().get(NamedConstants.DSM_UNIT_REQUEST);
    }

    public Map<String, RemoteRequest> getRemoteUnits() {
        return remoteUnits;
    }

}
