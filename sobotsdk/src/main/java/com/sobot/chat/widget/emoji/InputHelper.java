/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sobot.chat.widget.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.widget.EditText;

import com.sobot.chat.utils.ResourceUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kymjs (http://www.kymjs.com)
 */
public class InputHelper {
    public static void backspace(EditText editText) {
        if (editText == null) {
            return;
        }
        //模仿软键盘实现软键盘的删除功能
        int  keyCode = KeyEvent.KEYCODE_DEL;
        KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        editText.onKeyDown(keyCode, keyEventDown);
        editText.onKeyUp(keyCode, keyEventUp);
    }

    /**
     * 获取name对应的资源
     *
     * @param context
     * @param name
     * @return
     */

    public static int getEmojiResId(Context context, String name) {
        Map<String, Integer> mapAll = DisplayRules.getMapAll(context);
        if (mapAll.size() > 0) {
            Integer res = mapAll.get(name);
            if (res != null) {
                return res.intValue();
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Support OSChina Client，due to the need to support both 2 Format<br>
     * (I'm drunk, I go home)
     */
    public static Spannable displayEmoji(Context context, CharSequence s) {
        String str = s.toString();
        Spannable spannable = null;
        if (s instanceof Spannable) {
            spannable = (Spannable) s;
        } else {
            // 构建文字span
            spannable = new SpannableString(str);
        }
        Resources res = context.getResources();

        // a>，[大兵] </body> :smile:
        int bound = (int) res.getDimension(ResourceUtils.getIdByName(context, "dimen",
                "sobot_text_font_large"));
        String regex = "\\[[^\\]^\\[]+\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(spannable);
        while (m.find()) {
            String group = m.group();
//            LogUtils.i("aaa:"+group+"  "+m.start()+"  "+m.end());
            try { // [大兵]
                String emojiStr = m.group();
                int resId = getEmojiResId(context, emojiStr); // 通过字符串得到图片资源id
                if (resId > 0) {
                    // 构建图片span
                    Drawable drawable = res.getDrawable(resId);
                    drawable.setBounds(0, 0, bound, bound + 0);

                    // 将Drawable封装到ImageSpan中
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

                    // 替换指定位置内容为图片ImageSpan
                    spannable.setSpan(span, m.start(), m.end(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
            }
        }

        return spannable;
    }

    public static void input2OSC(EditText editText, EmojiconNew emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            // 没有多选时，直接在当前光标处添加
            editText.append(emojicon.getEmojiCode());
        } else {
            // 将已选中的部分替换为表情(当长按文字时会多选刷中很多文字)
            String str = emojicon.getEmojiCode();
            editText.getText().replace(Math.min(start, end),
                    Math.max(start, end), str, 0, str.length());
        }
    }
}