package com.nagarjuna_pamu.awesomeness

import java.util.ArrayList

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.widget.{ArrayAdapter, ListView}
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

    listView.setAdapter(new CustomAdapter(getApplicationContext, list))
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.menu_main, menu)
    true
  }
}
