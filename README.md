# Tree2View

> TreeView implementation in Android.
>
> 数据结构课程设计

## 主要功能 - Features

|TreeView|FileManage|
|--------|----------|
|①多级分层的树结构视图 | 基本的文件管理器布局|
|②记忆展开状态 | 自动展开上次打开未关闭的目录|
|③使用适配器设计模式，用户可自定义 TreeAdapter | 对不同类型的文件显示不同的Icon |
|④动态增删节点 | 删除和添加文件后可自动刷新状态(To-do) |
|⑤选择模式 | 长按节点进行文件操作(Copy, Cut, Rename, Delete) |


## TreeView 实现原理 - How it work

- TreeView 继承自 ListView

- DFS遍历可展开的树节点，转化为List 与 TreeAdapter 进行适配。

- 分级的视觉效果通过 SimpleTreeAdapter（通过对不同深度的节点设置不同的**缩进**）来实现的。

- 使用上直接使用 DefaultTreeNode （使用链表实现子节点之间的联系）增删节点。这样就实现了视觉效果和数据结构统一的设计。

## 效果预览 - Preview

![image](http://ojvnx00zs.bkt.clouddn.com/tree_view_product1.png)

## 下载 - Download

> 未上传到 `jCenter()`，可直接clone本项目使用。

```git
git clone git@github.com:LeeReindeer/Tree2View.git
```

Then open your project in Android Studio, then Click `FIle` -> `New` -> `Import Module`, to import this 
module.

And add dependence in your `build.gradle`
```groovy
 implementation project(path: ':treeview')
```

## 使用 - Usage

Feel free to use it as `ListView`.

XML:
```xml
    <xyz.leezoom.view.treeview.TreeView
        android:id="@+id/tree_view"
        android:layout_marginTop="16dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="#ffffff"
        android:dividerHeight="1px">

    </xyz.leezoom.view.treeview.TreeView>
```

Kotlin(Java is similar whit it)
```kotlin
  var root :DefaultTreeNode? = DefaultTreeNode("Root")
  val treeView = TreeView(this@MainActivity, root)
  val child1 = DefaultTreeNode("Child1")
  val child2 = DefaultTreeNode("Child2")
  root.addChild(child1)
  root.addChild(child2)
  val child3 = DefaultTreeNode("Child3")
  child1.addChild(child3)
```

If you want to use customized item view, you should implement `TreeAapater`, like this:
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
