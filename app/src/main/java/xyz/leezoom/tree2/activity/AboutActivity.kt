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

package xyz.leezoom.tree2.activity

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.*
import xyz.leezoom.tree2.R

class AboutActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about)
    about_version.text = "v" + getVersionName()
    about_page.setOnClickListener {
      browse(about_page.text.toString())
    }
    about_me.setOnClickListener {
      browse("https://t.me/LeeReindeer")
    }

    about_icon.setOnClickListener {
      toast("You find me!\nHa ha...")
      browse("http://leezoom.xyz/2017/12/14/my_tree_view/")
    }
  }

  private fun getVersionName(): String {
    val manager = this.packageManager
    var info: PackageInfo? = null

    try {
      info = manager.getPackageInfo(this.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }

    return info!!.versionName
  }
}
