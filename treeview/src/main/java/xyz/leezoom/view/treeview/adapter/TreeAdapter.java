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

package xyz.leezoom.view.treeview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import xyz.leezoom.view.treeview.module.DefaultTreeNode;

/**
 * Extend this class to set your own <code>TreeModel</code>
 * T can be your data
 */
@SuppressWarnings("WeakerAccess")
public abstract class TreeAdapter<T> extends BaseAdapter {

  //root node
  protected DefaultTreeNode<T> mRoot;
  protected Context mContext;
  protected ArrayList<DefaultTreeNode> mNodesList;

  protected int mResourceId;

  protected int baseIndent = 50;

  public TreeAdapter(Context mContext) {
    this.mContext = mContext;
  }

  public TreeAdapter(Context context, DefaultTreeNode<T> root) {
    this.mRoot = root;
    this.mContext= context;
  }

  public TreeAdapter(Context context, DefaultTreeNode<T> root,  int resourceId) {
    this.mRoot = root;
    this.mContext = context;
    this.mResourceId = resourceId;
  }

  /**
   * @return return the direct children'number of root
   */
  @Override
  public int getCount() {
    if (mNodesList != null) {
      return mNodesList.size();
    } else {
      return mRoot.getVisibleSize() + 1;
    }
  }

  @Override
  public Object getItem(int position) {
    if (mNodesList != null) {
      return mNodesList.get(position);
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  protected void setPadding(View v, int depth, int indent) {
    if (v == null || depth < 0) {
      throw new IllegalArgumentException("illegal params");
    }
    if (indent < 0) {
      indent = baseIndent;
    }
    v.setPadding(indent * (depth + 1),
            v.getPaddingTop(),
            v.getPaddingRight(),
            v.getPaddingBottom());
  }

  /**
   * Toggle your object's status
   * @param objects objects to toggle
   */
  public abstract void toggle(Object ... objects);

  //public abstract View createView(TreeNode node, Object content);

  public ArrayList<DefaultTreeNode> getNodesList() {
    return mNodesList;
  }

  public void setNodesList(ArrayList<DefaultTreeNode> mNodesList) {
    this.mNodesList = mNodesList;
  }

  public DefaultTreeNode getRoot() {
    return mRoot;
  }

  public void setRoot(DefaultTreeNode<T> node) {
    this.mRoot = node;
  }

  public int getResourceId() {
    return mResourceId;
  }

  public void setResourceId(int mResourceId) {
    this.mResourceId = mResourceId;
  }
}