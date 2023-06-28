package com.example.mysettings.settingWifi.adapter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysettings.R;
import com.lycoo.commons.util.LogUtils;


import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ViewHolder> {
    private static final String TAG = WifiListAdapter.class.getSimpleName();
    private static List<ScanResult> mWifiList;
    private static OnItemClickListener onItemClickListener;
    private WifiManager wifiManager;
    private static WifiInfo isConnectInfo;

    public WifiListAdapter(List<ScanResult> wifiList, WifiManager wifiManager) {
        mWifiList = wifiList;
        this.wifiManager=wifiManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
//        if (position == 0) {
//            holder.itemView.requestFocus();
//        }
        // 设置item获取焦点时的监听事件
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(Color.parseColor("#63000000"));
                }else {
                    v.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
            }
        });
        isConnectInfo = wifiManager.getConnectionInfo();
        String wifiName = mWifiList.get(position).SSID;
        if (isConnectInfo != null && isConnectInfo.getNetworkId() != -1) {
            LogUtils.info(TAG,"WIFI已经连接");
            String ssid = isConnectInfo.getSSID();// 已连接WiFi的SSID为ssid
            LogUtils.info(TAG,"当前wifi ："+ssid+"  扫描的wifi ：" + wifiName);
            if(ssid.equals("\"" + wifiName + "\"")){
                LogUtils.info(TAG,"已连接显示");
                holder.wifiConnect.setEnabled(true);
                holder.wifiConnect.setText("已连接");
            }else {
                holder.wifiConnect.setText(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mWifiList.size();
    }


    private static int getSignalIcon(int level) {
        if (level >= -50) {
            return R.drawable.ic_wifi_signal_3;
        } else if (level >= -70) {
            return R.drawable.ic_wifi_signal_2;
        } else {
            return R.drawable.ic_wifi_signal_1;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ssidTextView;
        ImageView signalImageView;
        TextView wifiConnect;
        public ViewHolder(View itemView) {
            super(itemView);
            ssidTextView = itemView.findViewById(R.id.wifi_name);
            signalImageView = itemView.findViewById(R.id.wifi_icon);
            wifiConnect=itemView.findViewById(R.id.wifi_enable);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        ScanResult scanResult = mWifiList.get(position);
                        // 调用OnItemClickListener的onItemClick方法
                        if(isConnectInfo.getSSID().equals("\"" + scanResult.SSID + "\"")) {
                            onItemClickListener.onItemClick(true,view, position, isConnectInfo,null);
                        }else {
                            onItemClickListener.onItemClick(false,view, position, null,scanResult);
                        }
                    }
                }
            });
        }
        public void bind(int position){
            ScanResult scanResult = mWifiList.get(position);
            ssidTextView.setText(scanResult.SSID);
            signalImageView.setImageResource(getSignalIcon(scanResult.level));
        }
    }

    // 定义OnItemClickListener接口
    public interface OnItemClickListener {
        void onItemClick(boolean isConnect,View view, int position, WifiInfo wifiInfo,ScanResult scanResult);
    }
}
