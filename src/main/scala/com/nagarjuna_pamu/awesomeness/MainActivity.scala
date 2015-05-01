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
      connection.setConnectTimeout(5000)
      connection.setReadTimeout(5000)
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
