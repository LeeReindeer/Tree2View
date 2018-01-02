package xyz.leezoom.tree2view_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import xyz.leezoom.view.treeview.TreeView;
import xyz.leezoom.view.treeview.module.DefaultTreeNode;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private TreeView treeView;
  private DefaultTreeNode<String> root;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initData();
    initView();
  }

  private void initData() {
    root = new DefaultTreeNode<String>("Root");
    root.addChild(new DefaultTreeNode<String>("Child1"));
    Log.d(TAG, "root's depth: " + root.getDepth());

    DefaultTreeNode<String> child2 = new DefaultTreeNode<String>("Child2");
    //Important: after create a node your should immediately add it.
    root.addChild(child2);

    DefaultTreeNode<String> childA = new DefaultTreeNode<String>("ChildA");
    child2.addChild(childA);
    child2.addChild(new DefaultTreeNode<String>("ChildB"));
    child2.addChild(new DefaultTreeNode<String>("ChildC"));


    Log.d(TAG, "childA's depth: " + childA.getDepth());
    Log.d(TAG, "child2's depth: " + child2.getDepth());
    root.addChild(new DefaultTreeNode<String>("Child3"));
    root.addChild(new DefaultTreeNode<String>("Child4"));
  }

  private void initView() {
    treeView = (TreeView) findViewById(R.id.tree_view);
    treeView.setRoot(root);
    treeView.setDefaultAnimation(true);
  }

}
