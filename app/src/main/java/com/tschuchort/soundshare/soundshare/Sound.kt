package com.tschuchort.soundshare.soundshare

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder

data class Sound(val url: String)

open class SoundModel(
        @EpoxyAttribute @JvmField val sound: Sound,
        val onClick: (Sound) -> Unit = {})
    : EpoxyModelWithHolder<SoundModel.Holder>() {

    init {
        id(sound.url)
    }

    override fun getDefaultLayout() = R.layout.sound_item

    override fun bind(holder: Holder?) {
        super.bind(holder)
        holder!!.titleView.text = sound.url
    }

    override fun unbind(holder: Holder?) {
        super.unbind(holder)
        holder!!.titleView.text = ""
    }


    override fun createNewHolder() = Holder()

    class Holder : EpoxyHolder() {
        lateinit var titleView: TextView

        override fun bindView(itemView: View?) {
            titleView = itemView!!.findViewById(R.id.titleView)
        }
    }
}