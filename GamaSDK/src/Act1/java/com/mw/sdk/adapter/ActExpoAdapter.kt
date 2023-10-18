/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mw.sdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.mw.sdk.R
import com.mw.sdk.data.ActData

class ActExpoAdapter(val context: Context?, val actDatas: ArrayList<ActData>) : RecyclerView.Adapter<ActExpoViewHolder>() {

    val webViewMap = HashMap<Int,WebView> ()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActExpoViewHolder {
        val mLayoutInflater = LayoutInflater.from(context)
        val itemView = mLayoutInflater.inflate(R.layout.activity_act_item, parent, false)
        return ActExpoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActExpoViewHolder, position: Int) {

        val actData = actDatas[position]
        holder.actWebView.loadUrl(actData.contentImgUrl)
        webViewMap[position] = holder.actWebView
    }

    override fun getItemCount(): Int {
        return actDatas.size
    }
}

class ActExpoViewHolder internal constructor(private val itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    val actWebView: WebView = itemView.findViewById(R.id.mw_act_wv_item)

}
