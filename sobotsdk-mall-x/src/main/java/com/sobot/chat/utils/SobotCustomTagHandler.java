package com.sobot.chat.utils;

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
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 自定义的一html标签解析
 * html 中的span 标签需要替换成 自定义的androidfont，因为高版本系统html.formhtml()已经识别了，就不会走自定义的
 */
public class SobotCustomTagHandler implements Html.TagHandler {

    private final String TAG = "CustomTagHandler";

    private int startIndex = 0;
    private int stopIndex = 0;

    private ColorStateList mOriginColors;
    private Context mContext;

    public SobotCustomTagHandler(Context context, ColorStateList originColors) {
        mContext = context;
        mOriginColors = originColors;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        processAttributes(xmlReader);

        if (tag.equalsIgnoreCase("androidfont")) {
            if (opening) {
                startSpan(tag, output, xmlReader);
            } else {
                endSpan(tag, output, xmlReader);
                attributes.clear();
            }
        }

    }

    public void startSpan(String tag, Editable output, XMLReader xmlReader) {
        startIndex = output.length();
    }

    public void endSpan(String tag, Editable output, XMLReader xmlReader) {
        stopIndex = output.length();

        String color = attributes.get("color");
        String size = attributes.get("size");
        String style = attributes.get("style");
        if (!TextUtils.isEmpty(style)) {
            analysisStyle(startIndex, stopIndex, output, style);
        }
        if (!TextUtils.isEmpty(size)) {
            size = size.split("px")[0];
        }
        if (!TextUtils.isEmpty(color)) {
            if (color.startsWith("@")) {
                Resources res = Resources.getSystem();
                String name = color.substring(1);
                int colorRes = res.getIdentifier(name, "color", "android");
                if (colorRes != 0) {
                    output.setSpan(new ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                try {
                    output.setSpan(new ForegroundColorSpan(Color.parseColor(color)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                    reductionFontColor(startIndex, stopIndex, output);
                }
            }
        }
        if (!TextUtils.isEmpty(size)) {
            int fontSizePx = 16;
            if (null != mContext) {
                fontSizePx = ScreenUtils.sp2px(mContext, Integer.parseInt(size));
            }
            output.setSpan(new AbsoluteSizeSpan(fontSizePx), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    /**
     * 解析style属性
     *
     * @param startIndex
     * @param stopIndex
     * @param editable
     * @param style
     */
    private void analysisStyle(int startIndex, int stopIndex, Editable editable, String style) {
        Log.e(TAG, "style：" + style);
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
        Log.e(TAG, "attrMap：" + attrMap.toString());

        String color = attrMap.get("color");
        String fontSize = attrMap.get("font-size");
        String textdecoration = attrMap.get("text-decoration");
        String backgroundColor = attrMap.get("background-color");

        if (!TextUtils.isEmpty(fontSize)) {
            fontSize = fontSize.split("px")[0];
        }
        if (!TextUtils.isEmpty(color)) {
            if (color.startsWith("@")) {
                Resources res = Resources.getSystem();
                String name = color.substring(1);
                int colorRes = res.getIdentifier(name, "color", "android");
                if (colorRes != 0) {
                    editable.setSpan(new ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                try {
                    if (color.startsWith("rgb")) {
                        editable.setSpan(new ForegroundColorSpan(rgbToInt(color, 0x000000)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reductionFontColor(startIndex, stopIndex, editable);
                }
            }
        }
        if (!TextUtils.isEmpty(fontSize)) {
            int fontSizePx = 16;
            if (null != mContext) {
                fontSizePx = ScreenUtils.sp2px(mContext, Integer.parseInt(fontSize));
            }
            editable.setSpan(new AbsoluteSizeSpan(fontSizePx), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(textdecoration)) {
            editable.setSpan(new UnderlineSpan(), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(backgroundColor)) {
            if (backgroundColor.startsWith("rgb")) {
                editable.setSpan(new BackgroundColorSpan(rgbToInt(backgroundColor, 0xFFFFFFFF)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


    public int rgbToInt(String rgb, int defColor) {
        int color = defColor;
        try {
            Matcher matcher = Pattern.compile("(?<=[\\(|\\[])[^\\)|\\]]+").matcher(rgb);
            String[] rgbArr = null;
            if (matcher.find()) {
                String tmp = matcher.group();
                rgbArr = tmp.split(",");
                if (rgbArr.length == 3) {
                    int a = Integer.parseInt(rgbArr[0].trim());
                    int b = Integer.parseInt(rgbArr[1].trim());
                    int c = Integer.parseInt(rgbArr[2].trim());
                    color = Color.rgb(Integer.parseInt(rgbArr[0].trim()), Integer.parseInt(rgbArr[1].trim()), Integer.parseInt(rgbArr[2].trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }
}