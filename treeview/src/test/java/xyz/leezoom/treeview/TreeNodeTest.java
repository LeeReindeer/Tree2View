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

package xyz.leezoom.treeview;

import org.junit.Before;
import org.junit.Test;

import moe.leer.tree2view.module.DefaultTreeNode;

import static org.junit.Assert.assertEquals;

public class TreeNodeTest {

  private DefaultTreeNode<String> root = new DefaultTreeNode<>("Root");
  private DefaultTreeNode<String> child1 = new DefaultTreeNode<>("child1");
  private DefaultTreeNode<String> child2 = new DefaultTreeNode<>("child2");
  private DefaultTreeNode<String> child3 = new DefaultTreeNode<>("child3");


  @Before
  public void init() {
    root.addChildren(child1, child2);
    child1.addChild(child3);
  }

  @Test
  public void shouldAutoIncreaseDepth() {
    int depth = 0;
    depth++;
    assertEquals(depth, child1.getDepth());
    depth++;
    assertEquals(depth, child3.getDepth());
  }

  @Test
  public void rootShouldNotHasParent() {
    assertEquals(null, root.getParent());
  }

  @Test
  public void ancestorAndDescendantTest() {
    //child3.addChild(root);
    boolean isAncestor = child1.isAncestorOf(child3);
    boolean isDescendant = child2.isDescendantOf(child1);
    assertEquals(true, isAncestor);
    assertEquals(false, isDescendant);
    isAncestor = child3.isAncestorOf(child1);
    assertEquals(false, isAncestor);
    isDescendant = child3.isDescendantOf(child1);
    assertEquals(true, isDescendant);
  }

  @Test
  public void shouldRemoveItself() {
    child1.removeThis();
    int size = root.getSize();
    assertEquals(1, size);
    assertEquals(false, root.isAncestorOf(child1));
  }

}
