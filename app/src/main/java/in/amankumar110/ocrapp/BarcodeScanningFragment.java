package in.amankumar110.ocrapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.amankumar110.ocrapp.databinding.FragmentBarcodeScanningBinding;
import in.amankumar110.ocrapp.view.utils.PermissionManager;
import in.amankumar110.ocrapp.view.utils.WifiConnector;


public class BarcodeScanningFragment extends Fragment {

    private static final int REQUEST_CODE_WRITE_SETTINGS = 309;
    private FragmentBarcodeScanningBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService executorService;
    private ImageAnalyzer imageAnalyzer;
    private PermissionManager permissionManager;
    private PermissionManager wifiPermissionManager;
    private Barcode.WiFi wifi;

    private final PermissionManager.PermissionCallback permissionCallback = new PermissionManager.PermissionCallback() {
        @Override
        public void onAccepted() {
            // Permission granted, setup camera
            setupCamera();
        }

        @Override
        public void onDenied() {
            // Handle permission denial
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    };

    private final PermissionManager.PermissionCallback wifiPermissionCallback = new PermissionManager.PermissionCallback() {
        @Override
        public void onAccepted() {
            WifiConnector.connectToWifi(requireContext(),wifi.getSsid(),wifi.getPassword());
        }

        @Override
        public void onDenied() {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionManager = new PermissionManager(this,
                new String[]{Manifest.permission.CAMERA},
                this.permissionCallback,
                getString(R.string.camera_permission_rationale_message));

        wifiPermissionManager = new PermissionManager(BarcodeScanningFragment.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                wifiPermissionCallback,
                "Requested Permissions are Essential!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_barcode_scanning, container, false);
        binding = FragmentBarcodeScanningBinding.bind(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        requestWriteSettingsPermission();
        permissionManager.requestPermissions();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check permission and set up camera when fragment is resumed
        if (permissionManager.hasPermissions()) {
            setupCamera();
        } else {
            permissionManager.hasPermissions();
        }
    }

    private void setupCamera() {
        executorService = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        imageAnalyzer = new ImageAnalyzer(requireActivity().getSupportFragmentManager());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                bindPreview(processCameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Error setting up camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(ProcessCameraProvider processCameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(binding.pvCamera.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder().build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(executorService, imageAnalyzer);

        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    private void requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(requireContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
        }
    }

    private class ImageAnalyzer implements ImageAnalysis.Analyzer {

        private final FragmentManager fragmentManager;
        private final BottomDialog bottomDialog;

        public ImageAnalyzer(FragmentManager supportFragmentManager) {
            this.fragmentManager = supportFragmentManager;
            bottomDialog = new BottomDialog();
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanBarcode(image);
        }

        private void scanBarcode(ImageProxy imageProxy) {
            @SuppressLint("UnsafeOptInUsageError")
            Image image = imageProxy.getImage();

            assert image != null;
            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
                    .build();

            BarcodeScanner barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(this::renderBarcodes)
                    .addOnFailureListener(e -> Toast.makeText(bottomDialog.requireContext(), "Failed To Read Code", Toast.LENGTH_SHORT).show()).addOnCompleteListener(task -> imageProxy.close());
        }

        private void renderBarcodes(List<Barcode> barcodes) {
            for (Barcode barcode : barcodes) {
                int valueType = barcode.getValueType();
                if (valueType == Barcode.TYPE_URL) {
                    if (!bottomDialog.isAdded()) {
                        bottomDialog.show(fragmentManager, "");
                    }
                    bottomDialog.setURL(Objects.requireNonNull(barcode.getUrl()).getUrl());
                } else if(valueType == Barcode.TYPE_WIFI) {
                    wifi = barcode.getWifi();
                    wifiPermissionManager.requestPermissions();
                }
            }
        }


    }
}
