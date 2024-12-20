package in.amankumar110.ocrapp.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.amankumar110.ocrapp.R;
import in.amankumar110.ocrapp.databinding.FeatureItemLayoutBinding;
import in.amankumar110.ocrapp.enums.FeatureType;
import in.amankumar110.ocrapp.models.Feature;

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.FeatureViewHolder> {

    private List<Feature> featureList = new ArrayList<>();
    private static final int FEATURE_COUNT = FeatureType.FEATURE_COUNT;
    private Context context;
    private View.OnClickListener clickCallback;

    public FeaturesAdapter(Context context,View.OnClickListener clickCallback) {
        this.context = context;
        this.clickCallback = clickCallback;
        this.featureList = FeatureType.getAppFeatures(context);
    }


    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeatureViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feature_item_layout,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {

        // Setting background color
        ((GradientDrawable)holder.binding.getRoot().getBackground()).setColor(getColorAt(position,context));

        // Setting data
        Feature feature = featureList.get(position);
        holder.binding.tvFeatureTitle.setText(feature.getTitle());
        holder.binding.tvFeatureDescription.setText(feature.getDescription());
        holder.binding.ivFeatureIcon.setImageResource(feature.getImageSource());

        // Adding on Click listener
        holder.binding.getRoot().setOnClickListener(clickCallback);

    }

    @Override
    public int getItemCount() {
        return FEATURE_COUNT;
    }

    private int getColorAt(int position, Context context) {
        int[] colors = {
                R.color.colorGreen80,  // For position % 4 == 0
                R.color.colorYellow80, // For position % 4 == 1
                R.color.colorBlue80,   // For position % 4 == 2
                R.color.colorRed80     // For position % 4 == 3
        };
        return ContextCompat.getColor(context, colors[position % 4]);
    }


    public static class FeatureViewHolder extends RecyclerView.ViewHolder {
        FeatureItemLayoutBinding binding;
        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FeatureItemLayoutBinding.bind(itemView);
        }
    }
}
