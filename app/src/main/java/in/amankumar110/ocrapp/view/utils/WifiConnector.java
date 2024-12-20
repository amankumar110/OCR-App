package in.amankumar110.ocrapp.view.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;

public class WifiConnector {

    private static final String TAG = "WifiConnector";

    // Check if the necessary permissions are granted
    private static boolean hasPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void connectToWifi(Context context, String ssid, String password) {
        if (hasPermissions(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (API 29+)
                connectWithNetworkSpecifier(context, ssid, password);
            } else {
                // Android 9 and below (API 28 and below)
                connectWithWifiManager(context, ssid, password);
            }
        } else {
            Log.e(TAG, "Permissions are not granted");
            // Handle permissions request here, or notify user
        }
    }

    // For Android 10+ (API 29 and above)
    private static void connectWithNetworkSpecifier(Context context, String ssid, String password) {
        NetworkRequest networkRequest = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSpecifier wifiSpecifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build();

            networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiSpecifier)
                    .build();
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (networkRequest != null) {
            connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Log.d(TAG, "Wi-Fi Connected successfully");
                    // Bind the network for internet access
                    connectivityManager.bindProcessToNetwork(network);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Log.e(TAG, "Wi-Fi Connection failed");
                }
            });
        }
    }

    // For Android 9 and below (API 28 and below)
    private static void connectWithWifiManager(Context context, String ssid, String password) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";

        // Add network and connect
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            Log.d(TAG, "Wi-Fi Connected successfully");
        } else {
            Log.e(TAG, "Failed to add Wi-Fi network");
        }
    }
}
