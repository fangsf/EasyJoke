package com.fangsf.easyjoke.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.fangsf.easyjoke.R;
import com.fangsf.easyjoke.adapter.SelectImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {
    private static final String TAG = "SelectImageActivity";


    // 照片多选的模式
    public static final int MODE_MULTI = 0x0011;
    // 照片的多选模式
    public static final int MODE_SINGLE = 0x0012;
    // 是否显示相机的extra_key
    public static final String EXTRA_SHOW_CAMERA = "extra_show_camera";
    public static boolean mIsShowCamera;

    // 照片最多选择的个数
    public static final String EXTRA_SELECT_MAX_COUNT = "extra_select_max_count";
    // 照片的 集合
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "extra_default_selected_list";
    // 照片选择的模式
    public static final String EXTRA_SELECT_MODE = "extra_select_mode";
    // 返回选择图片列表的 extra_key
    public static final String EXTRA_RESULT = "extra_result";
    // 加载所有的数据
    public static final int LOADER_TYPE = 0x0023;


    // 图片集合
    private ArrayList<String> mResultList;

    // 当前 图片的选择模式
    private int mMode;
    // 最多可以选择多少张照片
    private int mMaxCount;

    // 照片信息的集合
    private List<String> mImageList;

    private RecyclerView mRcImage;
    private TextView mTvPreview;
    private TextView mTvChooseNums;
    private TextView mTvConfirm;
    private SelectImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_choose);

        initViews();

        getIntents();


        // 获取相册的图片
        initImageList();
    }

    private void initViews() {
        mRcImage = findViewById(R.id.rc_image);
        mTvPreview = findViewById(R.id.tv_preview);
        mTvChooseNums = findViewById(R.id.tv_choose_nums);
        mTvConfirm = findViewById(R.id.tv_confirm);
    }

    /**
     * contentProvider 获取图片
     */
    private void initImageList() {
        // 这里可以考虑使用 thread, anyncTask, 加载图片, 获取使用 getLoaderManager() (异步加载神器, 自动更新ui)

        getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallbacks);
        /**
         *  // 查询图片需要的信息
         private final String[] IMAGE_PROJECTION = {
         MediaStore.Images.Media.DATA,
         MediaStore.Images.Media.DISPLAY_NAME,
         MediaStore.Images.Media.DATE_ADDED,
         MediaStore.Images.Media.MIME_TYPE,
         MediaStore.Images.Media.SIZE,
         MediaStore.Images.Media._ID
         };

         CursorLoader cursorLoader = new CursorLoader(SelectImageActivity.this,
         MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
         IMAGE_PROJECTION, IMAGE_PROJECTION[4] + ">0 AND " +
         IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
         new String[]{"image/jpeg", "image/png"},
         IMAGE_PROJECTION[2] + " DESC");

         */

    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        // 查询图片需要的信息
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        };

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            // 查询数据
            CursorLoader cursorLoader = new CursorLoader(SelectImageActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION, IMAGE_PROJECTION[4] + ">0 AND " +
                    IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
                    new String[]{"image/jpeg", "image/png"},
                    IMAGE_PROJECTION[2] + " DESC");
            // selection   ->   _size>0 AND mime_type=? OR mime_type=?
//            String selection = cursorLoader.getSelection(); // 查询条件
//            String[] selectionArgs = cursorLoader.getSelectionArgs(); // 查询条件参数
//            Log.i(TAG, "onCreateLoader: " + selection + " selectionArgs -> " + selectionArgs);


            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor data) {
            // 解析到数据
            if (data != null && data.getCount() > 0) {
                ArrayList<String> images = new ArrayList<>();
                if (mIsShowCamera) {
                    // 是否拍照
                    images.add("");
                }


                while (data.moveToNext()) {
                    //getColumnIndex() 和 getColumnIndexOrThrow() 找不到列名会抛出异常
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    images.add(path);
                }

                showImageList(images);

            }
        }


        @Override
        public void onLoaderReset(Loader loader) {

        }
    };

    /**
     * 显示数据集合
     *
     * @param images
     */
    private void showImageList(ArrayList<String> images) {
        Log.i(TAG, " images: 图片个数-> " + images.size());
        mImageAdapter = new SelectImageAdapter(R.layout.item_image, images);
        mRcImage.setLayoutManager(new GridLayoutManager(this, 4));
        mRcImage.setAdapter(mImageAdapter);

    }


    private void getIntents() {
        // 获取上一个页面设置的参数
        Intent intent = getIntent();
        if (intent != null) {
            mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode);
            mMaxCount = intent.getIntExtra(EXTRA_SELECT_MAX_COUNT, mMaxCount);
            mIsShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mIsShowCamera);
            mResultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
            if (mResultList == null) {
                mResultList = new ArrayList<>();
            }
        }
    }
}
