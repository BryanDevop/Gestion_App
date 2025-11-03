package com.boxing.gestioncanina.ui.dashboard

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val horizontalSpaceWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // No agregar espacio al primer item
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.left = horizontalSpaceWidth
        }
    }
}
