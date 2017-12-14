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

package xyz.leezoom.view.treeview.module;

public interface TreeNode {

  void addChild(TreeNode child);

  void removeChild(TreeNode child);

  /**
   * Remove this node and all it's children(if it has)
   */
  void removeThis();

  void setParent(TreeNode node);

  TreeNode getParent();

  boolean isLeaf();

  TreeNode[] getPathFromRoot(TreeNode node, int depth);

}
