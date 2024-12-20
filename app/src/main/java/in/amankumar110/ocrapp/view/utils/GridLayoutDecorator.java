package in.amankumar110.ocrapp.view.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutDecorator extends RecyclerView.ItemDecoration {

    private final int surroundingSpacing;
    private final int itemSpacing;

    public GridLayoutDecorator(int surroundingSpacing, int itemSpacing) {
        this.surroundingSpacing = surroundingSpacing;
        this.itemSpacing = itemSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Get item position

        // Add the surrounding spacing on the left and top for the first row and first column
        if (position == 0) {
            outRect.left = surroundingSpacing;
            outRect.top = surroundingSpacing;
        } else {
            outRect.left = itemSpacing / 2; // Add half of itemSpacing to the left
            outRect.top = itemSpacing / 2;  // Add half of itemSpacing to the top
        }

        // Add the surrounding spacing on the right and bottom for the last row and last column
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.right = surroundingSpacing;
            outRect.bottom = surroundingSpacing;
        } else {
            outRect.right = itemSpacing / 2; // Add half of itemSpacing to the right
            outRect.bottom = itemSpacing / 2; // Add half of itemSpacing to the bottom
        }

        // Adjust the horizontal and vertical spacing between columns
        if (position % 2 == 0) {
            outRect.right = itemSpacing;  // Adjust spacing between columns
        } else {
            outRect.left = itemSpacing;   // Adjust spacing between columns
        }
    }
}
