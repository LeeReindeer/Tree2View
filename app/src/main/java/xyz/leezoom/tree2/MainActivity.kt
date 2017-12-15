package xyz.leezoom.tree2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.annotation.IntegerRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import xyz.leezoom.view.treeview.TreeUtils
import xyz.leezoom.view.treeview.TreeView
import xyz.leezoom.view.treeview.module.DefaultTreeNode
import java.io.File


class MainActivity : AppCompatActivity() {

  private val TAG = "MainActivity"
  private val WRITE_EXTERNAL_STORAGE = 1

  private val INNER_STORAGE = Environment.getExternalStorageDirectory().absolutePath

  private val fileOps = listOf("Copy","Cut", "Rename", "Delete")

  private var adapter: FileTreeAdapter? = null
  private var root: DefaultTreeNode<FileItem> = DefaultTreeNode(FileItem(File(INNER_STORAGE)))
  //<hash, count>
  private var clickCount = HashMap<Int, Int>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    } else {
      initData()
    }

    val list = TreeUtils.getVisibleNodesB(root)
    for (item in list)  Log.w(TAG, "bfs node " + (item.element).toString())
  }

  private fun initRoot() {
    //expand root
    root.isExpanded = true
    val rootDir = File(INNER_STORAGE)
    val list = rootDir.listFiles()
    //do not rm root node's children or add children
    clickCount.put(root.hashCode(), 1)
    for (item in list) {
      this.root.addChild(DefaultTreeNode<FileItem>(FileItem(item)))
    }
  }

  private fun initData() {
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
        adapter!!.notifyDataSetChanged()
      }
    }
    tree_view.setTreeItemSelectedListener { v, node, pos ->
      val list = TreeUtils.getVisibleNodesD(root)
      toast("You selected " + list[pos].element.toString())
      selector("File operations", fileOps, { dialogInterface, i ->
        when (i) {
          0 -> {
            Log.d(TAG, "Copy")
            //todo
          }
          1 -> {
            Log.d(TAG, "Cut")
          }
          2 -> {
            Log.d(TAG, "Rename")
          }
          3 -> {
            Log.d(TAG, "Delete")
            node.removeThis()
            list.removeAt(pos)
            tree_view.refresh(list)
          }
          else -> { }
        }
      })
      false
    }
  }

  private fun createNode(aNode: DefaultTreeNode<FileItem>) {
    val thisFile = File(aNode.element.absName)
    if (thisFile.listFiles() != null && thisFile.listFiles().isNotEmpty()) {
      val itemList = thisFile.listFiles()
      for (file in itemList) {
        val n = DefaultTreeNode<FileItem>(FileItem(file))
        n.isExpandable = file.isDirectory
        aNode.addChild(n)
      }
    }
  }

  private fun openFile(fileItem: FileItem) {
    val myIntent = Intent(Intent.ACTION_VIEW)
    myIntent.data = Uri.fromFile(File(fileItem.absName))
    val intent = Intent.createChooser(myIntent, "Choose an application to open with:")
    startActivity(intent)

  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when(requestCode) {
      WRITE_EXTERNAL_STORAGE -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initData()
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

  private fun Activity.requestPermission(permission: String, requestCOde: Int) {
    ActivityCompat.requestPermissions(this,  arrayOf(permission), requestCOde)
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
        root!!.addChild(node)
        if (file.isDirectory) {
          createTreeNode(node)
        } else {
          continue
        }
      }
    }
    return root!!
  }

  @Deprecated("For test...")
  private fun createTree2(): TreeView {

    var treeView: TreeView? = null
    val root = DefaultTreeNode("Root")
    //tree.root = root
    treeView = TreeView(this@MainActivity, root)
    val child1 = DefaultTreeNode("Child1")
    val child2 = DefaultTreeNode("Child2")
    root.addChild(child1)
    root.addChild(child2)
    val childA = DefaultTreeNode("Child-A")
    child1.addChild(childA)
    child1.addChild(0, DefaultTreeNode("Child-B"))
    val  childC = DefaultTreeNode("Child-C")
    child1.addChild(childC)
    Log.d(TAG, "Before rm: " + treeView!!.travelTree())
    root.removeFromParent(child1, childC)
    Log.d(TAG, "After rm: " + treeView!!.travelTree())
    Log.w(TAG, "Child-A's depth: " + childA.depth)
    Log.w(TAG, "Child1's depth: " + child1.depth)
    Log.w(TAG, "root's depth: " + root.depth)
    Log.w(TAG, "child-A is child1's child: " + childA.isDescendantOf(child1))
    Log.w(TAG, "child-A is child2's child: " + childA.isDescendantOf(child2))

    Log.w(TAG, "child1's leafs: : " + child1.leafCount)
    Log.w(TAG, "root's leafs: : " + root.leafCount)
    val list = TreeUtils.getVisibleNodesB(root)
    Log.d(TAG, "Visible node " + list.size)
    for (item in list)  Log.d(TAG, "Visible node " + item.element)
    //treeView!!.setOnItemClickListener { parent, view, position, id ->  }
    return treeView!!
  }
}
/*
val root = DefaultTreeNode<FileItem>(FileItem("Root"))
val child1 = DefaultTreeNode<FileItem>(FileItem("Child1"))
val child2 = DefaultTreeNode<FileItem>(FileItem("Child2"))
val child3 = DefaultTreeNode<FileItem>(FileItem("Child3"))
val child4 = DefaultTreeNode<FileItem>(FileItem("Child4"))
root.addChildren(child1, child2)
child1.addChild(child3)
child1.addChild(child4)
tree_view.root = root
adapter = FileTreeAdapter(this@MainActivity, root, R.layout.layout_file_tree_item)
tree_view.treeAdapter = adapter
tree_view.setTreeItemSelectedListener { v, node, pos ->
  val list = TreeUtils.getVisibleNodesD(root)
  toast("You selected " + list[pos].element.toString())
  selector("File operations", fileOps, { dialogInterface, i ->
    when (i) {
      0 -> Log.d(TAG, "Copy")
      1 -> Log.d(TAG, "Cut")
      2 -> Log.d(TAG, "Rename")
      3 -> {
        Log.d(TAG, "Delete")
        node.removeThis()
        list.removeAt(pos)
        tree_view.refresh(list)
      }
      else -> { }
    }
  })
  //Toast.makeText(v.context, "You selected " + TreeUtils.getVisibleNodesD(root)[pos].element.toString(), Toast.LENGTH_SHORT).show()
  false
}
Log.d(TAG, tree_view!!.travelTree())
for (item in TreeUtils.getVisibleNodesD(root)) {
  //debug {"dfs node: $item.element"  }
  //debug { "test" }
  Log.d(TAG,"dfs node: " + item.element.toString())
}
*/
