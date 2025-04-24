package com.sobot.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sobot.chat.activity.base.SobotDialogBaseActivity;
import com.sobot.chat.adapter.SobotPostCascadeAdapter;
import com.sobot.chat.api.model.SobotCusFieldDataInfo;
import com.sobot.chat.api.model.SobotFieldModel;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 级联系自定义字段
 */
public class SobotPostCascadeActivity extends SobotDialogBaseActivity {

    private SobotPostCascadeAdapter categoryAdapter;
    private ListView listView;
    private LinearLayout sobot_btn_cancle;
    private TextView sobot_tv_title;
    private ImageView sobot_btn_back;

    private SparseArray<List<SobotCusFieldDataInfo>> tmpMap;
    private List<SobotCusFieldDataInfo> tmpDatas;
    private List<SobotCusFieldDataInfo> selectCusFieldDataInfos;
    private int currentLevel = 1;
    private String fieldId;

    private List<SobotCusFieldDataInfo> cusFieldDataInfoList;
    private SobotFieldModel cusField;


    @Override
    protected int getContentViewResId() {
        return ResourceUtils.getResLayoutId(this, "sobot_activity_post_category");
    }

    @Override
    protected void initView() {
        tmpMap = new SparseArray<>();
        tmpDatas = new ArrayList<>();
        selectCusFieldDataInfos = new ArrayList<>();
        sobot_btn_cancle = (LinearLayout) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_btn_cancle"));
        sobot_tv_title = (TextView) findViewById(ResourceUtils.getIdByName(
                this, "id", "sobot_tv_title"));
        sobot_btn_back = (ImageView) findViewById(ResourceUtils.getIdByName(this, "id", "sobot_btn_back"));
        listView = (ListView) findViewById(ResourceUtils.getResId(getBaseContext(), ("sobot_activity_post_category_listview")));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCusFieldDataInfos.add(categoryAdapter.getDatas().get(position));
                if (getNextLevelList(categoryAdapter.getDatas().get(position).getDataId()).size() > 0) {
                    currentLevel++;
                    showDataWithLevel(position, categoryAdapter.getDatas().get(position).getDataId());
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                    intent.putExtra("fieldType", ZhiChiConstant.WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE);
                    String typeName = "";
                    String typeValue = "";
                    for (int i = 0; i < selectCusFieldDataInfos.size(); i++) {
                        if (i == (selectCusFieldDataInfos.size() - 1)) {
                            typeName = typeName + selectCusFieldDataInfos.get(i).getDataName();
                            typeValue = typeValue + selectCusFieldDataInfos.get(i).getDataValue();
                        } else {
                            typeName = typeName + selectCusFieldDataInfos.get(i).getDataName() + ",";
                            typeValue = typeValue + selectCusFieldDataInfos.get(i).getDataValue() + ",";
                        }
                    }
                    intent.putExtra("category_typeName", typeName);
                    intent.putExtra("category_fieldId", fieldId);
                    intent.putExtra("category_typeValue", typeValue);
                    setResult(ZhiChiConstant.work_order_list_display_type_category, intent);
                    for (int i = 0; i < tmpMap.get(currentLevel).size(); i++) {
                        tmpMap.get(currentLevel).get(i).setChecked(i == position);
                    }
                    categoryAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });

        sobot_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sobot_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();

            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            fieldId = bundle.getString("fieldId");
            cusField = (SobotFieldModel) bundle.getSerializable("cusField");
        }

        sobot_tv_title.setText(ResourceUtils.getResString(this, "sobot_choice_classification"));

        if (cusField != null && cusField.getCusFieldDataInfoList() != null) {
            cusFieldDataInfoList = cusField.getCusFieldDataInfoList();
        } else {
            cusFieldDataInfoList = new ArrayList<>();
        }

        //存贮一级List
        currentLevel = 1;
        tmpMap.put(1, getNextLevelList(""));
        if (cusFieldDataInfoList != null && cusFieldDataInfoList.size() != 0) {
            showDataWithLevel(-1, "");
        }
        sobot_btn_back.setVisibility(View.GONE);
    }

    private void showDataWithLevel(int position, String dataId) {
        sobot_btn_back.setVisibility(currentLevel > 1 ? View.VISIBLE : View.GONE);
        if (position >= 0) {
            tmpMap.put(currentLevel, getNextLevelList(dataId));
        }

        ArrayList<SobotCusFieldDataInfo> currentList = (ArrayList<SobotCusFieldDataInfo>) tmpMap.get(currentLevel);
        if (currentList != null) {
            notifyListData(currentList);
        }
    }

    private void notifyListData(List<SobotCusFieldDataInfo> currentList) {
        tmpDatas.clear();
        tmpDatas.addAll(currentList);
        if (categoryAdapter != null) {
            categoryAdapter.notifyDataSetChanged();
        } else {
            categoryAdapter = new SobotPostCascadeAdapter(SobotPostCascadeActivity.this, SobotPostCascadeActivity.this, tmpDatas);
            listView.setAdapter(categoryAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        if (currentLevel <= 1) {
            finish();
        } else {
            currentLevel--;
            if (currentLevel == 1) {
                sobot_btn_back.setVisibility(View.GONE);
            }
            if (currentLevel > 1) {
                sobot_btn_back.setVisibility(View.VISIBLE);
            }
            selectCusFieldDataInfos.remove(selectCusFieldDataInfos.size() - 1);
            List<SobotCusFieldDataInfo> sobotTypeModels = tmpMap.get(currentLevel);
            notifyListData(sobotTypeModels);
        }
    }

    //获取下一级显示数据
    private List<SobotCusFieldDataInfo> getNextLevelList(String parentDataId) {
        List<SobotCusFieldDataInfo> curLevelList = new ArrayList<>();
        curLevelList.clear();
        for (int i = 0; i < cusFieldDataInfoList.size(); i++) {
            if (StringUtils.isEmpty(parentDataId)) {
                if (StringUtils.isEmpty(cusFieldDataInfoList.get(i).getParentDataId())) {
                    cusFieldDataInfoList.get(i).setHasNext(isHasNext(cusFieldDataInfoList.get(i).getDataId()));
                    curLevelList.add(cusFieldDataInfoList.get(i));
                }
            } else {
                if (parentDataId.equals(cusFieldDataInfoList.get(i).getParentDataId())) {
                    cusFieldDataInfoList.get(i).setHasNext(isHasNext(cusFieldDataInfoList.get(i).getDataId()));
                    curLevelList.add(cusFieldDataInfoList.get(i));
                }
            }
        }
        return curLevelList;
    }

    //是否还有下一级数据，有的话显示右箭头
    private boolean isHasNext(String parentDataId) {
        for (int i = 0; i < cusFieldDataInfoList.size(); i++) {
                if (parentDataId.equals(cusFieldDataInfoList.get(i).getParentDataId())) {
                    return true;
                }
        }
        return false;
    }
}