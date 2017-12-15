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

package xyz.leezoom.view.treeview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xyz.leezoom.view.treeview.adapter.SimpleTreeAdapter;
import xyz.leezoom.view.treeview.adapter.TreeAdapter;
import xyz.leezoom.view.treeview.module.DefaultTreeNode;
import xyz.leezoom.view.treeview.module.TreeNode;

@SuppressWarnings("unused")
public class TreeView extends ListView {

  private static final String TAG = "TreeView";

  private Context mContext;

  //same ad TreeModel in JTree
  private TreeAdapter adapter;
  //track node's expand status
  private HashMap<DefaultTreeNode<View>, Boolean> expandStatusMap = new HashMap<>();

  private onChildSelectedListener selectedListener;

  //The number of DefaultTreeNode in tree, root is already size in.
  protected int size = 1;

  //Tree depth
  protected int depth = 1;

  //The root node
  protected DefaultTreeNode root;
  // TODO: 12/14/17
  protected boolean isRootVisible = true;

  protected boolean isTraveled = false;

  private StringBuilder sb = new StringBuilder();

  // TODO: 12/14/17 use HashMap?
  private ArrayList<DefaultTreeNode> searchList = new ArrayList<>();
  //record the node's expanded status
  private HashMap<DefaultTreeNode, Boolean> expandedState;

  public TreeView(Context context) {
    super(context);
    init(context, null);
  }

  public TreeView(Context context, DefaultTreeNode root) {
    super(context);
    init(context, root);
  }

  public TreeView(Context context, TreeAdapter adapter) {
    super(context);
    this.mContext = context;
    this.adapter = adapter;
  }

  public TreeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, null);
  }

  public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, null);
  }

  private void init(Context context, DefaultTreeNode root) {
    this.mContext = context;
    if (root == null) {
      return;
    }
    if (this.root == null || this.root != root) {
      this.root = root;
      //expanded root node to show it's children
      this.root.setExpanded(true);
      adapter = new SimpleTreeAdapter(mContext, root, R.layout.layout_tree_item);
      this.setAdapter(adapter);
      adapter.notifyDataSetChanged();
    }
    this.setOnItemClickListener(new OnTreeItemClickListener());
  }

  public String travelTree() {
    return travelTree(root);
  }

  public String travelTree(DefaultTreeNode parent) {
    //reset
    depth = 1;
    size = 1;
    //reset sb
    sb = new StringBuilder();
    DefaultTreeNode node;
    if (parent == null) {
      node = root;
    } else {
      node = parent;
    }

    return root.getElement() +  "->" + travelNodeList(node);
  }

  /**
   * DFS travel tree.
   * @param first the parent node
   * @return string of nodes list
   */
  @SuppressWarnings({"unchecked", "Duplicates"})
  public String travelNodeList(DefaultTreeNode first) {
    //System.out.println("Size: " + first.getChildren().size());
    sb.append("{ ");
    for (int j = 0; j < first.getChildren().size(); j++) {
      DefaultTreeNode nextNode = (DefaultTreeNode) first.getChildren().get(j);
      sb.append(nextNode.getElement().toString());
      if (!nextNode.isHasChildren()) {
        sb.append(", ");
      }
      //count tree size
      size++;
      if (nextNode.isHasChildren()) {
        //count tree depth
        depth++;
        sb.append("->");
        travelNodeList(nextNode);
      }
    }
    sb.append("}, ");
    return sb.toString();
  }

  /**
   * BFS travel tree.
   * Get all TreeNodes in tree
   * @return array of <code>TreeNode</code>
   */
  public TreeNode[] getFullPath() {
    // TODO: 12/14/17 bfs
    return (TreeNode[]) TreeUtils.getAllNodesB(root).toArray();
  }

  public ArrayList<DefaultTreeNode> search(Object e) {
    searchList.clear();
    return searchElement(e, root);
  }

  @SuppressWarnings({"Duplicates", "unchecked"})
  private ArrayList<DefaultTreeNode> searchElement(Object e, DefaultTreeNode first) {
    for (int j = 0; j < first.getChildren().size(); j++) {
      DefaultTreeNode nextNode = (DefaultTreeNode) first.getChildren().get(j);
      //sb.append(nextNode.getElement().toString());
      if (nextNode.getElement() == e) {
        searchList.add(nextNode);
      }
      if (nextNode.isHasChildren()) {
        //search next node
        searchElement(e, nextNode);
      }
    }
    return searchList;
  }

  /*getters and setters.start*/
  public int getSize() {
    size = TreeUtils.travelNodes(root).first;
    return size;
  }

  public int getDepth() {
    //size depth
    depth = TreeUtils.travelNodes(root).second;
    return depth;
  }

  public void setRoot(DefaultTreeNode root) {
    init(mContext, root);
  }

  public DefaultTreeNode getRoot() {
    return root;
  }
  /*getters and setters.end*/

  public boolean isRootVisible() {
    return isRootVisible;
  }

  public void setRootVisible(boolean rootVisible) {
    isRootVisible = rootVisible;
  }

  public void setTreeAdapter(TreeAdapter adapter) {
    this.adapter = adapter;
    //call super
    this.setAdapter(adapter);
    this.root = adapter.getRoot();
    //expanded root node to show it's children
    this.root.setExpanded(true);
  }

  public TreeAdapter getTreeAdapter() {
    return adapter;
  }

  public void refresh(ArrayList<DefaultTreeNode> nodes) {
    if (adapter == null) {
      throw new NullPointerException("adapter is null");
    }
    if (nodes != null) {
      adapter.setNodesList(nodes);
    }
    adapter.notifyDataSetChanged();
  }

  @Override
  public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    final ArrayList<DefaultTreeNode> nodes = TreeUtils.getAllNodesD(root);
    //if (nodes == null || nodes.isEmpty()) {
    //  return;
    //}
    super.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return selectedListener != null && selectedListener.onTreeItemSelected(view, nodes.get(position), position);
      }
    });
  }

  public onChildSelectedListener getSelectedListener() {
    return selectedListener;
  }

  public void setTreeItemSelectedListener(onChildSelectedListener selectedListener) {
    this.selectedListener = selectedListener;
    setOnItemLongClickListener(null);
  }

  @Override
  public String toString() {
    return travelTree(root);
  }

  public interface onChildSelectedListener {
    boolean onTreeItemSelected(View v, DefaultTreeNode node, int pos);
  }

  /**
   * Implement ItemClickListener, and can't be extended.
   */
  class OnTreeItemClickListener implements AdapterView.OnItemClickListener {

    DefaultTreeNode root;
    TreeAdapter mAdapter;

    OnTreeItemClickListener() {
      this.mAdapter = adapter;
      this.root = TreeView.this.root;
    }

    /**
     * Lazy load.Click to toggle view, will call this:
     * @see SimpleTreeAdapter#getView(int, View, ViewGroup)
     * to refresh view
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      ArrayList<DefaultTreeNode> nodes = adapter.getNodesList();
      //the click item
      DefaultTreeNode node = nodes.get(position);
      Log.d(TAG, "onItemClick: " + node.getElement().toString());
      if (!node.isHasChildren()) {
        Log.w(TAG, "onItemClick: not Expandable");
        return;
      } else {
        //toggle
        if (node.isExpanded()) {
          node.setExpanded(false);
          Log.d(TAG, "onItemClick: close");
        } else {
          node.setExpanded(true);
          adapter.setRoot(root);
          Log.d(TAG, "onItemClick: open");
        }
      }
      adapter.setNodesList(TreeUtils.getVisibleNodesD(root));
      adapter.notifyDataSetChanged();
    }

  }

}
