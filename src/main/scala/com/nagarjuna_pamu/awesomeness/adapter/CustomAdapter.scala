package com.nagarjuna_pamu.awesomeness.adapter

import android.view.{ViewGroup, View}
import android.widget.BaseAdapter

/**
 * Created by pnagarjuna on 30/04/15.
 */
class CustomAdapter extends BaseAdapter {
  val textItem = 1
  val webItem = 2
  val imageItem = 2
  val totalViewLayouts = 3

  override def getCount: Int = ???

  override def getItemId(position: Int): Long = ???

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = ???

  override def getItem(position: Int): AnyRef = ???
}
