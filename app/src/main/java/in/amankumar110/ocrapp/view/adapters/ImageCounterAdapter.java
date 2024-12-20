package in.amankumar110.ocrapp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.amankumar110.ocrapp.R;

public class ImageCounterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnCountItemClicked {
        void onClick(int viewType,View view);
    }

    private int count;

    public static final int VIEW_TYPE_COUNT = 91;
    public static final int VIEW_TYPE_ADD_BUTTON_LARGE = 89;
    public static final int VIEW_TYPE_ADD_BUTTON_SMALL = 98;

    private final OnCountItemClicked clickListener;



    public ImageCounterAdapter(int count,OnCountItemClicked clickListener) {
        this.count = count;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == count) {
            // If at the "add button" position
            if (count == 0) {
                return VIEW_TYPE_ADD_BUTTON_LARGE; // Show large button
            } else {
                return VIEW_TYPE_ADD_BUTTON_SMALL; // Show small button
            }
        } else {
            return VIEW_TYPE_COUNT; // For uploaded images
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType== VIEW_TYPE_ADD_BUTTON_LARGE)
            return new AddButtonViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.large_add_button_layout,parent,false));
        else if(viewType == VIEW_TYPE_ADD_BUTTON_SMALL)
            return new AddButtonViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.small_add_button_layout,parent,false));
        else
            return new ImageCounterViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_count_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_COUNT)
            ((TextView) holder.itemView).setText(String.valueOf(position+1));

        holder.itemView.setOnClickListener(v -> {
            clickListener.onClick(getItemViewType(position),v);
            v.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return count+1;
    }

    public int getSize() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    public static class ImageCounterViewHolder extends RecyclerView.ViewHolder {

        public ImageCounterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
