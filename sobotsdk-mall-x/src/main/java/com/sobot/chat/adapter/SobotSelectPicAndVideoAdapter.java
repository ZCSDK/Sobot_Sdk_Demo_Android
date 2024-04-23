package com.sobot.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.chat.R;
import com.sobot.chat.adapter.model.SobotAlbumFile;
import com.sobot.chat.widget.horizontalgridpage.SobotRecyclerCallBack;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.List;

/**
 * android 14 部分权限 允许后的回显界面
 */
public class SobotSelectPicAndVideoAdapter extends RecyclerView.Adapter<SobotSelectPicAndVideoAdapter.ViewHolder> {
    private Context mContext;
    private List<SobotAlbumFile> list;
    private SobotRecyclerCallBack callBack;
    private int mSelectedPos = -1;//保存当前选中的position 重点！
    private myClickListener clickListener;

    public SobotSelectPicAndVideoAdapter(Context context, List<SobotAlbumFile> list, SobotRecyclerCallBack callBack) {
        mContext = context;
        this.list = list;
        this.callBack = callBack;
    }

    //得到当前选中的位置
    public int getmSelectedPos() {
        return mSelectedPos;
    }

    public void setmSelectedPos(int mSelectedPos) {
        this.mSelectedPos = mSelectedPos;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 设置监听
     *
     * @param holder 监听对象
     */
    public void setListener(RecyclerView.ViewHolder holder) {
        // 设置监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onItemClickListener(v, (Integer) v.getTag());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callBack.onItemLongClickListener(v, (Integer) v.getTag());
                return true;
            }
        });
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.sobot_select_pic_and_video_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(mContext, itemView);
        setListener(viewHolder);//设置监听
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SobotSelectPicAndVideoAdapter.ViewHolder viewHolder, int position) {
        final ViewHolder contact = (ViewHolder) viewHolder;
        final int finalPosition = position;
        SobotAlbumFile albumFile = (SobotAlbumFile) list.get(position);
        if (albumFile != null) {
            if (position == (list.size() - 1)) {
                contact.ll_open_other.setVisibility(View.VISIBLE);
                contact.sobot_iv_img.setVisibility(View.GONE);
                contact.check_box.setVisibility(View.GONE);
                contact.tv_duration.setVisibility(View.GONE);
                contact.ll_open_other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onClickOtherListener();
                        }
                    }
                });
            } else {
                contact.ll_open_other.setOnClickListener(null);
                contact.ll_open_other.setVisibility(View.GONE);
                contact.sobot_iv_img.setVisibility(View.VISIBLE);
                contact.check_box.setVisibility(View.VISIBLE);
                contact.tv_duration.setVisibility(View.VISIBLE);
                if (albumFile.getUri() != null) {
                    SobotBitmapUtil.display(mContext, albumFile.getUri(), contact.sobot_iv_img);
                }
                if (albumFile.getMediaType() == SobotAlbumFile.TYPE_VIDEO) {
                    contact.tv_duration.setVisibility(View.VISIBLE);
                    contact.tv_duration.setText(convertDuration(albumFile.getDuration()));
                } else {
                    contact.tv_duration.setVisibility(View.GONE);
                }
            }

            if (mSelectedPos == position) {
                contact.check_box.setChecked(true);
                contact.view_masking_select.setVisibility(View.VISIBLE);
            } else {
                contact.check_box.setChecked(false);
                contact.view_masking_select.setVisibility(View.GONE);
            }

            contact.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedPos != finalPosition) {//当前选中的position和上次选中不是同一个position 执行
                        contact.check_box.setChecked(true);
                        contact.view_masking_select.setVisibility(View.VISIBLE);
                        if (mSelectedPos != -1) {//判断是否有效 -1是初始值 即无效 第二个参数是Object 随便传个int 这里只是起个标志位
                            notifyItemChanged(mSelectedPos, 0);
                        }
                        mSelectedPos = finalPosition;
                    } else {
                        //点击是选中的本身，取消选中
                        mSelectedPos = -1;
                        contact.check_box.setChecked(false);
                        contact.view_masking_select.setVisibility(View.VISIBLE);
                    }
                    if (clickListener != null) {
                        clickListener.onCheckListener();
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        //payloads即有效负载，当首次加载或调用notifyDatasetChanged() ,notifyItemChange(int position)进行刷新时，payloads为empty 即空
        if (payloads.isEmpty()) {
            //如果payloads没数据，说明不是局部刷新，下面这句是关键，通过源码看 会执行不带payloads参数的onBindViewHolder
            onBindViewHolder(holder, position);
        } else {
            //当调用notifyItemChange(int position, Object payload)进行布局刷新时，payloads不会empty ，所以真正的布局刷新应该在这里实现 重点！
            if (mSelectedPos == position) {
                holder.check_box.setChecked(true);
                holder.view_masking_select.setVisibility(View.VISIBLE);
            } else {
                holder.check_box.setChecked(false);
                holder.view_masking_select.setVisibility(View.GONE);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView sobot_iv_img;
        private TextView tv_duration;
        private CheckBox check_box;
        private LinearLayout ll_open_other;
        private View view_masking_select;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.sobot_iv_img = itemView.findViewById(R.id.sobot_iv_img);
            this.tv_duration = itemView.findViewById(R.id.tv_duration);
            check_box = itemView.findViewById(R.id.check_box);
            ll_open_other = itemView.findViewById(R.id.ll_open_other);
            view_masking_select = itemView.findViewById(R.id.view_masking_select);
        }
    }


    public List<SobotAlbumFile> getList() {
        return list;
    }

    public void updateList(List<SobotAlbumFile> albumFileList) {
        list.clear();
        list.addAll(albumFileList);
        notifyDataSetChanged();
    }

    public interface myClickListener {
        void onClickOtherListener();

        void onCheckListener();
    }

    public myClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(myClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public String convertDuration(@IntRange(from = 1) long duration) {
        duration /= 1000;
        int hour = (int) (duration / 3600);
        int minute = (int) ((duration - hour * 3600) / 60);
        int second = (int) (duration - hour * 3600 - minute * 60);

        String hourValue = "";
        String minuteValue;
        String secondValue;
        if (hour > 0) {
            if (hour >= 10) {
                hourValue = Integer.toString(hour);
            } else {
                hourValue = "0" + hour;
            }
            hourValue += ":";
        }
        if (minute > 0) {
            if (minute >= 10) {
                minuteValue = Integer.toString(minute);
            } else {
                minuteValue = "0" + minute;
            }
        } else {
            minuteValue = "00";
        }
        minuteValue += ":";
        if (second > 0) {
            if (second >= 10) {
                secondValue = Integer.toString(second);
            } else {
                secondValue = "0" + second;
            }
        } else {
            secondValue = "00";
        }
        return hourValue + minuteValue + secondValue;
    }
}