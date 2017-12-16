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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import xyz.leezoom.tree2.*
import xyz.leezoom.tree2.module.FileItem
import xyz.leezoom.tree2.module.FileTreeAdapter
import xyz.leezoom.tree2.module.FileUtils
import xyz.leezoom.view.treeview.TreeUtils
import xyz.leezoom.view.treeview.module.DefaultTreeNode
import java.io.File

@Suppress("PrivatePropertyName", "UNUSED_ANONYMOUS_PARAMETER")
class MainActivity : AppCompatActivity() {

  private val TAG = "MainActivity"
  private val WRITE_EXTERNAL_STORAGE = 1

  private val INNER_STORAGE = Environment.getExternalStorageDirectory().absolutePath

  private val fileOps = listOf("Copy","Cut", "Rename", "Delete")

  private var lastSelectedNode: DefaultTreeNode<FileItem> ? = null
  private var lastClickedFile = ""
  //0 -> select, 1 -> copy, 2 -> cut
  private var selectedMod = 0
  private var hideMod = true

  private var adapter: FileTreeAdapter? = null
  private var root: DefaultTreeNode<FileItem> = DefaultTreeNode(FileItem(File(INNER_STORAGE)))
  //<hash, count>
  private var clickCount = HashMap<Int, Int>()

  private var lastPress: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    } else {
      initView()
    }

    val list = TreeUtils.getVisibleNodesB(root)
    //for (item in list)  Log.w(TAG, "bfs node " + (item.element).toString())
  }

  private fun initRoot() {
    //expand root
    root.removeAllChildren()
    root.isExpanded = true
    root.element.name = "Internal storage"
    root.isSelectable = false
    val rootDir = File(INNER_STORAGE)
    val list = rootDir.listFiles()
    list.sort()
    //do not rm root node's children or add children
    clickCount.put(root.hashCode(), 1)
    for (item in list) {
      if (!hideMod) {
        root.addChild(DefaultTreeNode<FileItem>(FileItem(item)))
      } else {
        if (!item.isHideFile()) {
          root.addChild(DefaultTreeNode<FileItem>(FileItem(item)))
        }
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun initView() {
    initRoot()
    adapter = FileTreeAdapter(this@MainActivity, root, R.layout.layout_file_tree_item)
    tree_view.treeAdapter = adapter
    tree_view.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
      run {
        val nodes = adapter!!.nodesList
        //the click item
        val node: DefaultTreeNode<FileItem> = nodes[position] as DefaultTreeNode<FileItem>
        val c = if (clickCount[node.hashCode()] == null) 0 else clickCount[node.hashCode()]
        clickCount.put(node.hashCode(), c!! + 1)
        //toggle
        if (node.isExpanded) {
          node.isExpanded = false
          Log.d(TAG, "onItemClick: close")
          //only remove and add node when first time click.
          if (c == 0) {
            node.removeAllChildren()
          }
        } else {
          node.isExpanded = true
          if (c == 0) {
            createNode(node)
          }
          adapter!!.root = root
          Log.d(TAG, "onItemClick: open")
        }
        //only notify when you try to open an empty folder
        if (!node.isHasChildren && node.isExpandable && node.isExpanded) {
          toast("Empty folder")
        } else if (!node.isHasChildren && !node.isExpandable){
          //open file in other app
          val fileItem: FileItem = node.element
          toast("Opening...")
          openFile(fileItem)
        }
        adapter!!.nodesList = TreeUtils.getVisibleNodesD(root)
        //adapter!!.nodesList = nodes
        tree_view.refresh(null)
        //adapter!!.notifyDataSetChanged()
      }
    }
    tree_view.setTreeItemSelectedListener { _, node, pos ->
      val list = TreeUtils.getVisibleNodesD(root)
      val fileItem: FileItem = list[pos].element as FileItem
      val file = File(fileItem.absName)
      toast("You selected " + fileItem.name)
      if (node.isSelectable) {
        when (selectedMod) {
          0 -> {
            selector("File operations", fileOps, { dialogInterface, i ->
              run {
                lastSelectedNode = node as DefaultTreeNode<FileItem>?
                when (i) {
                  0 -> {
                    Log.d(TAG, "Copy")
                    selectedMod = 1
                    lastClickedFile = fileItem.absName
                  }
                  1 -> {
                    Log.d(TAG, "Cut")
                    selectedMod = 2
                    lastClickedFile = fileItem.absName
                  }
                  2 -> {
                    Log.d(TAG, "Rename")
                    var reName: String
                    alert {
                      customView {
                        title = "Rename:"
                        //val edit = EditText(applicationContext)
                        val editor = editText()
                        editor.setText(fileItem.name)
                        okButton {
                          reName = if (file.getFileType().isNotEmpty()) {
                            editor.text.toString() + "" + file.getFileType()
                          } else {  //no file type
                            editor.text.toString()
                          }
                          if (reName.isNotEmpty()) {
                            FileUtils.doRename(fileItem.absName, reName)
                            fileItem.name = reName
                            list[pos].element = fileItem
                            tree_view.refresh(list)
                          } else {
                            toast("Can't rename")
                          }
                        }
                        cancelButton { }
                      }
                    }.show()
                  }
                  3 -> {
                    Log.d(TAG, "Delete")
                    //File(fileItem.absName).delete()
                    alert("Delete ${fileItem.name}") {
                      yesButton {
                        FileUtils.doDelete(fileItem.absName)
                        node.removeThis()
                        list.removeAt(pos)
                        tree_view.refresh(list)
                      }
                      noButton { }
                    }.show()
                  }
                  else -> {
                  }
                }
              }
            })
          }
        //select copy dest dir
          1 -> {
            if (lastClickedFile.isNotEmpty() && fileItem.isDir) {
              val source = File(lastClickedFile)
              val dest = File(file, source.name)
              source.copyTo(dest)
              val newNode = DefaultTreeNode(FileItem(source))
              node.addChild(newNode)
              list.add(newNode)
              list.remove(lastSelectedNode)
              lastSelectedNode!!.removeThis()
              tree_view.refresh(list)
            } else {
              toast("Can't copy")
            }
            lastClickedFile = ""
            selectedMod = 0
          }
        //select cut dest dir
          2 -> {
            if (lastClickedFile.isNotEmpty() && fileItem.isDir) {
              val source = File(lastClickedFile)
              val dest = File(file, source.name)
              source.moveTo(dest)
              val newNode = DefaultTreeNode(FileItem(source))
              node.addChild(newNode)
              list.add(newNode)
              list.remove(lastSelectedNode)
              lastSelectedNode!!.removeThis()
              tree_view.refresh(list)
            } else {
              toast("Can't move")
            }
            lastClickedFile = ""
            selectedMod = 0
          }
        }
      }
      false
    }
    tree_view.visibility = VISIBLE
    refresh_view.setOnRefreshListener {
      refreshTree()
      refresh_view.isRefreshing = false
    }
  }

  private fun refreshTree() {
    tree_view.visibility = GONE
    tree_view.treeAdapter.nodesList.clear()
    //adapter!!.notifyDataSetChanged()
    tree_view.refresh(null)
    initView()
  }

  private fun createNode(aNode: DefaultTreeNode<FileItem>) {
    val thisFile = File(aNode.element.absName)
    if (thisFile.listFiles() != null && thisFile.listFiles().isNotEmpty()) {
      val itemList = thisFile.listFiles()
      itemList.sort()
      for (file in itemList) {
        val n = DefaultTreeNode<FileItem>(FileItem(file))
        n.isExpandable = file.isDirectory
        if (hideMod) {
          if (!file.isHideFile()) {
            aNode.addChild(n)
          }
        } else {
          aNode.addChild(n)
        }

      }
    }
  }

  private fun openFile(fileItem: FileItem) {
    val myIntent = Intent(Intent.ACTION_VIEW)
    myIntent.data = Uri.fromFile(File(fileItem.absName))
    val intent = Intent.createChooser(myIntent, "Choose an application to open with:")
    startActivity(intent)

  }

  override fun onBackPressed() {
    val nowPress = System.currentTimeMillis()
    if (nowPress - lastPress > 1500) {
      toast(getString(R.string.hint_press_again))
      lastPress = nowPress
    } else {
      finish()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item!!.itemId) {
      R.id.hide_menu -> {
        hideMod = !hideMod
        toast("Hide Mode: $hideMod")
        runOnUiThread {
          refreshTree()
        }
      }
      R.id.about_menu -> {
        startActivity<AboutActivity>()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when(requestCode) {
      WRITE_EXTERNAL_STORAGE -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initView()
        } else {
          toast("Permission denied.")
          finish()
        }
      }
      else -> {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      }
    }

  }
  //DFS
  @Deprecated("Too slow...")
  private fun createTreeNode(aNode: DefaultTreeNode<FileItem>): DefaultTreeNode<FileItem> {
    val thisFile = File(aNode.element.absName)
    if (thisFile.listFiles() != null && thisFile.listFiles().isNotEmpty()) {
      val itemList = thisFile.listFiles()
      for (file in itemList) {
        Log.w(TAG, "file: " + file.name)
        val node = DefaultTreeNode<FileItem>(FileItem(file))
        root.addChild(node)
        if (file.isDirectory) {
          createTreeNode(node)
        } else {
          continue
        }
      }
    }
    return root
  }
}

