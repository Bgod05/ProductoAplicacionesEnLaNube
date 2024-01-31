package com.example.museoapp.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.museoapp.R
import com.example.museoapp.model.Culture


class Adapter(
    private val cultureList: ArrayList<Culture>,
    //private val listener: OnItemClickListener
) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.content_culture,
            parent, false
        )
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = cultureList[position]

        holder.bind(currentItem)

    }

    override fun getItemCount(): Int {

        return cultureList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameCulture: TextView = itemView.findViewById(R.id.title_name)
        private val descriptionCulture: TextView = itemView.findViewById(R.id.title_description)
        private val imageCulture: ImageView = itemView.findViewById(R.id.culture_img)


        fun bind(culture: Culture) {
            nameCulture.text = culture.name
            descriptionCulture.text = culture.description
            imageCulture.let {
                Glide.with(itemView.context).load(culture.url).into(it)
            }

/*//            arCulture.setOnClickListener {
//                listener.onItemClick()
//            }*/
        }
    }

/*//    interface OnItemClickListener {
//        fun onItemClick() {
//        }
//    }*/

}