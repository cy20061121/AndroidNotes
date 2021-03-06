package com.keertech.androidnotes.util;

import android.content.Context;
import android.os.Environment;

import com.keertech.androidnotes.R;
import com.keertech.androidnotes.application.MyApplication;
import com.yftools.DbUtil;
import com.yftools.LogUtil;
import com.yftools.db.sqlite.Selector;
import com.yftools.db.sqlite.SqlInfo;
import com.yftools.db.sqlite.WhereBuilder;
import com.yftools.db.table.DbModel;
import com.yftools.exception.DbException;
import com.yftools.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * *****************************************
 * Description ：数据库操作类
 * Created by cy on 2014/7/29.
 * *****************************************
 */
public class DbOperationManager_backup {

    public static final String DB_NAME = "android_notes.db";
    public static final int DB_VERSION = 1;
    private static DbOperationManager_backup instance;

    private final Context mContext;
    private final DbUtil dbUtil;

    private DbOperationManager_backup() {
        this.mContext = MyApplication.getContext();
        //第一次创建数据库（初始版本号为0）时，不会执行onUpgrade
        //TODO　让他第一次执行时也经过onUpgrade，下面就可以取消
        importDatabase();
        dbUtil = DbUtil.create(mContext, DB_NAME, DB_VERSION, new DbUtil.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtil dbUtil, int oldVersion, int newVersion) {
                if (oldVersion < newVersion) {
                    importDatabase();
                }
            }
        });
        dbUtil.configAllowTransaction(true);
        dbUtil.configDebug(true);
    }

    private void importDatabase() {
       boolean isFirst=false;
        String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + MyApplication.getPackName() + "/databases"; //在手机里存放数据库的位置
        File dbPath = new File(DB_PATH);
        if (!dbPath.exists()) {
            dbPath.mkdirs();
            isFirst=true;
        }
        // String DB_PATH = StorageUtil.getDiskCacheDir(mContext);
        LogUtil.d("DB_PATH=" + DB_PATH);
        String databaseFilename = DB_PATH + "/" + DB_NAME;
        File dbFile = new File(databaseFilename);
        try {
            if (isFirst) {//第一次时
                if (!dbFile.exists()) {
                    InputStream is = mContext.getResources().openRawResource(R.raw.android_notes);
                    FileUtil.writeFile(is, databaseFilename);
                }
            } else {
                dbFile.deleteOnExit();//如果存在就删除
                InputStream is = mContext.getResources().openRawResource(R.raw.android_notes);
                FileUtil.writeFile(is, databaseFilename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DbOperationManager_backup getInstance() {
        if (instance == null) {
            synchronized (DbOperationManager_backup.class) {
                if (instance == null) {
                    instance = new DbOperationManager_backup();
                }
            }
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

//    private static class SingletonHolder {
//        static final DbOperationManager INSTANCE = new DbOperationManager();
//    }
//
//    public static DbOperationManager getInstance() {
//        return SingletonHolder.INSTANCE;
//    }

    public <C> void createTableIfNotExist(Class<C> clazz) throws DbException {
        dbUtil.createTableIfNotExist(clazz);
    }

    public <C> void dropTable(Class<C> clazz) throws DbException {
        dbUtil.dropTable(clazz);
    }

    public <C> void saveOrUpdate(C bean) throws DbException {
        dbUtil.saveOrUpdate(bean);
    }

    public <C> void saveOrUpdate(List<C> beanList) throws DbException {
        dbUtil.saveOrUpdateAll(beanList);
    }

    public <C> void update(C bean, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException {
        dbUtil.update(bean, whereBuilder, updateColumnNames);
    }

    public <C> void update(List<C> beanList, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException {
        dbUtil.updateAll(beanList, whereBuilder, updateColumnNames);
    }

    public List<DbModel> getDbModels(String sql) throws DbException {
        return dbUtil.findDbModelAll(new SqlInfo(sql));
    }

    public <C> List<C> getBeans(Class<C> clazz) throws DbException {
        return dbUtil.findAll(clazz);
    }

    public <C> List<C> getBeans(Selector selector) throws DbException {
        return dbUtil.findAll(selector);
    }

    public <C> C getBeanById(Class<C> clazz, String id) throws DbException {
        return dbUtil.findById(clazz, id);
    }

    public <C> C getBeanFirst(Selector selector) throws DbException {
        return dbUtil.findFirst(selector);
    }

    public <C> void deleteBean(C bean) throws DbException {
        dbUtil.delete(bean);
    }

    public <C> void deleteBeans(List<C> beanList) throws DbException {
        dbUtil.deleteAll(beanList);
    }

    public <C> void deleteBean(Class<C> clazz, WhereBuilder whereBuilder) throws DbException {
        dbUtil.delete(clazz, whereBuilder);
    }

    public <C> void deleteBean(Class<C> clazz, String id) throws DbException {
        dbUtil.deleteById(clazz, id);
    }

    public void execSql(String sql) throws DbException {
        dbUtil.execNonQuery(sql);
    }

    public long count(Selector selector) throws DbException {
        return dbUtil.count(selector);
    }

    public void clearDb() throws DbException {
        dbUtil.dropDb();
    }

    public void close() {
        dbUtil.close();
        instance = null;
    }

}
