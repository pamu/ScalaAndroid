package com.nagarjuna_pamu.awesomeness

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.widget.{ArrayAdapter, ListView}

/**
 * Created by pnagarjuna on 29/04/15.
 */
class MainActivity extends Activity with TypedViewHolder {
  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.activity_main)
    val listView: ListView = findViewById(R.id.list_view).asInstanceOf[ListView]

    listView.setAdapter(new ArrayAdapter[String](getApplicationContext, R.layout.list_view_item, R.id.text_view, Array("java", "scala")))
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.menu_main, menu)
    true
  }
}
