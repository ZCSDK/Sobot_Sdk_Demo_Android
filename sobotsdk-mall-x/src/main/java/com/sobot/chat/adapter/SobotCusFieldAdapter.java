package com.sobot.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.SobotCusFieldDataInfo;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */

public class SobotCusFieldAdapter extends SobotBaseAdapter<SobotCusFieldDataInfo> implements Filterable {

    private MyViewHolder myViewHolder;
    private Context mContext;
    private Activity mActivity;
    private int fieldType;
    private MyFilter mFilter;

    //满足过滤条件的数据
    private List<SobotCusFieldDataInfo> displayList;
    //过滤时候的总数据 这个是不变的数据
    private List<SobotCusFieldDataInfo> adminList;
    //适配器的adpater

    public SobotCusFieldAdapter(Activity activity, Context context, List<SobotCusFieldDataInfo> list, int fieldType) {
        super(activity, list);
        this.mContext = context;
        this.mActivity = activity;
        this.fieldType = fieldType;

        this.adminList = list;
        displayList = list;
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, ResourceUtils.getIdByName(mContext, "layout", "sobot_activity_cusfield_listview_items"), null);
            myViewHolder = new MyViewHolder(mActivity,convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        myViewHolder.categorySmallTitle.setText(displayList.get(position).getDataName());

        if (ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            myViewHolder.categorySmallCheckBox.setVisibility(View.VISIBLE);
            if (displayList.get(position).isChecked()) {
                myViewHolder.categorySmallCheckBox.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_post_category_checkbox_pressed"));
            } else {
                myViewHolder.categorySmallCheckBox.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_post_category_checkbox_normal"));
            }
        } else {
            myViewHolder.categorySmallCheckBox.setVisibility(View.GONE);
            if (displayList.get(position).isChecked()) {
                myViewHolder.categorySmallIshave.setVisibility(View.VISIBLE);
                myViewHolder.categorySmallIshave.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_work_order_selected_mark"));
            } else {
                myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            }
        }

        if (displayList.size() >= 2) {
            if (position == displayList.size() - 1) {
                myViewHolder.categorySmallline.setVisibility(View.GONE);
            } else {
                myViewHolder.categorySmallline.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.categorySmallline.setVisibility(View.GONE);
        }

        return convertView;
    }

    class MyViewHolder {

        private TextView categorySmallTitle;
        private ImageView categorySmallIshave;
        private ImageView categorySmallCheckBox;
        private View categorySmallline;
        private Activity mActivity;

        MyViewHolder(Activity activity, View view) {
            this.mActivity = activity;
            categorySmallTitle = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_title"));
            categorySmallIshave = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_ishave"));
            categorySmallCheckBox = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_checkbox"));
            categorySmallline = view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_activity_cusfield_listview_items_line"));
            displayInNotch(categorySmallTitle);
        }

        public void displayInNotch(final View view) {
            if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
                // 支持显示到刘海区域
                NotchScreenManager.getInstance().setDisplayInNotch(mActivity);
                // 设置Activity全屏
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                // 获取刘海屏信息
                NotchScreenManager.getInstance().getNotchInfo(mActivity, new INotchScreen.NotchScreenCallback() {
                    @Override
                    public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                        if (notchScreenInfo.hasNotch) {
                            for (Rect rect : notchScreenInfo.notchRects) {
                                view.setPadding((rect.right > 110 ? 110 : rect.right), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                });

            }
        }
    }


    //返回过滤器
    public MyFilter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }


    public class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                results.values = adminList;
                results.count = adminList.size();
            } else {
                String prefixString = prefix.toString();

                final ArrayList<SobotCusFieldDataInfo> newValues = new ArrayList<>();

                for (int i = 0; i < adminList.size(); i++) {
                    final String value = adminList.get(i).getDataName();
                    if (value.contains(prefixString)) {//我这里的规则就是筛选出和prefix相同的元素
                        newValues.add(adminList.get(i));
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            displayList = (List<SobotCusFieldDataInfo>) results.values;
            if (displayList.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


}