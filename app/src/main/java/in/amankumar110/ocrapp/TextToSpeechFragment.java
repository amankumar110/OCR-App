package in.amankumar110.ocrapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.Locale;

import in.amankumar110.ocrapp.databinding.FragmentTextToSpeechBinding;

public class TextToSpeechFragment extends Fragment {


    private FragmentTextToSpeechBinding binding;
    private TextToSpeech textToSpeech;
    private RequestManager glide;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_text_to_speech, container, false);
        binding = FragmentTextToSpeechBinding.bind(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {}

                    @Override
                    public void onDone(String utteranceId) {
                        hidePauseButton();
                    }

                    @Override
                    public void onError(String utteranceId) {
                        textToSpeech.speak(getString(R.string.error_to_speak_message),TextToSpeech.QUEUE_FLUSH,null,utteranceId);
                    }
                });
            }
        });

        glide = Glide.with(requireContext());

        binding.btnSpeak.setOnClickListener(onSpeakButtonClicked);
        binding.btnPause.setOnClickListener(onPauseButtonClicked);
    }

    private final View.OnClickListener onSpeakButtonClicked = v -> {

        String text = binding.etInputText.getText().toString();
        String uniqueId = System.currentTimeMillis()+"";

        binding.etInputText.clearFocus();

        if(text.trim().isEmpty()) {

            String noTextMessage = getString(R.string.no_text_to_speak);
            textToSpeech.speak(noTextMessage,TextToSpeech.QUEUE_FLUSH,null,uniqueId);
        } else if(text.length() > TextToSpeech.getMaxSpeechInputLength()) {

            String longTextMessage = getString(R.string.long_text_message);
            textToSpeech.speak(longTextMessage,TextToSpeech.QUEUE_FLUSH,null,uniqueId);
        } else {
            textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,uniqueId);
            glide.asGif().load(R.drawable.pause_animation).into(binding.btnPause);
            binding.btnPause.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(requireContext(),R.anim.fade_in);
            binding.btnPause.setAnimation(animation);
        }
    };

    private final View.OnClickListener onPauseButtonClicked = v -> {
        textToSpeech.stop();
        binding.etInputText.setText("");
        binding.etInputText.clearFocus();
        hidePauseButton();
    };

    private void hidePauseButton() {
        Animation animation = AnimationUtils.loadAnimation(requireContext(),R.anim.fade_out);
        binding.btnPause.setAnimation(animation);
        requireActivity().runOnUiThread(() -> binding.btnPause.setVisibility(View.GONE));
    }
}