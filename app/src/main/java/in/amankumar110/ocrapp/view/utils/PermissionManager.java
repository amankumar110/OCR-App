package in.amankumar110.ocrapp.view.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;

public class PermissionManager {

    private final PermissionCallback permissionCallback;
    private final ActivityResultLauncher<String[]> permissionLauncher;
    private final String[] permissions;
    private final Context context;

    public interface PermissionCallback {
        void onAccepted();
        void onDenied();
    }

    public PermissionManager(Fragment fragment, String[] permissions, PermissionCallback permissionCallback, String rationaleMessage) {
        this.permissions = permissions;
        this.permissionCallback = permissionCallback;
        this.context = fragment.getContext();

        this.permissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (allPermissionsGranted(result)) {
                        permissionCallback.onAccepted();
                    } else {
                        if (shouldShowRationale(fragment)) {
                            showRationaleDialog(rationaleMessage);
                        } else {
                            showSettingsDialog();
                        }
                    }
                });
    }

    /**
     * Checks if all permissions are granted.
     */
    private boolean allPermissionsGranted(Map<String, Boolean> result) {
        for (Boolean granted : result.values()) {
            if (!granted) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if rationale should be shown for any denied permission.
     */
    private boolean shouldShowRationale(Fragment fragment) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all permissions are already granted.
     */
    public boolean hasPermissions() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Requests permissions using the launcher.
     */
    public void requestPermissions() {
        permissionLauncher.launch(permissions);
    }

    /**
     * Displays a rationale dialog to explain why the permissions are needed.
     */
    private void showRationaleDialog(String rationaleMessage) {
        new AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage(rationaleMessage)
                .setPositiveButton("Grant", (dialog, which) -> permissionLauncher.launch(permissions))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    permissionCallback.onDenied();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Displays a dialog directing the user to the app settings if permissions are permanently denied.
     */
    private void showSettingsDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage("You have permanently denied one or more permissions. To proceed, please enable them in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(context, "Permission denied. You may not be able to use all features.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}
