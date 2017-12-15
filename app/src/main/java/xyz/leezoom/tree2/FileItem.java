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

package xyz.leezoom.tree2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileItem {

  private boolean isDir;
  private String name;
  private String absName; //full name = abstract path
  private long size;
  private String type;

  private static Map<String, String> typeMap = new HashMap<>();

  //init map
  //key -> suffix
  //value -> type
  static {
    String[] video = {"3gp", "asf", "avi", "mp4", "mpe", "mpeg", "mpg", "mpg4", "m4u", "m4v", "mov", "rmvb"};
    for (String s : video) {
      typeMap.put(s, "video");
    }

    String[] text = {"txt", "xml", "conf", "prop", "cpp", "h", "java", "class", "log", "json", "js",
            "php", "css", "py", "c", "c++", "cfg", "ini", "bat", "mf", "mtd", "lua", "html", "htm"};
    for (String s : text) {
      typeMap.put(s, "text");
    }

    String[] audio = {"m3u", "m4a", "m4b", "m4p", "mp2", "mp3", "mpga", "ogg", "wav", "wma", "wmv", "3gpp", "flac", "amr"};
    for (String s : audio) {
      typeMap.put(s, "audio");
    }

    String[] archive = {"zip", "rar", "7z", "tar", "jar", "gz", "xz"};
    for (String s : archive) {
      typeMap.put(s, "archive");
    }
  }

  public FileItem(File file) {
    this.isDir = file.isDirectory();
    this.absName = file.getAbsolutePath();
    this.size = getSize();
    this.type = getType(file.getName());
  }

  @Deprecated
  public FileItem(String absName) {
    this.absName = absName;
  }

  public boolean isDir() {
    return isDir;
  }

  public void setDir(boolean dir) {
    isDir = dir;
  }

  public String getAbsName() {
    return absName;
  }

  public void setAbsName(String absName) {
    this.absName = absName;
  }

  public String getName() {
    int lastNameIndex = absName.lastIndexOf("/");
    return name = absName.substring(lastNameIndex + 1);
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  private String getType(String fileName) {
    int dotIndex = fileName.lastIndexOf(".");
    if (dotIndex == -1 || dotIndex == fileName.length()) {
      return "";
    }
    String fileSuffix = fileName.substring(dotIndex + 1).toLowerCase();
    return typeMap.containsKey(fileSuffix) ? typeMap.get(fileSuffix) : fileSuffix;
  }
}
