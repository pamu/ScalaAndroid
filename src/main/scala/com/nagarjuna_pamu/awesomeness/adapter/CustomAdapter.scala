package com.nagarjuna_pamu.awesomeness.adapter

import java.net.URL

import android.content.Context
import android.graphics.{Bitmap, BitmapFactory}
import android.os.AsyncTask
import android.text.Html
import android.util.Log
import android.view.{LayoutInflater, ViewGroup, View}
import android.webkit.WebView
import android.widget.{ListView, ImageView, TextView, BaseAdapter}
import com.nagarjuna_pamu.awesomeness.{TR, TypedResource, TypedLayoutInflater, R}
import android.support.v7.widget.CardView
import java.util.ArrayList

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * Created by pnagarjuna on 30/04/15.
 */
class CustomAdapter(val context: Context, val list: ArrayList[Interactable]) extends BaseAdapter {
  val textItem = 0
  val imageItem = 1
  val htmlItem = 2
  val totalViewLayouts = 3

  override def getViewTypeCount: Int = totalViewLayouts

  override def getItemViewType(position: Int): Int = {
    list.get(position) match {
      case item: TextItem => textItem
      case item: ImageItem => imageItem
      case item: HtmlItem => htmlItem
    }
  }

  override def getCount: Int = list.size()

  override def getItemId(position: Int): Long = position

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val viewType = getItemViewType(position)
    var mConvertView: View = convertView
    val mHolder = new ViewHolder
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    viewType match {
      case `textItem` => {
        if (convertView == null || convertView.getTag.asInstanceOf[ViewHolder].textView == null) {
          val rootView: View = inflater.inflate(R.layout.list_view_item, null)

          mHolder.textView = rootView.findViewById(TR.text_view.id).asInstanceOf[TextView]
          mHolder.nameView = rootView.findViewById(TR.name_text_view.id).asInstanceOf[TextView]
          rootView.setTag(mHolder)

          mConvertView = rootView
        }
        val holder = mConvertView.getTag.asInstanceOf[ViewHolder]
        holder.textView.setText(list.get(position).asInstanceOf[TextItem].text)
        holder.nameView.setText(list.get(position).asInstanceOf[TextItem].name)
        mConvertView
      }
      case `imageItem` => {
        if (convertView == null || convertView.getTag.asInstanceOf[ViewHolder].imageView == null) {
          val rootView: View = inflater.inflate(R.layout.list_view_image_item, null)

          mHolder.imageView = rootView.findViewById(R.id.image_view).asInstanceOf[ImageView]
          rootView.setTag(mHolder)

          mConvertView = rootView
        }
        val holder = mConvertView.getTag.asInstanceOf[ViewHolder]

        //initial loading image
        holder.imageView.setImageResource(R.drawable.loading)
        val imageItem = list.get(position).asInstanceOf[ImageItem]

        implicit val exec = ExecutionContext.fromExecutor(
          AsyncTask.THREAD_POOL_EXECUTOR)

        ImageUtils.getImage(new URL(imageItem.url)) onComplete {
          case Success(image) => holder.imageView.post(new Runnable {
            override def run = holder.imageView.setImageBitmap(image)
          })
          //load error image when failed
          case Failure(t) => holder.imageView.post(new Runnable {
            override def run = holder.imageView.setImageResource(R.drawable.loading)
          })
            Log.d("hello", "failed " + t.getMessage)
        }

        mConvertView
      }
      case `htmlItem` => {
        if (convertView == null || convertView.getTag.asInstanceOf[ViewHolder].htmlView == null) {
          val rootView: View = inflater.inflate(R.layout.list_view_html_item, null)

          mHolder.htmlView = rootView.findViewById(R.id.html_view).asInstanceOf[TextView]
          rootView.setTag(mHolder)

          mConvertView = rootView
        }
        val holder = mConvertView.getTag.asInstanceOf[ViewHolder]
        val htmlItem: HtmlItem = list.get(position).asInstanceOf[HtmlItem]
        holder.htmlView.setText(Html.fromHtml(htmlItem.html))

        mConvertView
      }
    }
  }

  override def getItem(position: Int): AnyRef = list.get(position)

  override def hasStableIds = true
}

class ViewHolder {
  var nameView: TextView = _
  var textView: TextView = _
  var imageView: ImageView = _
  var htmlView: TextView = _
}

trait Interactable {
  val name: String
}

case class TextItem(name: String, text: String) extends Interactable

case class ImageItem(name: String, url: String) extends Interactable

case class HtmlItem(name: String, html: String) extends Interactable

object ImageUtils {
  def getImage(url: URL): Future[Bitmap] = {
    implicit val exec = ExecutionContext.fromExecutor(
      AsyncTask.THREAD_POOL_EXECUTOR)
    val future = Future {
      val imageStream = url.openStream()
      val image = BitmapFactory.decodeStream(imageStream)
      Log.d("hello", "downloading...")
      image
    }
    future
  }
}