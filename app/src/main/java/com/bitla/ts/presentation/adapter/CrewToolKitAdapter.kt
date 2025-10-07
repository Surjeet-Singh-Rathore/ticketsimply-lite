package com.bitla.ts.presentation.adapter

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCrewToolkitBinding
import com.bitla.ts.domain.pojo.crew_toolkit.StuffGoodsDetail
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import gone
import timber.log.Timber
import visible

class CrewToolKitAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var stuffList: MutableList<StuffGoodsDetail>
) :
    RecyclerView.Adapter<CrewToolKitAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCrewToolkitBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stuffList.size
    }

    fun showPicture(uriList: MutableList<StuffGoodsDetail>) {
        stuffList = uriList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val stuff = stuffList[position]
       // holder.setIsRecyclable(false)
        holder.tvTitle.text = stuff.name
        if (stuff.remarks != "null")
            holder.etRemarks.setText(stuff.remarks)
        else
            holder.etRemarks.setText(context.getString(R.string.empty))

        holder.rgCrewSelection.setOnCheckedChangeListener(null)
        holder.rbYes.isChecked = stuff.is_checked == true
        holder.rbNo.isChecked = stuff.is_checked == false

        if (stuff.stuff_goods_image_details != null && stuff.stuff_goods_image_details.isNotEmpty()) {
            when (stuff.stuff_goods_image_details.size) {
                1 -> {
                    holder.tvCount.gone()
                    setPicture(
                        stuff.stuff_goods_image_details[0].stuff_goods_image,
                        holder.img1
                    )
                    holder.img2.gone()
                    holder.img3.gone()
                }
                2 -> {
                    holder.tvCount.gone()
                    setPicture(
                        stuff.stuff_goods_image_details[0].stuff_goods_image,
                        holder.img1
                    )
                    setPicture(
                        stuff.stuff_goods_image_details[1].stuff_goods_image,
                        holder.img2
                    )
                    holder.img3.gone()
                }
                3 -> {
                    holder.tvCount.gone()
                    setPicture(
                        stuff.stuff_goods_image_details[0].stuff_goods_image,
                        holder.img1
                    )
                    setPicture(
                        stuff.stuff_goods_image_details[1].stuff_goods_image,
                        holder.img2
                    )
                    setPicture(
                        stuff.stuff_goods_image_details[2].stuff_goods_image,
                        holder.img3
                    )
                }
                else -> {
                    setPicture(
                        stuff.stuff_goods_image_details[0].stuff_goods_image,
                        holder.img1
                    )
                    setPicture(
                        stuff.stuff_goods_image_details[1].stuff_goods_image,
                        holder.img2
                    )
                    setPicture(
                        stuff.stuff_goods_image_details[2].stuff_goods_image,
                        holder.img3
                    )
                    holder.tvCount.visible()
                    holder.tvCount.text = "+${stuff.stuff_goods_image_details.size.minus(3)}"
                }
            }
        } else {
            holder.tvCount.gone()
            holder.img1.gone()
            holder.img2.gone()
            holder.img3.gone()
        }
        /* else
         {
             if (stuff.uriImages != null)
             {
                 when (stuff.uriImages.size) {
                     1 -> {
                         holder.tvCount.gone()
                         setUri(Uri.parse(stuff.uriImages[0]), holder.img1)
                     }
                     2 -> {
                         holder.tvCount.gone()
                         setUri(Uri.parse(stuff.uriImages[0]), holder.img1)
                         setUri(Uri.parse(stuff.uriImages[1]), holder.img2)

                     }
                     3 -> {
                         holder.tvCount.gone()
                         setUri(Uri.parse(stuff.uriImages[0]), holder.img1)
                         setUri(Uri.parse(stuff.uriImages[1]), holder.img2)
                         setUri(Uri.parse(stuff.uriImages[2]), holder.img3)
                     }
                     else -> {
                         setUri(Uri.parse(stuff.uriImages[0]), holder.img1)
                         setUri(Uri.parse(stuff.uriImages[1]), holder.img2)
                         setUri(Uri.parse(stuff.uriImages[2]), holder.img3)
                         holder.tvCount.visible()
                         holder.tvCount.text = "+${stuff.uriImages.size.minus(3)}"
                     }
                 }
             }
         }*/

        holder.layoutCaptureImage.setOnClickListener {
            holder.layoutCaptureImage.tag = context.getString(R.string.capture_image)
            onItemClickListener.onClick(holder.layoutCaptureImage, position)
        }

        holder.layoutImages.setOnClickListener {
            holder.layoutImages.tag = context.getString(R.string.crew_images)
            onItemClickListener.onClick(holder.layoutImages, position)
        }

        holder.etRemarks.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                stuff.remarks = text.toString()
            }
        })

        holder.rgCrewSelection.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbYes -> { stuff.is_checked = true }
                R.id.rbNo -> { stuff.is_checked = false }
                else -> { stuff.is_checked = null }
            }
        }
    }


    private fun setPicture(stuffGoodsImageUrl: String, img: ImageView) {
        img.visible()
        Glide.with(context)
            .load(stuffGoodsImageUrl)
            .fitCenter()
            .transform(CenterInside(), RoundedCorners(4))
            .into(img)
    }

    private fun setUri(uri: Uri, img: ImageView) {
        img.visible()
        Glide.with(context)
            .load(uri)
            .fitCenter()
            .into(img)
    }


    class ViewHolder(binding: ChildCrewToolkitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val etRemarks = binding.etRemarks
        val tvTitle = binding.tvTitle
        val layoutCaptureImage = binding.layoutCaptureImage
        val img1 = binding.img1
        val img2 = binding.img2
        val img3 = binding.img3
        val tvCount = binding.tvCount
        val layoutImages = binding.layoutImages
        val rgCrewSelection = binding.rgCrewSelection
        val rbYes = binding.rbYes
        val rbNo = binding.rbNo
    }
}