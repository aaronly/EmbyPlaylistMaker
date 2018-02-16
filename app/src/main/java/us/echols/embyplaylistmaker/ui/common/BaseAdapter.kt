package us.echols.embyplaylistmaker.ui.common

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.View

abstract class BaseAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private val selectedItems = SparseBooleanArray()

    private lateinit var clickListener: OnItemClickListener
    private lateinit var longClickListener: OnItemLongClickListener

    private val selectedItemCount: Int
        get() = selectedItems.size()

    private val selectedIndexes: List<Int>
        get() {
            val items = ArrayList<Int>(selectedItems.size())
            (0 until selectedItems.size()).mapTo(items) { selectedItems.keyAt(it) }
            return items
        }

    fun isSelected(position: Int): Boolean {
        return selectedIndexes.contains(position)
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun clearSelections() {
        val selectedIndexes = selectedIndexes
        selectedItems.clear()
        for (i in selectedIndexes) {
            notifyItemChanged(i)
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onLongClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.longClickListener = listener
    }

    open inner class ViewHolder protected constructor(listView: View) :
            RecyclerView.ViewHolder(listView) {

        init {
            listView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener.onClick(adapterPosition)
                }
            }

            listView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    longClickListener.onLongClick(adapterPosition)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }
        }
    }


}
