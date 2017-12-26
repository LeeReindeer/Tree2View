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
import android.util.AndroidException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.leezoom.view.treeview.TreeView;
import xyz.leezoom.view.treeview.adapter.SimpleTreeAdapter;

import static org.junit.Assert.assertEquals;

//@RunWith(AndroidJunit4.class)
public class TreeViewTest {

  private Context instrumentationCtx;

  @Before
  public void setup() {
    //instrumentationCtx = InstrumentationRegistry.getContext();
  }

  @Test
  public void adapterShouldNonNull() {
    //SimpleTreeAdapter adapter = new SimpleTreeAdapter()
  }
}
