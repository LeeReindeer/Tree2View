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

package moe.leer.filetree.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;

import xyz.leezoom.tree2.R;
import moe.leer.tree2view.TreeUtils;
import moe.leer.tree2view.adapter.TreeAdapter;
import moe.leer.tree2view.module.DefaultTreeNode;

public class FileTreeAdapter extends TreeAdapter<FileItem> {

  public FileTreeAdapter(Context context, DefaultTreeNode<FileItem> root, int resourceId) {
    super(context, root, resourceId);
  }

  @SuppressWarnings("RedundantCast")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (mNodesList == null) {
      mNodesList = TreeUtils.getVisibleNodesD(super.mRoot);
      notifyDataSetChanged();
    }
    DefaultTreeNode node = mNodesList.get(position);
    ViewHolder holder;

    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
      holder = new ViewHolder();
      holder.arrowIcon = (PrintView) convertView.findViewById(R.id.arrow_icon);
      holder.itemIcon = (PrintView) convertView.findViewById(R.id.item_icon);
      holder.fileText = (TextView) convertView.findViewById(R.id.ft_name);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    FileItem fileItem = (FileItem)(node.getElement());
    //get the file name(not the full name)
    holder.fileText.setText(fileItem.getName());
    if (fileItem.isDir()) {
      //set dir icon
      holder.itemIcon.setIconText(getStringResource(R.string.ic_folder));
    } else {
      //set file icon
      switch (fileItem.getType()) {
        case FileItem.APK:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_apk));
          break;
        case FileItem.ARCHIVE:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_archive));
          break;
        case FileItem.AUDIO:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_audio));
          break;
        case FileItem.CODE:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_code));
          break;
        case FileItem.VIDEO:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_video));
          break;
        case FileItem.PIC:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_image));
          break;
        case FileItem.DOC:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_document));
          break;
        default:
          holder.itemIcon.setIconText(getStringResource(R.string.ic_drive_file));
          break;
      }
    }
    int depth = node.getDepth();
    setPadding(holder.arrowIcon, depth, -1);
    toggle(node, holder);
    return convertView;
  }


  @Override
  public void toggle(Object... objects) {
    DefaultTreeNode node = null;
    ViewHolder holder = null;
    try {
      node = (DefaultTreeNode) objects[0];
      holder = (ViewHolder) objects[1];
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
    //only toggle for dir
    if (node.isExpandable()) {
      if (!node.isExpanded()) {
        //set right arrowIcon
        holder.arrowIcon.setIconText(getStringResource(R.string.ic_keyboard_arrow_right));
      } else if (node.isExpanded()) {
        //set down arrowIcon
        holder.arrowIcon.setIconText(getStringResource(R.string.ic_keyboard_arrow_down));
      }
    } else {
      //holder.arrowIcon.setVisibility(View.GONE);
      holder.arrowIcon.setIconText(getStringResource(R.string.ic_keyboard_arrow_right));
    }
  }

  private String getStringResource(int id) {
    if (mContext == null) {
      throw new IllegalStateException("Context is null");
    }
    return mContext.getResources().getString(id);
  }

  class ViewHolder {
    PrintView arrowIcon;
    PrintView itemIcon;
    TextView fileText;
  }
}
