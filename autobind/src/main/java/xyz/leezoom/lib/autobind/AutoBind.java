package xyz.leezoom.lib.autobind;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xyz.leezoom.lib.autobind.annotation.BindView;
import xyz.leezoom.lib.autobind.annotation.OnClick;
import xyz.leezoom.lib.autobind.annotation.OnLongClick;

@SuppressWarnings("ALL")
public class AutoBind {

  private static final String TAG = "AutoBind";

  private static final int MASK = 0x01;
  private static final int VIEW = 0X01;
  private static final int CLICK = 0x02;
  private static final int VIEW_AND_CLICK = 0x03;

  public static void bind(Object obj, View view) {
    bindView(obj, view);
    bindClick(obj, view);
  }

  //bind in activity
  public static void bind(Object obj) {
    bind(obj, null);
  }

  private static void bindViewByAnnotation(Object obj, View view) {
    Class clazz = obj.getClass();
    Field fields[] = clazz.getFields();
    for (Field field : fields) {
      //get runtime annotation
      BindView bindView = field.getAnnotation(BindView.class);
      if (bindView != null && bindView.value() != -1) {
        int id = bindView.value();
        field.setAccessible(true);
        try {
          if (view == null) {
            field.set(obj, ((Activity) obj).findViewById(id));
          } else {
            field.set(obj, view.findViewById(id));
          }
        } catch (IllegalAccessException e) {
          Log.e(TAG, "bind failed, " + e.getLocalizedMessage());
          e.printStackTrace();
        }
      }
    }
  }

  private static void bindView(Object obj, View view) {
    if (obj == null) {
      throw new IllegalArgumentException("Context is null");
    }else if (view == null) {
      if (obj instanceof Activity) {  // in activity
        bindViewByAnnotation(obj, null);
      } else {                       //other situation(like fragment), view must be not null.
        throw new IllegalArgumentException("View can not be null in this situation");
      }
    } else {                        //when view isn't null
      bindViewByAnnotation(obj, view);
    }
  }

  private static void bindClickByAnnotation(final Object obj, final View view) {
    Class clazz = obj.getClass();
    Method methods[] = clazz.getMethods();
    for (final Method m : methods) {
      OnClick aOnClick = m.getAnnotation(OnClick.class);
      OnLongClick aOnLongClick = m.getAnnotation(OnLongClick.class);
      if (aOnClick != null && aOnClick.value() != -1) {
        View clickedView = null;
        int id = aOnClick.value();
        if (view == null) {
          clickedView = ((Activity) obj).findViewById(id);
        } else {
          clickedView = view.findViewById(id);
        }
        if (clickedView == null) {
          throw new IllegalStateException("can't find view" + id);
        }
        clickedView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            try {
              m.invoke(obj, v);
            } catch (IllegalAccessException e) {
              Log.e(TAG, "bind failed, " + e.getLocalizedMessage());
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              Log.e(TAG, "bind failed, " + e.getLocalizedMessage());
              e.printStackTrace();
            }
          }
        });
      }
      if (aOnLongClick != null && aOnLongClick.value() != -1) {
        View longClickedView = null;
        int id = aOnLongClick.value();
        if (view == null) {
          longClickedView = ((Activity) obj).findViewById(id);
        } else {
          longClickedView = view.findViewById(id);
        }
        if (longClickedView == null) {
          throw new IllegalStateException("can't find view" + id);
        }
        longClickedView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            try {
              m.invoke(obj, v);
            } catch (IllegalAccessException e) {
              Log.e(TAG, "bind failed, " + e.getLocalizedMessage());
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              Log.e(TAG, "bind failed, " + e.getLocalizedMessage());
              e.printStackTrace();
            }
            return true;
          }
        });
      }
    }
  }

  private static void bindClick(Object obj, View view) {
    if (obj == null) {
      throw new IllegalArgumentException("Context is null");
    }else if (view == null) {
      if (obj instanceof Activity) {  // in activity
        bindClickByAnnotation(obj, null);
      } else {                       //other situation(like fragment), view must be not null.
        throw new IllegalArgumentException("View can not be null in this situation");
      }
    } else {                        //when view isn't null
      bindClickByAnnotation(obj, view);
    }
  }

  @Deprecated
  private static int checkAnnotation(Object obj, View view) {
    Class clazz = obj.getClass();
    Field fields[] = clazz.getFields();
    Method methods[] = clazz.getMethods();
    int n = MASK;
    for (Field f : fields) {
      if (f.getAnnotation(BindView.class) != null) {
        // FIXME: 12/13/17 if bind view > 1, will be a fault.
        n <<= VIEW;
        //bindView(obj, view);
      }
    }
    for (Method m : methods) {
      if (m.getAnnotation(OnClick.class) != null
              || m.getAnnotation(OnLongClick.class) != null) {
        n <<= CLICK;
      }
    }
    if (n == MASK << VIEW_AND_CLICK) {
      return VIEW_AND_CLICK;
    } else if (n == MASK << VIEW) {
      return VIEW;
    } else {
      return CLICK;
    }
  }
}
