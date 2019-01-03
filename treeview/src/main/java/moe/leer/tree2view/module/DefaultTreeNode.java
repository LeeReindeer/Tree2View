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

package moe.leer.tree2view.module;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Author: lee
 * Time: 12/4/17.
 */
@SuppressWarnings({"WeakerAccess", "unchecked", "unused", "UnusedAssignment", "RedundantCast"})
public class DefaultTreeNode<E> implements TreeNode, Serializable, Cloneable{

  //max = 1 << 31
  private int id;
  private int lastId;

  //The content of this node
  private transient E element;
  private DefaultTreeNode<E> parent = null;

  //array of node's child
  private LinkedList<DefaultTreeNode> children;

  //just a mark, even node has children,
  //this value can still be false.
  boolean hasChildren = false;
  //same as hasChildren, but it's default value is true.
  //it let an expandable node can add children, after add the hasChildren will be true
  private boolean mExpandable = true;
  //whether this node is expanded, always false when (mExpandable == false)
  private boolean isExpanded = false;
  private boolean mSelectable = true;
  private boolean isSelected = false;

  //children size
  private int size;
  @SuppressWarnings("FieldCanBeLocal")
  private int depth;

  public DefaultTreeNode(E element) {
    this.element = element;
    this.hasChildren = false;
  }

  public DefaultTreeNode(E element, boolean hasChildren) {
    this.hasChildren = hasChildren;
    this.element = element;
  }

  public DefaultTreeNode(DefaultTreeNode<E> parent, E element) {
    this.parent = parent;
    this.element = element;

    this.hasChildren = false;
  }

  public DefaultTreeNode(DefaultTreeNode<E> parent, LinkedList<DefaultTreeNode> children, E element) {
    this.parent = parent;
    this.children = children;
    for (TreeNode n : children) {
      n.setParent(this);
    }
    this.element = element;
    this.hasChildren = true;
  }

  //generate id for it's direct children
  private int generateId() {
    return ++lastId;
  }

  /**
   * Add child to this node at last in list.
   * @param child the child
   */
  public void addChild(TreeNode child) {
    if (child == null) {
      throw new NullPointerException("Child can't be null");
    } else if (isAncestorOf((DefaultTreeNode) child)) {
      throw new IllegalArgumentException("child can't be the ancestor of this node");
    }
    if (children == null) {
      children = new LinkedList<>();
      this.hasChildren = mExpandable = true;
    }
    size++;
    child.setParent(this);
    ((DefaultTreeNode) child).depth = this.depth + 1;
    ((DefaultTreeNode) child).id = generateId();
    children.add((DefaultTreeNode) child);
  }

  /**
   * Insert child at the index
   * @param index index to insert(after insert the child node's index is <code>index</code>)
   * @param child the given child
   */
  public void addChild(int index, DefaultTreeNode child) {
    if (child == null) {
      throw new NullPointerException("Child can't be null");
    } else if (isAncestorOf((DefaultTreeNode) child)) {
      throw new IllegalArgumentException("child can't be the ancestor of this node");
    }
    if (children == null) {
      children = new LinkedList<>();
      this.hasChildren = mExpandable = true;
    }
    size ++;
    child.setParent(this);
    child.depth = this.depth + 1;
    child.id = generateId();
    children.add(index, (DefaultTreeNode) child);
  }

