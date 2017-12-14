package xyz.leezoom.tree2

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import xyz.leezoom.view.treeview.TreeUtils
import xyz.leezoom.view.treeview.TreeView
import xyz.leezoom.view.treeview.module.DefaultTreeNode
import java.io.File

class MainActivity : AppCompatActivity() {

  private val TAG = "MainActivity"

  private val INNER_STORAGE = Environment.getExternalStorageDirectory().absolutePath

  private val fileOps = listOf("Copy","Cut", "Rename", "Delete")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    //container.addView(createTree(), LinearLayout.LayoutParams(400, 400))
    val root = DefaultTreeNode("Root")
    val child1 = DefaultTreeNode("child1")
    val child2 = DefaultTreeNode("child2")
    val child3 = DefaultTreeNode("child3")
    val child4 = DefaultTreeNode("child4")
    root.addChildren(child1, child2)
    child1.addChild(child3)
    child1.addChild(child4)
    tree_view.root = root
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

    val list = TreeUtils.getVisibleNodesB(root)
    for (item in list)  Log.w(TAG, "bfs node " + item.element.toString())

    //val intent = Intent(this, SecondActivity::class.java)
    //main_text_view.setOnClickListener { startActivity(intent) }
  }

  /**
   * Add children(files and dirs) to parent
   */
  private fun createNode(parent: DefaultTreeNode<FileItem>) {

  }

  //dfs
  private fun findAllFileAndDir(): DefaultTreeNode<FileItem> {
    val rootDir = File(INNER_STORAGE)
    var root = DefaultTreeNode<FileItem>(FileItem(rootDir))
    while (rootDir.listFiles() != null && rootDir.listFiles().isNotEmpty()) {
      val itemList = rootDir.listFiles()
      createNode(root)
    }
    return root;
  }



  @Deprecated("")
  private fun createTree(): TreeView {

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
