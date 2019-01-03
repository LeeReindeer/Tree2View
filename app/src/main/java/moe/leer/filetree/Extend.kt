/*
 *       Copyright 2017 LeeReindeer <reindeerlee.work@gmail.com>
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */

package moe.leer.filetree

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.webkit.MimeTypeMap
import android.widget.Toast
import moe.leer.filetree.module.FileItem
import java.io.File


fun Activity.requestPermission(permission: String, requestCode: Int) {
  ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}

fun Activity.openFile(fileItem: FileItem) {
  //exposure file uri
  if (Build.VERSION.SDK_INT >= 24) {
    try {
      val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
      m.invoke(null)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
  val file = File(fileItem.absName)
  val myMime = MimeTypeMap.getSingleton()
  val newIntent = Intent(Intent.ACTION_VIEW)
  val mimeType = myMime.getMimeTypeFromExtension(file.extension)
  newIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
  newIntent.setDataAndType(Uri.fromFile(file), mimeType)


  try {
    startActivity(newIntent)
  } catch (e: ActivityNotFoundException) {
    Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show()
  }
}

fun File.isHideFile(): Boolean {
  return (this.name.indexOf(".") == 0)
}

fun File.copyTo(file: File) {
  this.inputStream().use { input ->
    file.outputStream().use { output ->
      input.copyTo(output)
    }
  }
}

fun File.moveTo(file: File) {
  //copy then delete this
  copyTo(file)
  this.delete()
}

fun File.getPrefixPath(): String {
  val abPath = this.absolutePath
  val index = abPath.lastIndexOf("/")
  //append "/" at last
  return abPath.substring(0, index + 1)
}

fun File.getNameWithoutType(): String {
  val fileName = this.name
  val index = fileName.lastIndexOf(".")
  if (index == -1 || index == 0) {
    return fileName
  }
  return fileName.substring(0, index)
}

fun File.getFileType(): String {
  val fileName = this.name
  val index = fileName.lastIndexOf(".")
  if (index == -1) {
    return ""
  }
  return fileName.substring(index + 1, fileName.length)
}