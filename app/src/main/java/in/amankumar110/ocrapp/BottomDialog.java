package in.amankumar110.ocrapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import in.amankumar110.ocrapp.databinding.BottomDialogBinding;

public class BottomDialog extends BottomSheetDialogFragment {

    private BottomDialogBinding binding;
    private String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomDialogBinding.inflate(getLayoutInflater(), container, false);

        // Preventing the BottomSheet from being hidden by the navigation keys
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> insets);
        } else {
            // For devices below Android 11
            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> insets);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Close button logic
        if (binding != null) {
            binding.btnClose.setOnClickListener(v -> dismiss());
        }

        // Visit link logic
        if (binding != null && url != null) {
            binding.btnVisitLink.setOnClickListener(v -> {
                Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(visitIntent);
            });
        }
    }

    public void setURL(String url) {
        this.url = url;
        if (binding != null) {
            binding.tvLinkAddress.setText(url);
        }
    }
}
