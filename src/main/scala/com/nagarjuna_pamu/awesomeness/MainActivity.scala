package com.nagarjuna_pamu.awesomeness

import java.util.ArrayList

import android.content.Context
import android.os.Bundle
import android.view.{View, Menu}
import android.widget._
import com.nagarjuna_pamu.awesomeness.adapter._

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
}
