package com.hdarha.happ.adapters

import android.app.Activity
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.R
import com.hdarha.happ.other.inflate
import com.hdarha.happ.objects.SettingItem

class SettingsAdapter(
    private val settings: ArrayList<SettingItem>,
    private val activity: Activity
) :
    RecyclerView.Adapter<SettingsAdapter.SettingsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsHolder {
        val inflatedView = parent.inflate(R.layout.view_settings_item, false)
        return SettingsHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(holder: SettingsHolder, position: Int) {
        val settingItem = settings[position]
        holder.bindItem(settingItem, activity)
    }


    inner class SettingsHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        //private var drawableId: String? = null


        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Toast.makeText(this@SettingsAdapter.activity,"You clicked me ${adapterPosition}", Toast.LENGTH_SHORT).show()
        }

        fun bindItem(settingItem: SettingItem, activity: Activity) {
            val inflated = view

            val titleTextView = inflated.findViewById<TextView>(R.id.setting_title)

            val iconImageView = inflated.findViewById<ImageView>(R.id.setting_end_ic)
            val endLayout = inflated.findViewById<LinearLayout>(R.id.EndIconsLayout)
            titleTextView.text = settingItem.title
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(settingItem.textDrawable,0,0,0)

            if (settingItem.endButtons != null && settingItem.endButtons.size > 1) {
                Log.d("Setting","PART1 $adapterPosition")
                for ((index,img)in settingItem.endButtons.withIndex()) {
                    val imgView = ImageView(activity)
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.weight = 0f
                    lp.setMargins(5,0,5,0)
                    imgView.layoutParams = lp
                    imgView.setImageResource(img)
                    imgView.setColorFilter(Color.GRAY)
                    imgView.setOnClickListener { Toast.makeText(activity,"You clicked image n $index",Toast.LENGTH_SHORT).show() }
                    endLayout.addView(imgView)
                }

            } else {

                if (settingItem.endButtons != null) {
                    iconImageView.setImageResource(settingItem.endButtons[0])
                } else{
                    iconImageView.visibility = View.GONE
                }
            }




            if (settingItem.endDrawables != null) {
                for (mView in settingItem.endDrawables) {
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.weight = 0f
                    view.layoutParams = lp
                    endLayout.addView(mView)
                }
            }



            //galleryImageView.setImageResource(drawableId!!)

        }
    }

}