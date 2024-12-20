package in.amankumar110.ocrapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

import in.amankumar110.ocrapp.databinding.FragmentImageToTextBinding;
import in.amankumar110.ocrapp.view.adapters.ImageCounterAdapter;
import in.amankumar110.ocrapp.view.adapters.UploadedImageAdapter;
import in.amankumar110.ocrapp.view.utils.TextRecognitionManager;


public class ImageToTextFragment extends Fragment {


    private FragmentImageToTextBinding binding;
    private ActivityResultLauncher<Intent> pickImageResultLauncher;

    private final List<Uri> imageResources = new ArrayList<>();
    private UploadedImageAdapter uploadedImageAdapter;
    private ImageCounterAdapter imageCounterAdapter;
    private TextRecognitionManager textRecognitionManager;
    private final StringBuilder convertedText = new StringBuilder();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_image_to_text, container, false);
        binding = FragmentImageToTextBinding.bind(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadedImageAdapter = new UploadedImageAdapter(imageResources);
        imageCounterAdapter = new ImageCounterAdapter(uploadedImageAdapter.getItemCount(), countItemCallback);
        binding.rvUploadedImages.setAdapter(uploadedImageAdapter);
        binding.rvImagesCount.setAdapter(imageCounterAdapter);

        textRecognitionManager = new TextRecognitionManager(requireContext(),processCallback);

        pickImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),activityResultCallback);

        binding.btnGetText.setOnClickListener(v -> {

            convertedText.setLength(0);
            for(Uri uri : imageResources) {
                textRecognitionManager.process(uri);
            }
        });

        binding.tvResultText.setOnLongClickListener(v -> {
            copyToClipboard(requireContext(),convertedText.toString());
            return false;
        });

    }

    private final View.OnClickListener uploadImageCallback = v -> {
        selectImage();
    };

    private final ImageCounterAdapter.OnCountItemClicked countItemCallback = (viewType,view) -> {

        if(viewType == ImageCounterAdapter.VIEW_TYPE_ADD_BUTTON_LARGE ||
                viewType == ImageCounterAdapter.VIEW_TYPE_ADD_BUTTON_SMALL) {

            view.setOnClickListener(uploadImageCallback);
        }
    };


    private void selectImage() {

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        selectImageIntent.setType("image/");

        Intent chooserIntent = Intent.createChooser(selectImageIntent,"Choose an Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{getContentIntent});

        pickImageResultLauncher.launch(chooserIntent);
    }



    private final ActivityResultCallback<ActivityResult> activityResultCallback = result -> {

        if(result.getData()!=null && result.getData().getData()!= null) {

            imageResources.add(result.getData().getData());
            uploadedImageAdapter.addImages(imageResources);
            imageCounterAdapter.setCount(uploadedImageAdapter.getItemCount());

            binding.rvUploadedImages.setVisibility(View.VISIBLE);
        }
    };

    private final TextRecognitionManager.ProcessCallback processCallback = new TextRecognitionManager.ProcessCallback() {
        @Override
        public void onComplete(Text text) {

            if (text.getTextBlocks().isEmpty()) {
                binding.tvResultText.setText("No text found.");
                return;
            }

            for (Text.TextBlock block : text.getTextBlocks()) {
                for (Text.Line line : block.getLines()) {
                    convertedText.append(line.getText()).append("\n"); // Add each line of text with a newline
                }
                convertedText.append("\n"); // Separate blocks with an extra newline
            }

            binding.tvResultText.setText(convertedText.toString());
        }

        @Override
        public void onFailure(Exception e) {

            binding.tvResultText.setText("Error Occured While Retrieving Text");
        }
    };

    public void copyToClipboard(Context context, String textToCopy) {
        // Get the Clipboard Manager
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // Create a ClipData object with the text
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);

        // Set the clip data to the clipboard
        clipboard.setPrimaryClip(clip);

        // Optional: Show a confirmation
        Toast.makeText(context, "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}