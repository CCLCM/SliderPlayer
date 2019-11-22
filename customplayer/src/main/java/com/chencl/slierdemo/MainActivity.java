package com.chencl.slierdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.chencl.slierdemo.image.ImageSlider;
import com.chencl.slierdemo.video.VideoPlayer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AutoPlay ";
    private static String mPath;
    private ArrayList<String> fileList;

    private ArrayList<String> imagesFilPaths;
    private ArrayList<String> videoFilPaths;

    private static int REQUEST_PERMISSION_CODE = 1;

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String[] VIDEO_EXTENSIONS = {"3gp", "amv",
            "avb", "avd", "avi", "flh", "fli", "flv", "flx", "gvi", "gvp",
            "hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf", "ivr",
            "m4v", "m75", "meta", "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv",
            "mnv", "mod", "modd", "moff", "moi", "moov", "mov", "movie",
            "mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4",
            "mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls", "mpv", "mpv2", "mqv",
            "msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve",
            "mvp", "mxf", "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx",
            "osp", "par", "pds", "pgi", "piv", "playlist", "pmf", "prel",
            "pro", "prproj", "psh", "pva", "pvr", "pxv", "qt", "qtch", "qtl",
            "qtm", "qtz", "rcproject", "rdb", "rec", "rm", "rmd", "rmp", "rms",
            "rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt",
            "scm", "scm", "scn", "sec", "seq", "sfvidcap", "smil", "smk",
            "sml", "smv", "spl", "ssm", "str", "stx", "svi", "swf", "swi",
            "swt", "tda3mt", "tivo", "tix", "tod", "tp", "tp0", "tpd", "tpr",
            "trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr", "veg",
            "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo",
            "wma"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        fileList = new ArrayList();
        fileList.clear();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                return;
            }
        }
        havePermissionToStart();
    }

    /**
     * 已经获取用户权限,开始播放视频或者展示图片
     */
    private void havePermissionToStart() {
        Intent getPath = getIntent();
        String action = getPath.getAction();
        if (getPath.ACTION_VIEW.equals(action)) {
            videoFilPaths = new ArrayList<>();
            mPath = getPath.getDataString();
            videoFilPaths.add(mPath);
            startVideoIntent();
        } else {

            File baseFile = new File(takePicRootDir(this));
            Log.d(TAG, "chencl_ baseFile  " + baseFile.getAbsolutePath());

            videoFilPaths = filePath(baseFile, videoFilenameFilter);

            if (videoFilPaths != null && videoFilPaths.size() != 0) {
                for (String aa : videoFilPaths) {
                    Log.d(TAG, "chencl_ videoFilPaths  " + aa);
                }

                startVideoIntent();

            } else {
                imagesFilPaths = new ArrayList<String>();
                LinearLayout l = (LinearLayout) findViewById(R.id.sliderbox);
                imagesFilPaths = filePath(baseFile, imageFilenameFilter);

                if (imagesFilPaths != null && imagesFilPaths.size() != 0) {
                    for (String aa : imagesFilPaths) {
                        Log.d(TAG, "chencl_ image " + aa);
                    }
                }

                ImageSlider s = new ImageSlider(this, imagesFilPaths);
                l.addView(s.initView());
            }
        }
    }

    /**
     * 用户点击权限后的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == 0) {

            }
            havePermissionToStart();
        }
    }


    /**
     * 开启播放视频页
     */
    private void startVideoIntent() {
        SystemClock.sleep(500);
        Intent intent = new Intent(this, VideoPlayer.class);
        intent.putStringArrayListExtra("videoURL", videoFilPaths);
        startActivity(intent);
        finish();
    }


    /**
     * 照片过滤器
     */
    FileFilter imageFilenameFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            // 测试发现有不清楚的图片 原因是 thumbnails存在缩略图,剔除掉缩略图图片

            if (file.getAbsolutePath().contains("thumbnails")){
                return false;
            }

            String s = file.getName();
            return s.toLowerCase().endsWith("jpeg")
                    | s.endsWith("jpg")
                    | s.endsWith("png");
        }
    };

    /**
     * 视频过滤器
     */
    FileFilter videoFilenameFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String s = file.getName();
            String apkName = s.substring(s.lastIndexOf(".") + 1);
            return Arrays.asList(VIDEO_EXTENSIONS).contains(apkName);
        }
    };


    /**
     * 判断当前存储卡是否可用
     */
    public boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 获取需要查询的文件夹
     */

    public String takePicRootDir(Context context) {
        if (checkSDCardAvailable()) {
            return Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator;
        } else {
            return context.getFilesDir().getAbsolutePath() + File.separator + "DCIM" + File.separator;
        }
    }


    /**
     * 描述：  获取DCIM文件夹下的所有的 图片 以及 视频列表
     */
    private static ArrayList<String> filePath(File file, FileFilter filenameFilter) {

        ArrayList<String> list = new ArrayList<String>();
        File[] files = file.listFiles(filenameFilter);

        if (files == null) {
            return null;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(filePath(f, filenameFilter));
            } else {
                list.add(f.getAbsolutePath());
            }
        }
        return list;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
