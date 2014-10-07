package cn.com.pplo.sicauhelper.model;

/**
 * Created by winson on 2014/10/7.
 */
public class News {
    private int id;
    private String title;
    private String url;
    private String category;
    private String content;
    private String src;

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

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", src='" + src + '\'' +
                '}';
    }
}
