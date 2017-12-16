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

package xyz.leezoom.tree2.module;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Author: lee
 * Time: 12/16/17.
 */
public class FileUtils {

  public static String getNameFromAbsolutePath(String abPath) {
    int index = abPath.lastIndexOf("/");
    return abPath.substring(index + 1, abPath.length());
  }

  public static String getPrefixPath(String abPath) {
    int index = abPath.lastIndexOf("/");
    //append "/" at last
    return abPath.substring(0, index + 1);
  }

  public static String getFileType(String fileName) {
    int index = fileName.lastIndexOf(".");
    return fileName.substring(index + 1, fileName.length());
  }

  public static boolean doRename(String abPath, String reName) {
    return new File(abPath).renameTo(new File(getPrefixPath(abPath) + reName));
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static void doMove(String source, String dest) throws IOException {
    //copy then delete
    copy(source, dest);
    doDelete(source);
  }

  public static boolean doDelete(String abPath) {
    return new File(abPath).delete();
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static void copy(File origin, File dest) throws IOException {
    Files.copy(origin.toPath(), dest.toPath());
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static void copy(String fromPath, String toPath) throws IOException {
    File from = new File(fromPath);
    File to = new File(toPath);
    Files.copy(from.toPath(), to.toPath());
  }

  public static void main(String[] args) {
    String ab = "Home/Lee/Pic/lee.jpg";
    String name = getNameFromAbsolutePath(ab);
    System.out.println(getFileType(name));
    System.out.println(getNameFromAbsolutePath(ab));
    System.out.println(getPrefixPath(ab));
  }

}
