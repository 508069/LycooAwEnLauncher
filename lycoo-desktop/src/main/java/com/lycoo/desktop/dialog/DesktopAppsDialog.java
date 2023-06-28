package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.util.SparseArray;

import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ResourceUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.helper.DesktopItemManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/11
 */
public class DesktopAppsDialog extends AppsDialog {
    private static final String TAG = DesktopAppsDialog.class.getSimpleName();

    private int mTag;

    private Context mContext;
    private  SparseArray<String> mItemSources = null;
    private SparseArray<String> mItemLables = null;
    private SparseArray<String> mItemIcons=null;

    private SparseArray<String> itemImages=null;

    private boolean mConfigxx = false;

    public DesktopAppsDialog(Context context, int themeResId, int numColumns, int tag) {
        super(context, themeResId, numColumns);
        this.mTag = tag;
        mContext = context;
        mConfigxx = context.getResources().getBoolean(R.bool.config_replaceAppWithInitializeName);
//        mConfigxx = true;

        if (mConfigxx){
            mItemSources = new SparseArray<>();
            mItemLables = new SparseArray<>();
            mItemIcons = new SparseArray<>();
            itemImages = new SparseArray<>();

            String[] arrays;
            String[] configs;

            arrays=mContext.getResources().getStringArray(ResourceUtils.getIdByName(
                    mContext,
                    "array",
                    "desktop_item_sources"));
            for (String config : arrays) {
                configs = config.split("__");
                mItemSources.put(Integer.valueOf(configs[0]), configs[1]);


            }
            arrays=mContext.getResources().getStringArray(ResourceUtils.getIdByName(
                    mContext,
                    "array",
                    "desktop_item_labels"));
            for (String config : arrays) {
                configs = config.split("__");
                mItemLables.put(Integer.valueOf(configs[0]), configs[1]);
            }
            arrays=mContext.getResources().getStringArray(ResourceUtils.getIdByName(
                    mContext,
                    "array",
                    "desktop_item_icons"));
            for (String config : arrays) {
                configs = config.split("__");
                mItemIcons.put(Integer.valueOf(configs[0]), configs[1]);
            }

            arrays = mContext.getResources().getStringArray(ResourceUtils.getIdByName(
                    mContext,
                    "array",
                    "desktop_item_images"));

            for (String config : arrays) {
                configs = config.split("__");
                itemImages.put(Integer.valueOf(configs[0]), configs[1]);
            }

        }





//        String[] array = mContext.getResources().getStringArray(R.array.desktop_item_sources);
//        ResourceUtils.getIdArrayByName(
//                context,
//                "array",
//                "desktop_item_sources");
    }

    @Override
    Observable<List<String>> getPackageNames() {
        return DesktopItemManager.getInstance(mContext).getPackageNames();
    }

    @Override
    void doUpdate(ResolveInfo resolveInfo) {
        mCompositeDisposable.add(
                DesktopItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .doOnNext(desktopItemInfo -> {
                            desktopItemInfo.setPackageName(resolveInfo.activityInfo.packageName);
                            String label = ApplicationUtils.getAppLabel(mContext,resolveInfo.activityInfo.packageName);
                            desktopItemInfo.setLabel(label);
                            desktopItemInfo.setIconUrl("");

                            if (mConfigxx) {

                                for (int i=0;i<mItemSources.size();i++){
                                    int tag = mItemSources.keyAt(i);
                                    String type = mItemSources.valueAt(i);

                                    if (type.contains(resolveInfo.activityInfo.packageName)){
                                        label = mItemLables.get(tag);
                                        String itemIcon = mItemIcons.get(tag);
                                        String imgSource=itemImages.get(tag);
                                        desktopItemInfo.setLabel(label);
                                        desktopItemInfo.setIconUrl(itemIcon);
                                        desktopItemInfo.setImageUrl(imgSource);
                                        break;
                                    }
                                }

                            }

                            // 更新数据库
                            DesktopItemManager.getInstance(mContext).updateItemInfo(desktopItemInfo);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(desktopItemInfo -> {
                            // 更新UI
                            DesktopItemManager.getInstance(mContext).getItem(mTag).update(desktopItemInfo);
                            dismiss();
                        }, throwable -> {
                            LogUtils.error(TAG, "bind or replace failed, error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }


}
