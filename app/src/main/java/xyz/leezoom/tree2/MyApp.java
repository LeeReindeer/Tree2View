package xyz.leezoom.tree2;

import android.app.Application;

import com.github.johnkil.print.PrintConfig;

public class MyApp extends Application{

  @Override
  public void onCreate() {
    super.onCreate();
    PrintConfig.initDefault(getAssets(), "fonts/material-icon-font.ttf");
  }

}
