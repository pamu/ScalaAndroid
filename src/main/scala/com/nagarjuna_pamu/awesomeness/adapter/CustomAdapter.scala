package com.nagarjuna_pamu.awesomeness.adapter

import android.content.Context
import android.view.{LayoutInflater, ViewGroup, View}
import android.webkit.WebView
import android.widget.{ListView, ImageView, TextView, BaseAdapter}
import com.nagarjuna_pamu.awesomeness.{TR, TypedResource, TypedLayoutInflater, R}
import android.support.v7.widget.CardView
import java.util.ArrayList

/**
 * Created by pnagarjuna on 30/04/15.
 */
class CustomAdapter(val context: Context, val list: ArrayList[Interactable]) extends BaseAdapter {
  val textItem = 0
  val imageItem = 1
  val webItem = 2
  val totalViewLayouts = 3

  override def getViewTypeCount: Int = totalViewLayouts

  override def getItemViewType(position: Int): Int = {
    list.get(position) match {
      case item: TextItem => textItem
      case item: ImageItem => imageItem
      case item: WebItem => webItem
    }
  }

  override def getCount: Int = list.size()

  override def getItemId(position: Int): Long = position

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val viewType = getItemViewType(position)
    var appropriateView: Option[View] = None
    viewType match {
      case `textItem` => {
        if (convertView != null) {
          appropriateView = Some(convertView)
        } else {
          val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
          val view: View = inflater.inflate(R.layout.list_view_item, null)
          appropriateView = Some(view)
        }
        appropriateView.map(view => {
          val textView: TextView = view.findViewById(TR.text_view.id).asInstanceOf[TextView]
          textView.setText(list.get(position).name)
        })
      }
      case `imageItem` => {

      }
      case `webItem` => {

      }
    }
    return appropriateView.getOrElse(null)
  }

  override def getItem(position: Int): AnyRef = list.get(position)

  override def hasStableIds = true
}

case class ViewHolder(var textView: TextView, var imageView: ImageView, var webView: WebView)

trait Interactable {
  val name: String
}

case class TextItem(name: String, text: String) extends Interactable

case class ImageItem(name: String, url: String) extends Interactable

case class WebItem(name: String, url: String) extends Interactable
