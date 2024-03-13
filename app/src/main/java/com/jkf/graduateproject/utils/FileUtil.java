package com.jkf.graduateproject.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.SingleInfom;
import com.jkf.graduateproject.application.myApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    public static String SDPATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/GraduateProject/Users";
    public static String temoPath = SDPATH+"/"+"temp.png";


    /*
    返回文件的路径
     */
    public static String getFileAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, imageUri);
        }

        if ( android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return uriToFileApiQ(context,imageUri);
        }
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    private static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    /*
    创建文件夹，方便本地存储识别的结果,登录成功就会创建文件
     */
    public static void creatRootDir(String sonDir){
        File file = new File(SDPATH+"/"+sonDir);
        if(!file.exists()){
            boolean isSuccess = file.mkdirs();
        }
    }

    /**
     * 此过程为了创建用户下面的一个层级的文件夹，直接目录进行存储
     */

    public static void creatSonDir(String timeStamp){
        String time = SDPATH+"/"+ myApp.getInstance().getUserName()+"/" + timeStamp;
        File file = new File(time);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     *
     * @param bitmap  图片对象  将要保存的数据
     */

    public static String saveBitmap( Bitmap bitmap){
        File file = new File(temoPath);
        if(file.exists()){
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return temoPath;
    }
    /*
    将文件进行复制
     */

    public static void copyFile(String pathFrom, String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(pathFrom)).getChannel();
            outputChannel = new FileOutputStream(new File(pathTo)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }
    /*
    删除源文件
     */
    public static void deleteFile(String path){
        if(path.isEmpty()){
            return;
        }
        File file = new File(path);
        if(!file.exists()){
            return;
        }
        boolean is = file.delete();
    }

    /*
    模拟数据加载，后期数据的加载通过此方法，
    模拟分页：每次加载的数据大小为4个Singlefom的数据
    保存时的层级都为 ：GraduateProject/Username/timestamp/Singlefom
    单个的Singlefom我们包含三个文件，两张图片和单个txt,主要的信息存在txt内部，咱们直接进行加载即可
     */
    /**
     *
     * @param dirPath   文件目录
     * @param currentSize  目前展示的个数
     * @param pageCount   每次加载的个数
     * @return  返回将要加载的数据集资源
     */
    public static List<SingleInfom> loadDataFromDir(String dirPath,int currentSize,int pageCount){
        List<SingleInfom> currenList = new ArrayList<>();
        File file = new File(dirPath);
        if(!file.exists()){
            return currenList;
        }
        File [] files = file.listFiles();
        //判断大小进行对比
        int allFile = files.length;
        //判断终止加载的位置
        int index = Math.min(allFile, currentSize + pageCount);
        //数据的装载
        for(int m = currentSize;m < index; m++){
            File sondir = files[m];
            //错误情况下为单个文件
            if(sondir.isFile()){
                continue;
            }
            //正常为文件夹，且包含三个文件
            else{
                File[] innerFile = sondir.listFiles();
                if(innerFile == null || innerFile.length!=3){
                    continue;
                }
                else{
                    for(File temp:innerFile){
                        String tempName = temp.getName();
                        if(tempName.endsWith(".txt")){
                            try {
                                FileReader reader = new FileReader(file);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String line;
                                SingleInfom singleInfom = new SingleInfom();
                                while((line=bufferedReader.readLine())!=null){
                                    if(line.contains("识别时间")){
                                        singleInfom.setCrackTime(line.split(":",2)[1]);
                                    }
                                    else if(line.contains("裂缝长度")){
                                        singleInfom.setCrackLength(line.split(":",2)[1]);
                                    }else if(line.contains("裂缝面积")){
                                        singleInfom.setCrackArea(line.split(":",2)[1]);
                                    }else if(line.contains("最大宽度")){
                                        singleInfom.setCrack_maxWidth(line.split(":",2)[1]);
                                    }else if(line.contains("实际宽度")){
                                        singleInfom.setCrack_realWidth(line.split(":",2)[1]);
                                    }
                                    else if(line.contains("原图路径")){
                                        singleInfom.setCrakOriginalPath(line.split(":",2)[1]);
                                    }
                                    else if(line.contains("裂缝路径")){
                                        singleInfom.setCrackPath(line.split(":",2)[1]);
                                    }
                                    else if(line.contains("种类")){
                                        singleInfom.setKind(line.split(":",2)[1]);
                                    }
                                    else if(line.contains("裂缝描述")){
                                        singleInfom.setDescription(line.split(":",2)[1]);
                                    }
                                }
                                currenList.add(singleInfom);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
        return currenList;
    }

    /**
     *
     * @param currentSize   目前一共展示了多少个
     * @param pageCount    每一次加载最多加载的个数
     * @return          返回增加的个数
     */

    public static List<SingleInfom> loadDataFromDir(int currentSize,int pageCount){
        String dirPath = SDPATH+"/"+myApp.getInstance().getUserName();
        List<SingleInfom> currenList = new ArrayList<>();
        File file = new File(dirPath);
        if(!file.exists()){
            return currenList;
        }
        File [] files = file.listFiles();
        //保证后后识别的永远再后面
        Arrays.sort(files);
        //判断大小进行对比
        int allFile = files.length;
        //判断终止加载的位置
        int index = Math.min(allFile, currentSize + pageCount);

        //数据的装载
        for(int m = currentSize;m < index; m++){
            File sondir = files[m];
            //错误情况下为单个文件
            if(sondir.isFile()){
                continue;
            }
            //正常为文件夹，且包含三个文件
            else{
                File[] innerFile = sondir.listFiles();
                if(innerFile == null || innerFile.length!=3){
                    continue;
                }
                else{
                    for(File temp:innerFile){
                        String tempName = temp.getName();
                        if(tempName.endsWith(".txt")){
                            try {
                                SingleInfom singleInfom ;
                                FileInputStream in = new FileInputStream(temp);
                                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                                singleInfom = (SingleInfom)objectInputStream.readObject();
                                currenList.add(singleInfom);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
        return currenList;
    }

    /**
     *
     * @param singleInfo 写入txt的对象
     * @param txtPath  路径，将要写入的路径
     */
    public static void saveTxtFile(SingleInfom singleInfo,String txtPath){
        File file = new File(txtPath);
        //没有就创建文件
        if(file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(singleInfo);
            fos.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadImageToIm(Context context , String path, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f4)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(path)
                .apply(options)
                .into(imageView);
    }

}