  /**
   * Add a number of children
   * @param children the children
   */
  public void addChildren(DefaultTreeNode...children) {
    for (TreeNode child : children) {
      addChild(child);
    }
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public void removeChild(TreeNode child) {
    if (child == null) {
      throw new IllegalArgumentException("given child is null");
    }
    if (!children.contains(child)) {
      throw new IllegalArgumentException("is not the child of this");
    }
    size--;
    children.remove(child);
  }

  @Override
  public void removeThis() {
    TreeNode parent = this.getParent();
    if (parent != null) {  //not the root node
      this.getParent().removeChild(this);
    } else {
      throw new IllegalStateException("can not remove root node");
    }
    //remove children
    this.children = null;
    //unlink from parent
    this.parent = null;
    this.size = 0;
    this.hasChildren = mExpandable = false;
  }

  public void removeAllChildren() {
    this.children = null;
    this.hasChildren = false;
    this.size = 0;
  }

  public void removeFromParent(TreeNode parent, TreeNode child) {
    if (parent == null || child == null) {
      throw new IllegalArgumentException("parent or child is null");
    }
    parent.removeChild(child);
  }

  /*getters & setters*/

  public E getElement() {
    return element;
  }

  public void setElement(E element) {
    this.element = element;
  }

  public TreeNode getParent() {
    return parent;
  }

  public void setParent(TreeNode parent) {
    this.parent = (DefaultTreeNode<E>) parent;
  }

  public TreeNode getFirstNode() {
    if (!hasChildren) {
      throw new NoSuchElementException("node has no children");
    }
    return children.getFirst();
  }

  public TreeNode getLastNode() {
    if (!hasChildren) {
      throw new NoSuchElementException("node has no children");
    }
    return children.getLast();
  }

  public int getSize() {
    return size;
  }


  /**
   *
   * @return the expanded nodes count
   */
  public int getVisibleSize() {
    int c = 0;
    for (DefaultTreeNode child : children) {
      if (child.isExpanded()) {
        ++c;
      }
    }
    return c;
  }

  /**
   * Return number of this node's depth.
   * Bug: After create a node your should immediately add it.
   * depth is auto increase at:
   * @see #addChild(TreeNode), so is this cheaper to get depth?
   * @return node's depth
   */
  public int getDepth() {
    //int d = 0;
    //TreeNode thisNode = this;
    //while ((thisNode = thisNode.getParent()) != null) {
    //  d++;
    //}
    //depth = d;
    if (this.parent == null) {  //the root node
     depth = 0;
    }
    return depth;
  }

  public LinkedList<DefaultTreeNode> getChildren() {
    return children;
  }

  public TreeNode getChildAt(int index) {
    if (children == null) {
      throw new NullPointerException("this node has no children");
    }
    return children.get(index);
  }

  /**
   * Set the children for this node, this will replace before children.
   * @param children the children to be added.
   */
  public void setChildren(LinkedList<DefaultTreeNode> children) {
    if (children != null) {
      this.hasChildren = true;
      for (TreeNode n : children) {
        n.setParent(this);
      }
      size+=children.size();
      this.children = children;
    }
  }

  public void setExpandable(boolean mExpandable) {
    this.mExpandable = mExpandable;
  }

  public boolean isExpandable() {
    return mExpandable;
  }

  public boolean isExpanded() {
    return isExpanded;
  }

  public void setExpanded(boolean expanded) {
    isExpanded = expanded;
  }

  public boolean isSelectable() {
    return mSelectable;
  }

  public void setSelectable(boolean mSelectable) {
    this.mSelectable = mSelectable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  /**
   * If a node has no children, the node is called a leaf
   * @return true if has no children
   */
  @Override
  public boolean isLeaf() {
    return !isHasChildren();
  }

  public int getLeafCount() {
    return getLeafCountOf(this);
  }

  protected int getLeafCountOf(DefaultTreeNode aNode) {
    int c = 0;
    LinkedList<DefaultTreeNode> list = aNode.getChildren();
    DefaultTreeNode node = null;
    if (list != null) {
      for (int i = 0; i < list.size(); i++) {
        node = list.get(i);
        if (!node.hasChildren) {
          c++;
        } else {  //node has children
          c += getLeafCountOf(node);
        }
      }
    }
    return c;
  }

  public boolean isHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(boolean hasChildren) {
    this.hasChildren = hasChildren;
  }

  public boolean isRoot() {
    return parent == null;
  }

  private void increaseId(DefaultTreeNode node) {
     node.id = this.id + 1;
  }

  /**
   * public method to Get path from root
   * @return string of element path
   */
  public String getPath() {
    DefaultTreeNode<E>[] nodes = (DefaultTreeNode<E>[]) getPathFromRoot(this, 0);
    StringBuilder sb = new StringBuilder();
    if (nodes == null) {
      return "Root/";
    }
    for (DefaultTreeNode<E> e : nodes) {
      sb.append(e.getElement().toString()).append("/");
    }
    return sb.toString();
  }

  /*getters & setters*/

  /**
   * //@see javax.swing.tree.DefaultMutableTreeNode#getPathToRoot
   * @param node  always this node
   * @param depth use to specialise array size
   * @return array of <code>DefaultTreeNode</code>
   */
  @Override
  @SuppressWarnings({"Duplicates", "unchecked", "ConstantConditions"})
  public TreeNode[] getPathFromRoot(TreeNode node, int depth) {

    DefaultTreeNode<E>[] treeNodes;
    // root's parent node
    if (node == null) {
      if (depth == 0) {
        return null;
      } else {
        return treeNodes = new DefaultTreeNode[depth];
      }
    } else {
      depth++;
      treeNodes = (DefaultTreeNode<E>[]) getPathFromRoot(node.getParent(), depth);
      //Add root in treeNodes[0], and...
      treeNodes[treeNodes.length - depth] = (DefaultTreeNode<E>) node;
    }
    return treeNodes;
  }

  /**
   * Check this node whether the descendant of given node.
   */
  public boolean isDescendantOf(DefaultTreeNode gNode) {
    if (gNode == null) {
      return false;
    }

    TreeNode a = this;
    do {
      if (a == gNode) {
        return true;
      }
    } while ((a = a.getParent()) != null);

    return false;
  }

  /**
   * Check this node whether the ancestor of given node
   */
  public boolean isAncestorOf(DefaultTreeNode gNode) {
    return gNode != null && gNode.isDescendantOf(this);
  }

  @Override
  public String toString() {
    if (element == null) {
      return "";
    }
    return element.toString();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      DefaultTreeNode node = (DefaultTreeNode) super.clone();
      node.hasChildren = this.hasChildren;
      node.element = this.element;
      node.mSelectable = this.mSelectable;
      node.isSelected = this.isSelected;
      node.isExpanded = this.isExpanded;
      node.children = (LinkedList) this.children.clone();
      return node;
    } catch (Exception ignored) {
    }
    return null;
  }

  // TODO: 12/14/17 implements Serializable
}
