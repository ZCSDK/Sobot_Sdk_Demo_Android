package com.sobot.chat.widget.emoji;

/**
 * @author kymjs (http://www.kymjs.com)
 */
public class EmojiconNew {
    private String emojiCode;
    private String emojiDes;

    public EmojiconNew(String emojiCode, String emojiDes) {
        this.emojiCode = emojiCode;
        this.emojiDes = emojiDes;
    }

    public String getEmojiCode() {
        return emojiCode;
    }

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public String getEmojiDes() {
        return emojiDes;
    }

    public void setEmojiDes(String emojiDes) {
        this.emojiDes = emojiDes;
    }
}