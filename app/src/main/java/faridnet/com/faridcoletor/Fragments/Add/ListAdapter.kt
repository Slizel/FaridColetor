package faridnet.com.faridcoletor.Fragments.Add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import faridnet.com.faridcoletor.Data.ContagnesData.Contagens
import faridnet.com.faridcoletor.Data.ProdutosData.Produtos
import faridnet.com.faridcoletor.R
import kotlinx.android.synthetic.main.custom_row.view.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var contagensList = emptyList<Contagens>()
    private var produtosList = emptyList<Produtos>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }


    override fun getItemCount(): Int {
        return contagensList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val contagensItem = contagensList[position]
        val produtosItem = produtosList[position]
        holder.itemView.codInternoTextView.text = produtosItem.produtoId.toString()
        holder.itemView.codBarrasTextView.text = produtosItem.codBarras
        holder.itemView.qtdeTextView.text = contagensItem.quantidade.toString()
        holder.itemView.codBarrasTextView.text = produtosItem.descricao
    }


    fun setContagensData(contagens: List<Contagens>){
        this.contagensList = contagens
        notifyDataSetChanged()
    }

    fun setProdutosData(produtos: List<Produtos>){
        this.produtosList = produtos
        notifyDataSetChanged()
    }



}