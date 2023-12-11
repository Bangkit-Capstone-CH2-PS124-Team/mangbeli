import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mangbeli.R
import com.capstone.mangbeli.model.Food

class FoodAdapter(private val foodList: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.food_image)
        val foodName: TextView = itemView.findViewById(R.id.food_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = foodList[position]
                    clickedItem.isSelected = !clickedItem.isSelected
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fav_item, parent, false)
        return FoodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = foodList[position]
        holder.foodImage.setImageResource(currentItem.imageResource)
        holder.foodName.text = currentItem.name

        // Menetapkan latar belakang berdasarkan isSelected
        val backgroundDrawable = if (currentItem.isSelected) {
            R.drawable.backgroud_selected // Ganti dengan drawable background_selected
        } else {
            R.drawable.backgroud_unselected // Ganti dengan drawable background_unselected
        }
        holder.itemView.setBackgroundResource(backgroundDrawable)

    }


    override fun getItemCount() = foodList.size

    fun getSelectedFoodNames(): List<String> {
        val selectedFoodNames = mutableListOf<String>()
        for (food in foodList) {
            if (food.isSelected) {
                selectedFoodNames.add(food.name)
            }
        }
        return selectedFoodNames
    }
}
