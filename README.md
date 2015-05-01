# ScalaAndroid
Android app development using Scala (Functional and Object oriented language on JVM)

This app uses http://playscalaandroid.herokuapp.com as a web service for fetching information.

## Folder structure of Scala Android App

```
Project Name folder
  | -  src
      | - main
            | - java
            | - scala [scala source files with general java package system]
            | - AndroidManifest.xml
            | - res
                | - drawable
                | - layout
                | - menu
                | - values
                | - standard android res structure
            | - test
  | - project
        | - plugins.sbt

```

## in package com.nagarjuna_pamu.awesomeness

```
| - MainActivity.scala
| - adapter
    | - CustomAdapter.scala
| - utils
    | - Utils.scala
| - contacts
    | - Constants.scla
```

## MainActivity.scala

```scala
    package com.nagarjuna_pamu.awesomeness
    
    import java.io.{InputStreamReader, BufferedReader}
    import java.net.HttpURLConnection
    import java.util.{Scanner, ArrayList}
    
    import android.app.AlertDialog.Builder
    import android.app.{Activity, AlertDialog, ProgressDialog}
    import android.content.DialogInterface.OnClickListener
    import android.content.{DialogInterface, Context}
    import android.os.Bundle
    import android.util.Log
    import android.view.{View, Menu}
    import android.widget._
    import com.nagarjuna_pamu.awesomeness.adapter._
    import com.nagarjuna_pamu.awesomeness.constants.Constants
    import com.nagarjuna_pamu.awesomeness.utils.Utils
    import org.json.JSONObject
    
    import scala.concurrent.Future
    import scala.util.{Failure, Success}
    
    /**
     * Created by pnagarjuna on 29/04/15.
     */
    class MainActivity extends TypedActivity {
    
      override def onCreate(bundle: Bundle): Unit = {
        super.onCreate(bundle)
        setContentView(TR.layout.activity_main.id)
        val listView: ListView = findView[ListView](TR.list_view)
        val list = new ArrayList[Interactable]
        list.add(TextItem(name = "Client", text = "I wanna buy cars !!! Anyone help me decide ... "))
        val adapter = new CustomAdapter(getApplicationContext, list)
        listView.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    
        val editText: EditText = findView[EditText](TR.edit_text)
        val send: Button = findView[Button](TR.send)
    
        list.add(ImageItem(name = "Car", url = "http://media.treehugger.com/assets/images/2011/10/toyota-iq-small-car-001.jpg"))
        adapter.notifyDataSetChanged()
    
        list.add(HtmlItem(name = "Oracle", html = """<h1 style="color: black;">Android</h1>"""))
        adapter.notifyDataSetChanged()
    
        val context: Context = getApplicationContext
    
        send.setOnClickListener(new View.OnClickListener {
          override def onClick(v: View): Unit = {
            val content = editText.getText.toString.trim
            if (content != "") {
              list.add(new TextItem(name = "Client", text = content))
              adapter.notifyDataSetChanged()
              scrollToRecentItem(listView)
              editText.getText.clear()
    
              val dialog = new ProgressDialog(MainActivity.this)
              dialog.setIndeterminate(true)
              dialog.setTitle("Busy")
              dialog.setMessage("fetching info from cloud ...")
              dialog.show()
              import Constants._
              getInfo(content) onComplete {
                case Success(result) =>
                  Log.d("hello", result)
                  val json = new JSONObject(result.asInstanceOf[String])
                  val name = json.getString("name")
                  val url = json.getString("url")
                  if (name != "not_found") {
                    listView.post(new Runnable {
                      override def run(): Unit = {
                        dialog.dismiss()
                        list.add(ImageItem(name, url))
                        adapter.notifyDataSetChanged()
                      }
                    })
                  } else {
                    listView.post(new Runnable {
                      override def run(): Unit = {
                        dialog.dismiss()
                        list.add(new TextItem(name = "Server", text = "Not found in the inventory"))
                        adapter.notifyDataSetChanged()
                      }
                    })
                  }
                case Failure(t) =>
                  listView.post(new Runnable {
                    override def run(): Unit = {
                      alert(s"Failed due to ${t.getMessage}", MainActivity.this)
                      dialog.dismiss()
                    }
                  })
    
                  Log.d("hello", t.getMessage)
              }
    
            } else {
              Toast.makeText(context, "Send Clicked :)", Toast.LENGTH_SHORT).show
            }
        }})
      }
    
      def scrollToRecentItem(listView: ListView): Unit = {
        listView.post(new Runnable {
          override def run(): Unit = {
            listView.setSelection(listView.getAdapter.getCount - 1)
          }
        })
      }
    
      override def onCreateOptionsMenu(menu: Menu): Boolean = {
        super.onCreateOptionsMenu(menu)
        getMenuInflater.inflate(R.menu.menu_main, menu)
        true
      }
    
      def getInfo(car: String): Future[String] = {
        import Constants._
        val future = Future {
          val connection = Utils.query(car).openConnection().asInstanceOf[HttpURLConnection]
          connection.setConnectTimeout(10000)
          connection.setReadTimeout(10000)
          connection.setRequestMethod("GET");
          connection.setRequestProperty("Content-Type", "text/html");
          connection.connect()
          val scan = new Scanner(connection.getInputStream)
          var result = ""
          while (scan.hasNext) {
            result += scan.next()
          }
          result
        }
        future
      }
    
      def alert(msg: String, activity: Activity) = {
        val builder = new Builder(activity)
        builder.setTitle("Message")
        builder.setMessage(msg)
        builder.setPositiveButton("Ok", new OnClickListener {
          override def onClick(dialogInterface: DialogInterface, i: Int): Unit = dialogInterface.dismiss()
        });
        builder.create().show()
      }
    }

````

MainActivity.scala is an Android Activity component that inflates UI (ListView) and allows user to query the server
using Chat like UI.

![UI](https://raw.githubusercontent.com/pamu/ScalaAndroid/master/images/ui.png)

## user asking for swift

![swift](https://raw.githubusercontent.com/pamu/ScalaAndroid/master/images/swift.png)

## fetching from internet

![busy](https://raw.githubusercontent.com/pamu/ScalaAndroid/master/images/busy.png)

## Done fetching

![done](https://raw.githubusercontent.com/pamu/ScalaAndroid/master/images/done.png)


## CustomAdapter.scala

```scala
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
```

## Utils.scala

```scala
    package com.nagarjuna_pamu.awesomeness.utils
    
    import java.net.URL
    
    import android.net.Uri
    import com.nagarjuna_pamu.awesomeness.constants.Constants
    
    /**
     * Created by pnagarjuna on 01/05/15.
     */
    object Utils {
      def query(car: String): URL = new URL(Uri.parse(Constants.infoURL).buildUpon().
        appendQueryParameter("car", car).build().toString)
    }
````

## Constants.scala

```scala
    package com.nagarjuna_pamu.awesomeness.constants

    import java.net.URL

    import android.os.AsyncTask

    import scala.concurrent.ExecutionContext

    /**
     * Created by pnagarjuna on 01/05/15.
     */
    object Constants {
      val infoURL = """http://playscalaandroid.herokuapp.com/info"""
      val testingURL = """http://playscalaandroid.herokuapp.com/info"""
      implicit val exec = ExecutionContext.fromExecutor(
        AsyncTask.THREAD_POOL_EXECUTOR)
    }
```