# Tree2View 

<!--[![Build Status](https://travis-ci.org/LeeReindeer/Tree2View.svg?branch=master)](https://travis-ci.org/LeeReindeer/Tree2View)-->

[![](https://jitpack.io/v/LeeReindeer/Tree2View.svg)](https://jitpack.io/#LeeReindeer/Tree2View)

> TreeView implementation in Android.

[中文版](/README-ZH.md)

## Features

|TreeView|File Explorer(Advanced Example)|
|--------|----------|
|①Multi-level tree view | Basic file manager layout|
|②Remember expansion state | Automatically expand the last unclosed directory|
|③Customize TreeAdapter | Different types of documents show different Icon |
|④Dynamic add and delete  nodes | refresh status after delete and add files |
|⑤Select listener | Long press node for file operations (Copy, Cut, Rename, Delete) |
|⑥Animation support | Add or delete files with animation |

You can also see [a more simple example](https://github.com/LeeReindeer/Tree2View-demo).

## Implement

- TreeView extends from ListView.

- DFS travel the expandable tree node, and convert it to List which adapt with [TreeAdapter](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/adapter/TreeAdapter.java).

- Use [SimpleTreeAdapter](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/adapter/SimpleTreeAdapter.java) ot set different indentation on nodes of different depths.

- Use LinkedList to store node's children, see [DefaultTreeNode](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/module/DefaultTreeNode.java).

## Preview

![](https://github.com/LeeReindeer/Tree2View/blob/master/screenshot/tree2view_demo1.png)

![](https://github.com/LeeReindeer/Tree2View/blob/master/screenshot/tree2view_demo2.png)

## Download

1. Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add the dependency

```
	dependencies {
	        implementation 'com.github.LeeReindeer:Tree2View:v0.1.2'
	}
```

## Usage

1. Add in your xml:

Feel free to use it as `ListView`.

```xml
    <moe.leer.tree2view.TreeView
        android:id="@+id/tree_view"
        android:layout_marginTop="16dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="#ffffff"
        android:dividerHeight="1px">

    </moe.leer.tree2view.TreeView>
```

2. Add children in Kotlin code(Java is similar whit it)
```kotlin
  var root :DefaultTreeNode? = DefaultTreeNode("Root")
  tree_view.root = root
  val child1 = DefaultTreeNode("Child1")
  val child2 = DefaultTreeNode("Child2")
  // After create a node your should immediately add it.
  root.addChild(child1)
  root.addChild(child2)
  val child3 = DefaultTreeNode("Child3")
  child1.addChild(child3)
  //whether the root's children is expanded by default
  tree_view.isRootVisible = true
  //animation
  tree_view.isDefaultAnimation = true
```
3. Add click listener and select listener

Tree2View has a default click listener, but it's ok to add your own.You can refer to [here](https://github.com/LeeReindeer/Tree2View/blob/master/app/src/main/java/xyz/leezoom/tree2/activity/MainActivity.kt#L111).

4. If you want to use customized item view, you should implement `TreeAapater`, like this:
```java
public class FileTreeAdapter extends TreeAdapter<FileItem> {
  //resourceId is your customized view resourceId, please use RelativeLayout, and let view neighbour.
  public FileTreeAdapter(Context context, DefaultTreeNode root, int resourceId) {
    super(context, root, resourceId);
  }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //your code here
     //...
     //call padding for a better UI
     setPadding(holder.arrowIcon, depth, -1);
     //toggle your view's status here
     toggle(node, holder);
    }
    
    @Override
     public void toggle(Object... objects) {
     }
```

Then simply add:

```kotlin
  val adapter = FileTreeAdapter(this@MainActivity, root, R.layout.layout_file_tree_item)
  treeView.treeAdapter = adapter
```

## License

Apache 2.0
