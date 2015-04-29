package com.nagarjuna_pamu.awesomeness

import android.app.Activity
import android.os.Bundle

/**
 * Created by pnagarjuna on 29/04/15.
 */
class MainActivity extends Activity with TypedViewHolder {
  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.activity_main)
  }
}
