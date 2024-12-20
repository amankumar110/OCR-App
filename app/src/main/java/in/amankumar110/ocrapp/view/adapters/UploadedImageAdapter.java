package in.amankumar110.ocrapp.view.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.amankumar110.ocrapp.R;

public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.UploadedImageViewHolder> {

    private List<Uri> imageResources;

    public UploadedImageAdapter(List<Uri> uris) {
        this.imageResources = uris;
    }

    @NonNull
    @Override
    public UploadedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UploadedImageViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.uploaded_image_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedImageViewHolder holder, int position) {

        holder.imageView.setImageURI(imageResources.get(position));
    }

    @Override
    public int getItemCount() {
        return imageResources.size();
    }

    public void addImages(List<Uri> imageResources) {
        this.imageResources = imageResources;
        notifyDataSetChanged();
    }
    public class UploadedImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        public UploadedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
