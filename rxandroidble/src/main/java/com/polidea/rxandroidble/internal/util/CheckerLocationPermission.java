package com.polidea.rxandroidble.internal.util;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;

import javax.inject.Inject;
import javax.inject.Named;

import com.polidea.rxandroidble.ClientComponent;

public class CheckerLocationPermission {

    private final Context context;
    private final String[] scanPermissions;

    @Inject
    CheckerLocationPermission(
            Context context,
            @Named(ClientComponent.PlatformConstants.STRING_ARRAY_SCAN_PERMISSIONS) String[] scanPermissions
    ) {
        this.context = context;
        this.scanPermissions = scanPermissions;
    }

    public boolean areScanPermissionsOk() {
        for (String locationPermission : scanPermissions) {
            if (isPermissionGranted(locationPermission)) {
                return true;
            }
        }
        return scanPermissions.length == 0;
    }

    /**
     * Copied from android.support.v4.content.ContextCompat for backwards compatibility
     * @param permission the permission to check
     * @return true is granted
     */
    private boolean isPermissionGranted(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }
}
