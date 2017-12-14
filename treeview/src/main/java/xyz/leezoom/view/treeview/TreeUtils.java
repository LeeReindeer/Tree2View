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

import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import xyz.leezoom.view.treeview.module.DefaultTreeNode;
import xyz.leezoom.view.treeview.module.TreeNode;

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
public class TreeUtils {

  /**
   * Recursive DFS
   * Return all the size of the aRoot(tree or sub tree)
   */
  @SuppressWarnings({"unchecked", "Duplicates"})
  public static Pair<Integer, Integer> travelNodes(DefaultTreeNode aRoot) {
    int size = 0;
    int depth = 0;
    //System.out.println("Size: " + first.getChildren().size());
    for (int j = 0; j < aRoot.getChildren().size(); j++) {
      DefaultTreeNode nextNode = (DefaultTreeNode) aRoot.getChildren().get(j);
      //count tree size
      size++;
      if (nextNode.isHasChildren()) {
        //count tree depth
        depth++;
        travelNodes(nextNode);
      }
    }
    return new Pair<>(size, depth);
  }

  /**
   * DFS use <code>Stack</code>
   */
  public static ArrayList<DefaultTreeNode> getVisibleNodesD(DefaultTreeNode aRoot) {
    ArrayList<DefaultTreeNode> list = new ArrayList<>();
    Stack<DefaultTreeNode> stack = new Stack<>();
    if (aRoot == null) {
      return null;
    }
    stack.push(aRoot);
    while (!stack.isEmpty()) {
      DefaultTreeNode node = stack.pop();
      list.add(node);
      LinkedList<DefaultTreeNode> children = node.getChildren();
      if (children == null) {
        continue;
      }
      //add children in reversed order
      for (int i = children.size() - 1; i >= 0; i--) {
        if (node.isExpanded()) {
          stack.push(children.get(i));
        }
      }
    }
    return list;
  }

  /**
   * DFS, same as
   * @see TreeUtils#getVisibleNodesD(DefaultTreeNode)
   * @see DefaultTreeNode#getPathFromRoot
   * Get all children nodes(contains the given node).
   */
  public static ArrayList<DefaultTreeNode> getAllNodesD(DefaultTreeNode aRoot) {
    ArrayList<DefaultTreeNode> list = new ArrayList<>();
    Stack<DefaultTreeNode> stack = new Stack<>();
    if (aRoot == null) {
      return null;
    }
    stack.push(aRoot);
    while (!stack.isEmpty()) {
      DefaultTreeNode node = stack.pop();
      list.add(node);
      LinkedList<DefaultTreeNode> children = node.getChildren();
      if (children == null) {
        continue;
      }
      //add children in reversed order
      for (int i = children.size() - 1; i >= 0; i--) {
        stack.push(children.get(i));
      }
    }
    return list;
  }

  /**
   * BFS use <code>Queue</code>
   * @return ArrayList of <code>DefaultTreeNode</code>
   */
  public static ArrayList<DefaultTreeNode> getVisibleNodesB(DefaultTreeNode aRoot) {
    Queue<DefaultTreeNode<Object>> q = new LinkedList<>();
    if (aRoot == null) {
      return null;
    }
    ArrayList<DefaultTreeNode> list = new ArrayList<>();
    q.add(aRoot);
    while (!q.isEmpty()) {
      DefaultTreeNode<Object> node = q.remove();
      list.add(node);
      LinkedList<DefaultTreeNode> children = node.getChildren();
      if (children == null) {
        continue;
      }
      //add expanded node's children
      if (node.isExpanded()) {
        for (DefaultTreeNode n : children) {
          q.add(n);
        }
      }
    }
    return list;
  }

  /**
   * BFS
   * Get all children nodes(contains the given node).
   */
  public static ArrayList<DefaultTreeNode> getAllNodesB(DefaultTreeNode aRoot) {
    Queue<DefaultTreeNode<Object>> q = new LinkedList<>();
    if (aRoot == null) {
      return null;
    }
    ArrayList<DefaultTreeNode> list = new ArrayList<>();
    q.add(aRoot);
    while (!q.isEmpty()) {
      DefaultTreeNode<Object> node = q.remove();
      list.add(node);
      LinkedList<DefaultTreeNode> children = node.getChildren();
      if (children == null) {
        continue;
      }
      for (DefaultTreeNode n : children) {
        q.add(n);
      }
    }
    return list;
  }

  public static TreeNode getNodeAtIndex(DefaultTreeNode node, int index) {
    if (node == null || index < 0) {
      return null;
    }
    //use dfs index
    return getAllNodesD(node).get(index);
  }

}
