package com.sobot.chat.widget.html;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;

import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.StringUtils;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 自定义的一html标签解析
 * html 中的span 标签需要替换成 自定义的sobotfont，因为高版本系统html.formhtml()已经识别了，就不会走自定义的
 */
public class SobotCustomTagHandler implements Html.TagHandler {

    private final String TAG = "CustomTagHandler";

    //标签
    public static final String NEW_FONT = "myfont";
    public static final String HTML_FONT = "font";

    public static final String NEW_SPAN = "sobotspan";
    public static final String HTML_SPAN = "span";

    private List<SobotHtmlLabelBean> labelBeanList = new ArrayList<>();//顺序添加的Bean
    private List<SobotHtmlLabelBean> tempRemoveLabelList = new ArrayList<>();


    private ColorStateList mOriginColors;
    private Context mContext;

    public SobotCustomTagHandler(Context context, ColorStateList originColors) {
        mContext = context;
        mOriginColors = originColors;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        try {
            processAttributes(xmlReader);
            if (tag.equalsIgnoreCase(NEW_SPAN) || tag.equalsIgnoreCase(NEW_FONT)) {
                if (opening) {
                    startFont(tag, output, xmlReader);
                } else {
                    endFont(tag, output, xmlReader);
                    attributes.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final HashMap<String, String> attributes = new HashMap<String, String>();

    private void processAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            /**
             * MSH: Look for supported attributes and add to hash map.
             * This is as tight as things can get :)
             * The data index is "just" where the keys and values are stored.
             */
            for (int i = 0; i < len; i++)
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
        } catch (Exception e) {
        }
    }

    public void startFont(String tag, Editable output, XMLReader xmlReader) {
        int startIndex = output.length();
        SobotHtmlLabelBean bean = new SobotHtmlLabelBean();
        bean.startIndex = startIndex;
        bean.tag = tag;

        String color = null;
        String size = null;
        //字体加粗的值CSS font-weight属性:,normal,bold,bolder,lighter,也可以指定的值(100-900,其中400是normal)
        //说这么多,这里只支持bold,如果是bold则加粗,否则就不加粗
        String fontWeight = null;
        if (NEW_FONT.equals(tag)) {
            color = attributes.get("color");
            size = attributes.get("size");

        } else if (NEW_SPAN.equals(tag)) {
            String colorstr = attributes.get("color");
            if (!TextUtils.isEmpty(colorstr)) {
                color = colorstr;
            }
            String sizestr = attributes.get("size");
            if (!TextUtils.isEmpty(sizestr)) {
                size = sizestr;
            }

            String style = attributes.get("style");
            if (!TextUtils.isEmpty(style)) {
                analysisStyle(bean, style);
            }
        }
        labelBeanList.add(bean);
        //Log.d(TAG, "opening:开" + "tag:<" + tag + " startIndex:" + startIndex + " 当前遍历的开的集合长度:" + labelBeanList.size());
    }

    /**
     * 解析style属性
     *
     * @param style
     */
    private void analysisStyle(SobotHtmlLabelBean bean, String style) {
       // Log.e(TAG, "style：" + style);
        String[] attrArray = style.split(";");
        Map<String, String> attrMap = new HashMap<>();
        if (null != attrArray) {
            for (String attr : attrArray) {
                String[] keyValueArray = attr.split(":");
                if (null != keyValueArray && keyValueArray.length == 2) {
                    // 记住要去除前后空格
                    attrMap.put(keyValueArray[0].trim(), keyValueArray[1].trim());
                }
            }
        }
      //  Log.i(TAG, "attrMap：" + attrMap.toString());
        bean.color = attrMap.get("color");
        bean.fontSize = attrMap.get("font-size");
        bean.textdecoration = attrMap.get("text-decoration");
        bean.textdecorationline= attrMap.get("text-decoration-line");
        bean.backgroundColor = attrMap.get("background-color");
        bean.background = attrMap.get("background");
        bean.fontweight = attrMap.get("font-weight");
        bean.fontstyle = attrMap.get("font-style");
    }


    /**
     * 计算影响的范围
     *
     * @param bean
     */
    private void optBeanRange(SobotHtmlLabelBean bean) {

        if (bean.ranges == null) {
            bean.ranges = new ArrayList<>();
        }

        if (tempRemoveLabelList.size() == 0) {
            SobotHtmlLabelRangeBean range = new SobotHtmlLabelRangeBean();
            range.start = bean.startIndex;
            range.end = bean.endIndex;
            bean.ranges.add(range);
        } else {
            int size = tempRemoveLabelList.size();
            //逆向找到  第一个结束位置<=当前结束位置
            //逆向找到最后一个开始位置>=当前开始位置
            int endRangePosition = -1;
            int startRangePosition = -1;
            for (int i = size - 1; i >= 0; i--) {
                SobotHtmlLabelBean bean1 = tempRemoveLabelList.get(i);
                if (bean1.endIndex <= bean.endIndex) {
                    //找第一个
                    if (endRangePosition == -1)
                        endRangePosition = i;
                }
                if (bean1.startIndex >= bean.startIndex) {
                    //找最后一个,符合条件的都覆盖之前的
                    startRangePosition = i;
                }
            }
            if (startRangePosition != -1 && endRangePosition != -1) {
                SobotHtmlLabelBean lastBean = null;
                //有包含关系
                for (int i = startRangePosition; i <= endRangePosition; i++) {
                    SobotHtmlLabelBean removeBean = tempRemoveLabelList.get(i);
                    lastBean = removeBean;
                    SobotHtmlLabelRangeBean range;
                    if (i == startRangePosition) {
                        range = new SobotHtmlLabelRangeBean();
                        range.start = bean.startIndex;
                        range.end = removeBean.startIndex;
                        bean.ranges.add(range);
                    } else {
                        range = new SobotHtmlLabelRangeBean();
                        SobotHtmlLabelBean bean1 = tempRemoveLabelList.get(i - 1);
                        range.start = bean1.endIndex;
                        range.end = removeBean.startIndex;
                        bean.ranges.add(range);
                    }
                }
                SobotHtmlLabelRangeBean range = new SobotHtmlLabelRangeBean();
                range.start = lastBean.endIndex;
                range.end = bean.endIndex;
                bean.ranges.add(range);
            } else {
                //表示将要并列添加,那么影响的范围就是自己的角标范围
                SobotHtmlLabelRangeBean range = new SobotHtmlLabelRangeBean();
                range.start = bean.startIndex;
                range.end = bean.endIndex;
                bean.ranges.add(range);
            }
        }
    }


    public void endFont(String tag, Editable output, XMLReader xmlReader) {
        int stopIndex = output.length();
       // Log.d(TAG, "opening:关" + "tag:" + tag + "/> endIndex:" + stopIndex);
        int lastLabelByTag = getLastLabelByTag(tag);
        if (lastLabelByTag != -1) {
            SobotHtmlLabelBean bean = labelBeanList.get(lastLabelByTag);
            bean.endIndex = stopIndex;
            optBeanRange(bean);
         //   Log.d(TAG, "完整的TagBean解析完成:" + bean.toString());

            for (SobotHtmlLabelRangeBean range : bean.ranges) {
                String color = bean.color;
                String fontSize = bean.fontSize;
                String textdecoration = bean.textdecoration;
                String textdecorationline=bean.textdecorationline;
                String backgroundColor = bean.backgroundColor;
                String background = bean.background;
                String fontweight = bean.fontweight;
                String fontstyle = bean.fontstyle;
                //斜体
                if (!TextUtils.isEmpty(fontstyle) && ("italic".equalsIgnoreCase(fontstyle) || "oblique".equalsIgnoreCase(fontstyle))) {
                    output.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //粗体
                if (!TextUtils.isEmpty(fontweight) && StringUtils.isNumber(fontweight) && Integer.parseInt(fontweight) >= 700) {
                    output.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
                }
                if (!TextUtils.isEmpty(fontweight) && "bold".equalsIgnoreCase(fontweight)) {
                    output.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
                }
                //设置字体大小
                if (!TextUtils.isEmpty(fontSize)) {
                    fontSize = fontSize.split("px")[0];
                }
                if (!TextUtils.isEmpty(fontSize)) {
                    int fontSizePx = 16;
                    if (null != mContext) {
                        fontSizePx = ScreenUtils.sp2px(mContext, Integer.parseInt(fontSize));
                    }
                    output.setSpan(new AbsoluteSizeSpan(fontSizePx), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //不支持上划线overline，闪烁blink
                if (!TextUtils.isEmpty(textdecoration) && !textdecoration.equalsIgnoreCase("none") && !textdecoration.equalsIgnoreCase("overline") && !textdecoration.equalsIgnoreCase("blink")) {
                    if (textdecoration.equalsIgnoreCase("line-through")) {
                        //中划线
                        output.setSpan(new StrikethroughSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        output.setSpan(new UnderlineSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                //不支持上划线overline，闪烁blink
                if (!TextUtils.isEmpty(textdecorationline) && !textdecorationline.equalsIgnoreCase("none") && !textdecorationline.equalsIgnoreCase("overline") && !textdecorationline.equalsIgnoreCase("blink")) {
                    if (textdecorationline.equalsIgnoreCase("line-through")) {
                        //中划线
                        output.setSpan(new StrikethroughSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        output.setSpan(new UnderlineSpan(), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                //设置字体前景色
                if (!TextUtils.isEmpty(color)) {
                    if (color.startsWith("@")) {
                        Resources res = Resources.getSystem();
                        String name = color.substring(1);
                        int colorRes = res.getIdentifier(name, "color", "android");
                        if (colorRes != 0) {
                            output.setSpan(new ForegroundColorSpan(colorRes), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        try {
                            output.setSpan(new ForegroundColorSpan(parseHtmlColor(color)), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            reductionFontColor(range.start, stopIndex, output);
                        }
                    }
                }
                //设置字体背景色
                if (!TextUtils.isEmpty(backgroundColor)) {
                    output.setSpan(new BackgroundColorSpan(parseHtmlColor(backgroundColor)), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //设置字体背景色
                if (!TextUtils.isEmpty(background)) {
                    output.setSpan(new BackgroundColorSpan(parseHtmlColor(background)), range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            //从顺序添加的集合中删除已经遍历完结束标签
            labelBeanList.remove(lastLabelByTag);
            optRemoveByAddBean(bean);
        }
    }

    /**
     * 获取最后一个与当前tag匹配的Bean的位置
     * 从后往前找
     *
     * @param tag
     * @return
     */
    private int getLastLabelByTag(String tag) {
        for (int size = labelBeanList.size(), i = size - 1; i >= 0; i--) {
            if (!TextUtils.isEmpty(tag) &&
                    !TextUtils.isEmpty(labelBeanList.get(i).tag) &&
                    labelBeanList.get(i).tag.equals(tag)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 操作删除的Bean,将其添加到删除的队列中
     *
     * @param removeBean
     */
    private void optRemoveByAddBean(SobotHtmlLabelBean removeBean) {
        int isAdd = 0;
        for (int size = tempRemoveLabelList.size(), i = size - 1; i >= 0; i--) {
            SobotHtmlLabelBean bean = tempRemoveLabelList.get(i);
            if (removeBean.startIndex <= bean.startIndex && removeBean.endIndex >= bean.endIndex) {
                if (isAdd == 0) {
                    tempRemoveLabelList.set(i, removeBean);
                    isAdd = 1;
                } else {
                    //表示已经把isAdd = 1;当前删除的bean,添加到了删除队列中,如果再次找到了可以removeBean可以替代的bean,则删除
                    tempRemoveLabelList.remove(i);
                }

            }
        }
        if (isAdd == 0) {
            tempRemoveLabelList.add(removeBean);
        }

      //  Log.d(TAG, "已经删除的完整开关结点的集合长度:" + tempRemoveLabelList.size());
    }

    /**
     * 将dp单位的值转换为px为单位的值
     *
     * @param context  上下文对象
     * @param dipValue dp为单位的值
     * @return 返回转换后的px为单位的值
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    /**
     * 将px单位的值转换为dp为单位的值
     *
     * @param context 上下文对象
     * @param pxValue px为单位的值
     * @return 返回转换后的dp为单位的值
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //解析颜色，四类：red等特定指、#000000、rgb(255,0,0)、rgba(255,255,0,0)
    public static int parseHtmlColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            if (colorString.length() == 4) {
                StringBuilder sb = new StringBuilder("#");
                for (int i = 1; i < colorString.length(); i++) {
                    char c = colorString.charAt(i);
                    sb.append(c).append(c);
                }
                colorString = sb.toString();
            }
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                return 0x000000;
            }
            return (int) color;
        } else if (colorString.startsWith("rgb(") || colorString.startsWith("rgba(") && colorString.endsWith(")")) {
            colorString = colorString.substring(colorString.indexOf("(") + 1, colorString.indexOf(")"));
            colorString = colorString.replaceAll(" ", "");
            String[] colorArray = colorString.split(",");
            if (colorArray.length == 3) {
                return Color.argb(255, Integer.parseInt(colorArray[0]), Integer.parseInt(colorArray[1]), Integer.parseInt(colorArray[2]));
            } else if (colorArray.length == 4) {
                return Color.argb(Integer.parseInt(colorArray[3]), Integer.parseInt(colorArray[0]), Integer.parseInt(colorArray[1]), Integer.parseInt(colorArray[2]));
            }
        } else if ("red".equalsIgnoreCase(colorString.trim())) {
            return Color.RED;
        } else if ("blue".equalsIgnoreCase(colorString.trim())) {
            return Color.BLUE;
        } else if ("black".equalsIgnoreCase(colorString.trim())) {
            return Color.BLACK;
        } else if ("gray".equalsIgnoreCase(colorString.trim())) {
            return Color.GRAY;
        } else if ("green".equalsIgnoreCase(colorString.trim())) {
            return Color.GREEN;
        } else if ("yellow".equalsIgnoreCase(colorString.trim())) {
            return Color.YELLOW;
        } else if ("white".equalsIgnoreCase(colorString.trim())) {
            return Color.WHITE;
        }
        return 0x000000;
    }

    /**
     * 还原为原来的颜色
     *
     * @param startIndex
     * @param stopIndex
     * @param editable
     */
    private void reductionFontColor(int startIndex, int stopIndex, Editable editable) {
        if (null != mOriginColors) {
            editable.setSpan(new TextAppearanceSpan(null, 0, 0, mOriginColors, null),
                    startIndex, stopIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            editable.setSpan(new ForegroundColorSpan(0xff2b2b2b), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

}