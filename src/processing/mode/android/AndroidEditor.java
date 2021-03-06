/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 Part of the Processing project - http://processing.org

 Copyright (c) 2009-11 Ben Fry and Casey Reas

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package processing.mode.android;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import processing.app.*;
import processing.mode.java.JavaEditor;

import processing.core.PApplet;


public class AndroidEditor extends JavaEditor {
  private AndroidMode androidMode;


  protected AndroidEditor(Base base, String path, EditorState state, Mode mode) throws Exception {
    super(base, path, state, mode);
    androidMode = (AndroidMode) mode;
    androidMode.checkSDK(this);
  }


  public EditorToolbar createToolbar() {
    return new AndroidToolbar(this, base);
  }


  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


  public JMenu buildFileMenu() {
    String exportPkgTitle = AndroidToolbar.getTitle(AndroidToolbar.EXPORT, false);
    JMenuItem exportPackage = Toolkit.newJMenuItem(exportPkgTitle, 'E');
    exportPackage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleExportPackage();
      }
    });

    String exportProjectTitle = AndroidToolbar.getTitle(AndroidToolbar.EXPORT, true);
    JMenuItem exportProject = Toolkit.newJMenuItemShift(exportProjectTitle, 'E');
    exportProject.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handleExportProject();
      }
    });

    return buildFileMenu(new JMenuItem[] { exportPackage, exportProject});
  }


  public JMenu buildSketchMenu() {
    JMenuItem runItem = Toolkit.newJMenuItem(AndroidToolbar.getTitle(AndroidToolbar.RUN, false), 'R');
    runItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleRunDevice();
        }
      });

    JMenuItem presentItem = Toolkit.newJMenuItemShift(AndroidToolbar.getTitle(AndroidToolbar.RUN, true), 'R');
    presentItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleRunEmulator();
        }
      });

    JMenuItem stopItem = new JMenuItem(AndroidToolbar.getTitle(AndroidToolbar.STOP, false));
    stopItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleStop();
        }
      });
    return buildSketchMenu(new JMenuItem[] { runItem, presentItem, stopItem });
  }


  public JMenu buildModeMenu() {
    JMenu menu = new JMenu("Android");
    JMenuItem item;

    item = new JMenuItem("Sketch Permissions");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Permissions(sketch);
      }
    });
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem("Signing Key Setup");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Keys(AndroidEditor.this);
      }
    });
    item.setEnabled(false);
    menu.add(item);

    item = new JMenuItem("Android SDK Manager");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        File file = androidMode.getSDK().getAndroidTool();
        try {
          Runtime.getRuntime().exec(new String[] { file.getAbsolutePath(), "sdk" });
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    menu.add(item);

    item = new JMenuItem("Android AVD Manager");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        File file = androidMode.getSDK().getAndroidTool();
        PApplet.exec(new String[] { file.getAbsolutePath(), "avd" });
      }
    });
    menu.add(item);

    item = new JMenuItem("Reset Connections");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
//        editor.statusNotice("Resetting the Android Debug Bridge server.");
        Devices.killAdbServer();
      }
    });
    menu.add(item);

    return menu;
  }


  /**
   * Uses the main help menu, and adds a few extra options. If/when there's
   * Android-specific documentation, we'll switch to that.
   */
  public JMenu buildHelpMenu() {
    JMenu menu = super.buildHelpMenu();
    JMenuItem item;

    menu.addSeparator();

    item = new JMenuItem("Processing for Android Wiki");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Base.openURL("http://wiki.processing.org/w/Android");
      }
    });
    menu.add(item);


    item = new JMenuItem("Android Developers Site");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Base.openURL("http://developer.android.com/index.html");
      }
    });
    menu.add(item);

    return menu;
  }


  /** override the standard grab reference to just show the java reference */
  public void showReference(String filename) {
    File javaReferenceFolder = Base.getContentFile("modes/java/reference");
    File file = new File(javaReferenceFolder, filename);
    Base.openURL("file://" + file.getAbsolutePath());
  }



//  protected void updateMode() {
//    // When the selection is made, the menu will update itself
//    boolean active = toggleItem.isSelected();
//    if (active) {
//      boolean rolling = true;
//      if (sdk == null) {
//        rolling = loadAndroid();
//      }
//      if (rolling) {
//        editor.setHandlers(new RunHandler(), new PresentHandler(),
//                           new StopHandler(),
//                           new ExportHandler(),  new ExportAppHandler());
//        build = new AndroidBuild(editor, sdk);
//        editor.statusNotice("Android mode enabled for this editor window.");
//      }
//    } else {
//      editor.resetHandlers();
//      editor.statusNotice("Android mode disabled.");
//    }
//  }


