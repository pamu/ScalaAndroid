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
