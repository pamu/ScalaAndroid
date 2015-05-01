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
