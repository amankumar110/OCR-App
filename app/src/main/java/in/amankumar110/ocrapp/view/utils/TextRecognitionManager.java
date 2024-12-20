package in.amankumar110.ocrapp.view.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class TextRecognitionManager {

    public interface ProcessCallback {
        void onComplete(Text text);
        void onFailure(Exception e);
    }
    private TextRecognizer textRecognizer;
    private Context context;
    private final ProcessCallback processCallback;

    public TextRecognitionManager(Context context, ProcessCallback processCallback)  {
        this.context = context;
        this.processCallback = processCallback;
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    }

    public void process(Uri imageUri) {

        try{
            InputImage image = InputImage.fromFilePath(context,imageUri);
            textRecognizer.process(image)
                    .addOnSuccessListener(processCallback::onComplete)
                    .addOnFailureListener(processCallback::onFailure);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
