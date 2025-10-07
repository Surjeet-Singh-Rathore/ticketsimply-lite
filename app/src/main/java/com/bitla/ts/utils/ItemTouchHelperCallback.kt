import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.presentation.adapter.RouteManager.EditRouteViaCitiesAdapter
import com.google.android.material.snackbar.Snackbar

class ItemTouchHelperCallback(
    private val adapter: EditRouteViaCitiesAdapter,
    val viaCitiesList: ArrayList<ViaCitiesData>,
    val citiesList: ArrayList<CitiesListData>
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val position = viewHolder.adapterPosition
        val itemCount = recyclerView.adapter?.itemCount ?: 0
        val swipeFlags = if (position == 0 || position == itemCount - 1) {
            0 // No swipe
        } else {
            ItemTouchHelper.START // Allow swipe in both directions
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = source.adapterPosition
        val toPosition = target.adapterPosition



        if (fromPosition == 0 || fromPosition == recyclerView.getAdapter()!!
                .getItemCount() - 1 ||
            toPosition == 0 || toPosition == recyclerView.getAdapter()!!
                .getItemCount() - 1
        ) {
            return false;
        }

        adapter.onItemMove(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val obj = viaCitiesList[position]
        if (direction == ItemTouchHelper.START) {
            for (i in 0 until citiesList.size){
                if(citiesList[i].id == viaCitiesList[position].id){
                    citiesList[i].isSelected = false
                }
            }
            val tempData = CitiesListData()
            tempData.id = obj.id
            tempData.name = obj.name
            tempData.isSelected = false
            citiesList.add(0,tempData)

            viaCitiesList.removeAt(position)
            adapter?.notifyItemRemoved(position)
            adapter?.notifyItemRangeChanged(
                position,
                viaCitiesList.size - position
            )
            Snackbar.make(viewHolder.itemView, "City removed", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    viaCitiesList.add(position, obj)
                    for (i in 0 until citiesList.size){
                        if(citiesList[i].id == viaCitiesList[position].id){
                            citiesList[i].isSelected = true
                        }
                    }
                    citiesList.removeIf {
                        it.id == viaCitiesList[position].id
                    }
                    adapter?.notifyItemInserted(position)
                    adapter?.notifyItemRangeChanged(
                        position,
                        viaCitiesList.size - position
                    )

                }.show()
        }


        // No-op since swipe actions are not handled here
    }
}