//  protected boolean loadAndroid() {
//    statusNotice("Loading Android tools.");
//
//    try {
//      sdk = AndroidSDK.find(this);
//    } catch (final Exception e) {
//      Base.showWarning("Android Tools Error", e.getMessage(), null);
//      statusNotice("Android mode canceled.");
//      return false;
//    }
//
//    // Make sure that the processing.android.core.* classes are available
//    if (!checkCore()) {
//      statusNotice("Android mode canceled.");
//      return false;
//    }
//
//    statusNotice("Done loading Android tools.");
//    return true;
//  }


//  static protected File getCoreZipLocation() {
//    if (coreZipLocation == null) {
//      coreZipLocation = checkCoreZipLocation();
//    }
//    return coreZipLocation;
//  }


//  private boolean checkCore() {
//    final File target = getCoreZipLocation();
//    if (!target.exists()) {
//      try {
//        final URL url = new URL(ANDROID_CORE_URL);
//        PApplet.saveStream(target, url.openStream());
//      } catch (final Exception e) {
//        Base.showWarning("Download Error",
//          "Could not download Android core.zip", e);
//        return false;
//      }
//    }
//    return true;
//  }


  public void statusError(String what) {
    super.statusError(what);
//    new Exception("deactivating RUN").printStackTrace();
    toolbar.deactivate(AndroidToolbar.RUN);
  }


  public void sketchStopped() {
    deactivateRun();
    statusEmpty();
  }


  /**
   * Build the sketch and run it inside an emulator with the debugger.
   */
  public void handleRunEmulator() {
    new Thread() {
      public void run() {
        toolbar.activate(AndroidToolbar.RUN);
        startIndeterminate();
        prepareRun();
        try {
          androidMode.handleRunEmulator(sketch, AndroidEditor.this);
        } catch (SketchException e) {
          statusError(e);
        } catch (IOException e) {
          statusError(e);
        }
        stopIndeterminate();
      }
    }.start();
  }


  /**
   * Build the sketch and run it on a device with the debugger connected.
   */
  public void handleRunDevice() {
    new Thread() {
      public void run() {
        toolbar.activate(AndroidToolbar.RUN);
        startIndeterminate();
        prepareRun();
        try {
          androidMode.handleRunDevice(sketch, AndroidEditor.this);
        } catch (SketchException e) {
          statusError(e);
        } catch (IOException e) {
          statusError(e);
        }
        stopIndeterminate();
      }
    }.start();
  }


  public void handleStop() {
    toolbar.deactivate(AndroidToolbar.RUN);
    stopIndeterminate();
    androidMode.handleStop(this);
  }


  /**
   * Create a release build of the sketch and have its apk files ready.
   * If users want a debug build, they can do that from the command line.
   */
  public void handleExportProject() {
    if (handleExportCheckModified()) {
      new Thread() {
        public void run() {
          toolbar.activate(AndroidToolbar.EXPORT);
          startIndeterminate();
          statusNotice("Exporting a debug version of the sketch...");
          AndroidBuild build = new AndroidBuild(sketch, androidMode);
          try {
            File exportFolder = build.exportProject();
            if (exportFolder != null) {
              Base.openFolder(exportFolder);
              statusNotice("Done with export.");
            }
          } catch (IOException e) {
            statusError(e);
          } catch (SketchException e) {
            statusError(e);
          }
          stopIndeterminate();
          toolbar.deactivate(AndroidToolbar.EXPORT);
        }
      }.start();
    }

//    try {
//      buildReleaseForExport("debug");
//    } catch (final MonitorCanceled ok) {
//      statusNotice("Canceled.");
//    } finally {
//      deactivateExport();
//    }
  }


  /**
   * Create a release build of the sketch and install its apk files on the
   * attached device.
   */
  public void handleExportPackage() {
    // Need to implement an entire signing setup first
    // http://dev.processing.org/bugs/show_bug.cgi?id=1430
    statusError("Exporting signed packages is not yet implemented.");
    deactivateExport();

    // make a release build
//    try {
//      buildReleaseForExport("release");
//    } catch (final MonitorCanceled ok) {
//      statusNotice("Canceled.");
//    } finally {
//      deactivateExport();
//    }

    // TODO now sign it... lots of fun signing code mess to go here. yay!

    // maybe even send it to the device? mmm?
//      try {
//        runSketchOnDevice(AndroidEnvironment.getInstance().getHardware(), "release");
//      } catch (final MonitorCanceled ok) {
//        editor.statusNotice("Canceled.");
//      } finally {
//        editor.deactivateExport();
//      }
  }
}