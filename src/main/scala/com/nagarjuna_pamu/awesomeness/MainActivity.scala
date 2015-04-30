package com.nagarjuna_pamu.awesomeness

import java.util.ArrayList

import android.app.Activity
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.view.{View, Menu}
import android.widget._
import com.nagarjuna_pamu.awesomeness.adapter.{TextItem, Interactable, CustomAdapter}

/**
 * Created by pnagarjuna on 29/04/15.
 */
class MainActivity extends TypedActivity {

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(TR.layout.activity_main.id)
    val listView: ListView = findView[ListView](TR.list_view)
    val list = new ArrayList[Interactable]
    list.add(new TextItem(name = "text item", text = "hey hello"))
    list.add(new TextItem(name = "text item", text = "hey hello"))
    list.add(new TextItem(name = "text item", text = "hey hello"))
    val adapter = new CustomAdapter(getApplicationContext, list)
    listView.setAdapter(adapter)
    list.add(new TextItem(name = "text item1", text = "hey hello1"))
    adapter.notifyDataSetChanged()

    val editText: EditText = findView[EditText](TR.edit_text)
    val send: Button = findView[Button](TR.send)

    val context: Context = getApplicationContext

    send.setOnClickListener(new View.OnClickListener {
      override def onClick(v: View): Unit = {
        val content = editText.getText.toString.trim
        if (content != "") {
          list.add(new TextItem(name = content, text = content))
          adapter.notifyDataSetChanged()
          scrollToRecentItem(listView)
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
