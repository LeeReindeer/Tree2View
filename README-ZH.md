# Tree2View 

<!--[![Build Status](https://travis-ci.org/LeeReindeer/Tree2View.svg?branch=master)](https://travis-ci.org/LeeReindeer/Tree2View)-->

[![](https://jitpack.io/v/LeeReindeer/Tree2View.svg)](https://jitpack.io/#LeeReindeer/Tree2View)

> TreeView implementation in Android.

## 主要功能

|Tree2View|文件管理器(一个例子)|
|--------|----------|
|①多级分层的树结构视图 | 基本的文件管理器布局|
|②记忆展开状态 | 自动展开上次打开未关闭的目录|
|③使用适配器设计模式，用户可自定义 TreeAdapter | 对不同类型的文件显示不同的Icon |
|④动态增删节点 | 删除和添加文件后可自动刷新状态 |
|⑤选择模式 | 长按节点进行文件操作(Copy, Cut, Rename, Delete) |
|⑥动画支持，内置增删节点的动画 | 增删文件时带有动画 |

[一个更简单的例子](https://github.com/LeeReindeer/Tree2View-demo)

## 实现原理

- TreeView 继承自 ListView

- DFS遍历可展开的树节点，转化为List 与 [TreeAdapter](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/adapter/TreeAdapter.java) 进行适配。

- 分级的视觉效果通过 [SimpleTreeAdapter](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/adapter/SimpleTreeAdapter.java)（通过对不同深度的节点设置不同的**缩进**）来实现的。

- 使用 [DefaultTreeNode](https://github.com/LeeReindeer/Tree2View/blob/master/treeview/src/main/java/xyz/leezoom/view/treeview/module/DefaultTreeNode.java) 来（用链表保存子节点）增删节点。这样就实现了视觉效果和数据结构统一的设计。

## 效果预览 - Preview

![](https://github.com/LeeReindeer/Tree2View/blob/master/screenshot/tree2view_demo1.png)

![](https://github.com/LeeReindeer/Tree2View/blob/master/screenshot/tree2view_demo2.png)

## 下载

1. 在项目的 build.gradle 中添加：

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. 在 app 目录下的 build.gradle 添加：

```
	dependencies {
	        implementation 'com.github.LeeReindeer:Tree2View:v0.1.2'
	}
```

## 使用

1. 在 `xml` 中添加，和 `ListView` 类似的.

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
2. 在代码里添加初始化，这里使用Kotlin(Java 代码类似)

```kotlin
  var root :DefaultTreeNode? = DefaultTreeNode("Root")
  tree_view.root = root
  val child1 = DefaultTreeNode("Child1")
  val child2 = DefaultTreeNode("Child2")
  //在你创建节点之后，你需要立即添加她
  root.addChild(child1)
  root.addChild(child2)
  val child3 = DefaultTreeNode("Child3")
  child1.addChild(child3)
  //是否默认显示根节点的孩子
  tree_view.isRootVisible = true
  //默认的动画
  tree_view.isDefaultAnimation = true
```

3. 设置监听器

`Tree2View` 有默认的点击监听器，你也可以自己实现，可以参考[这里](https://github.com/LeeReindeer/Tree2View/blob/master/app/src/main/java/xyz/leezoom/tree2/activity/MainActivity.kt#L111)。

选择（长按）监听器需要自己实现。

4. 自定义使用

只需要实现 `TreeAapater` ,就那么简单。

```java
public class FileTreeAdapter extends TreeAdapter<FileItem> {
  //resourceId 是 itemView 的 xml 视图。
  public FileTreeAdapter(Context context, DefaultTreeNode root, int resourceId) {
    super(context, root, resourceId);
  }
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //your code here
     //...
     //调用 setPadding 来设置缩进
     setPadding(holder.arrowIcon, depth, -1);
     //根据数据改变 view 的状态（比如图标什么的）
     toggle(node, holder);
    }
    
    @Override
     public void toggle(Object... objects) {
      //需要自己实现
     }
}
```

之后只要设置一下适配器：

```kotlin
  val adapter = FileTreeAdapter(this@MainActivity, root, R.layout.layout_file_tree_item)
  treeView.treeAdapter = adapter
```

## License

Apache 2.0