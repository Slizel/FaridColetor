import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import faridnet.com.faridcoletor.Fragments.List.ListFragmentDirections
import faridnet.com.faridcoletor.Model.Contagens
import faridnet.com.faridcoletor.Model.JoinContagemProduto
import faridnet.com.faridcoletor.R
import kotlinx.android.synthetic.main.custom_row.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var joinContagemProdutoList = emptyList<JoinContagemProduto>()
    //private var contagensList = emptyList<Contagens>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return joinContagemProdutoList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //val contagensItem = contagensList[position]
        //holder.itemView.qtdeTextView.text = contagensItem.quantidade.toString()

        val joinContagemProdutoItem = joinContagemProdutoList[position]
        holder.itemView.codBarrasTextView.text = joinContagemProdutoItem.produtoId.toString()
        holder.itemView.descricaoTextView.text = joinContagemProdutoItem.produtoDescricao
        holder.itemView.qtdeTextView.text = joinContagemProdutoItem.contagemQuantidade.toString()






        holder.itemView.rowLayout.setOnClickListener {

            val action =
                ListFragmentDirections.actionListFragmentToUpdateFragment(joinContagemProdutoItem)
                //ListFragmentDirections.actionListFragmentToUpdateFragment(contagensItem)
            holder.itemView.findNavController().navigate(action)
        }


    }

    fun setJoinContagensData(joinContagemProduto: List<JoinContagemProduto>) {
        this.joinContagemProdutoList = joinContagemProduto
        notifyDataSetChanged()
    }

    //fun setContagensData(contagens: List<Contagens>) {
    //    this.contagensList = contagens
    //    notifyDataSetChanged()
    //}
}