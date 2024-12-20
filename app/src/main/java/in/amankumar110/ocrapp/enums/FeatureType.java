package in.amankumar110.ocrapp.enums;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import in.amankumar110.ocrapp.BarcodeScanningFragment;
import in.amankumar110.ocrapp.ImageToTextFragment;
import in.amankumar110.ocrapp.R;
import in.amankumar110.ocrapp.TextToSpeechFragment;
import in.amankumar110.ocrapp.models.Feature;

public enum FeatureType {

    IMAGE_TO_TEXT(R.string.feature_image_to_text_title,R.string.feature_image_to_text_description,R.drawable.image_to_text, ImageToTextFragment.class),

    TEXT_TO_SPEECH(R.string.feature_text_to_speech_title, R.string.feature_text_to_speech_description,R.drawable.text_to_speech, TextToSpeechFragment.class),

    BARCODE_SCANNING(R.string.feature_barcode_scanning_title,R.string.feature_barcode_scanning_description,R.drawable.barcode_scanning, BarcodeScanningFragment.class),
    UPCOMING_FEATURE(R.string.upcoming_features_title,R.string.upcoming_features_description,R.drawable.work_in_progress, null);

    private final int title;
    private final int featureDescription;
    private final int featureImageResource;
    private final Class<? extends Fragment> fragmentClass;

    public static final int FEATURE_COUNT = 4;

    FeatureType(int title,int desc,int imageRes, Class<? extends Fragment> fragmentClass) {
        this.title = title;
        this.featureDescription = desc;
        this.featureImageResource = imageRes;
        this.fragmentClass = fragmentClass;
    }


    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public static Class<? extends Fragment> getFragmentForTitle(Context context,String title) {

        for(FeatureType featureType : values()) {
            String convertedTitle = context.getString(featureType.title);
            if (title.equals(convertedTitle)) {
                return featureType.fragmentClass;
            }
        }
        return null;

    }

    public static List<Feature> getAppFeatures(Context context) {

        List<Feature> featureList = new ArrayList<>();

        for(FeatureType featureType : values()) {
            String title = context.getString(featureType.title);
            String description = context.getString(featureType.featureDescription);
            Feature feature = new Feature(title,description, featureType.featureImageResource);
            featureList.add(feature);
        }

        return featureList;
    }
}
