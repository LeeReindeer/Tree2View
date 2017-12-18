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

package xyz.leezoom.tree2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import xyz.leezoom.tree2.module.FileItem
import java.io.File


fun Activity.requestPermission(permission: String, requestCode: Int) {
  ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}

fun Activity.openFile(fileItem: FileItem) {
  val myIntent = Intent(Intent.ACTION_VIEW)
  myIntent.data = Uri.fromFile(File(fileItem.absName))
  val intent = Intent.createChooser(myIntent, "Choose an application to open with:")
  startActivity(intent)
}

fun  File.isHideFile(): Boolean {
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