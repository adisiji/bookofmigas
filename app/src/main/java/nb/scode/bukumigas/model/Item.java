package nb.scode.bukumigas.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import nb.scode.bukumigas.constants.Constants;


public class Item extends Application implements Constants, Parcelable {

    private long id;
    private int createAt, likesCount, categoryId;
    private String timeAgo, date, categoryTitle, itemTitle, itemDescription, itemContent, imgUrl;
    private Boolean myLike;

    public Item() {

    }

    public Item(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setContent(jsonData.getString("itemContent"));
                this.setTitle(jsonData.getString("itemTitle"));
                this.setDescription(jsonData.getString("itemDesc"));
                this.setCategoryTitle(jsonData.getString("categoryTitle"));
                this.setCategoryId(jsonData.getInt("category"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setMyLike(jsonData.getBoolean("myLike"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));

            }

        } catch (Throwable t) {

            Log.e("Item", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Item", jsonData.toString());
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }


    public void setCategoryId(int categoryId) {

        this.categoryId = categoryId;
    }

    public void setCreateAt(int createAt) {
        this.createAt = createAt;
    }


    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }


    public String getContent() {

        return itemContent;
    }

    public void setContent(String itemContent) {

        this.itemContent = itemContent;
    }

    public String getTitle() {

        return itemTitle;
    }

    public void setTitle(String itemTitle) {

        this.itemTitle = itemTitle;
    }

    public void setDescription(String itemDescription) {

        this.itemDescription = itemDescription;
    }


    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }


    public Boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(Boolean myLike) {

        this.myLike = myLike;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemTitle);
        dest.writeString(date);
        dest.writeInt(myLike ? 1 : 0);
        dest.writeString(imgUrl);
        dest.writeString(itemContent);
    }

    public Item (Parcel in){
        this.itemTitle = in.readString();
        this.date = in.readString();
        this.myLike = in.readInt() != 0;
        this.imgUrl = in.readString();
        this.itemContent = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Item createFromParcel(Parcel in) {

            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
