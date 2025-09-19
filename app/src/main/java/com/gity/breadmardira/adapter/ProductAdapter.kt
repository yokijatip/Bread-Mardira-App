import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gity.breadmardira.databinding.ItemProductBinding
import com.bumptech.glide.Glide
import com.gity.breadmardira.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvName.text = product.name
            binding.tvPrice.text = "Rp ${product.price}"
            Glide.with(binding.root.context)
                .load(product.imageRes)
                .into(binding.imgProduct)

            binding.root.setOnClickListener { onItemClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}
