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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.leezoom.view.treeview.R;
import xyz.leezoom.view.treeview.TreeUtils;
import xyz.leezoom.view.treeview.module.DefaultTreeNode;

public class SimpleTreeAdapter extends TreeAdapter<String> {

  public SimpleTreeAdapter(Context context, DefaultTreeNode<Object> root, int resourceId) {
    super(context, root, resourceId);
  }

  @SuppressWarnings({"RedundantCast", "StatementWithEmptyBody", "UnusedAssignment"})
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    //dfs travel when first time called
    if (mNodesList == null) {
      mNodesList = TreeUtils.getVisibleNodesD(super.mRoot);
    }
    DefaultTreeNode node = mNodesList.get(position);
    ViewHolder holder = null;

    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
      holder = new ViewHolder();
      holder.tv = (TextView) convertView.findViewById(R.id.default_tree_item);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.tv.setText(node.getElement().toString());
    int depth = node.getDepth();
    //set view indent
    //left, top, right, bottom
    holder.tv.setPadding(baseIndent * (depth + 1),
            holder.tv.getPaddingTop(),
            holder.tv.getPaddingRight(),
            holder.tv.getPaddingBottom());
    if (node.isHasChildren() && !node.isExpanded()) {
      //set your icon
    } else if (node.isHasChildren() && node.isExpanded()) {
      //set your icon
    }
    return convertView;
  }

  class ViewHolder {
    TextView tv;
  }
}
    /*
    convertView = new FrameLayout(mContext);
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100);
    convertView.setLayoutParams(layoutParams);
    holder = new ViewHolder();
    holder.tv = new TextView(mContext);
    holder.tv.setLayoutParams(layoutParams);

    convertView.setTag(holder);
    */