package cn.com.pplo.sicauhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by winson on 2014/10/7.
 */
public class News implements Parcelable {
    private int id;
    private String title;
    private String url;
    private String category;
    private String content;
    private String src;
    private String date;

    public News() {
    }

    private News(Parcel in) {
        id = in.readInt();
        title = in.readString();
        url = in.readString();
        category = in.readString();
        content = in.readString();
        src = in.readString();
        date = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", src='" + src + '\'' +
                ", date='" + date + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(category);
        dest.writeString(content);
        dest.writeString(src);
        dest.writeString(date);
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
