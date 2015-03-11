package dv106.lnu.themediaplayer.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable{
	
	private String _ID;
	private String DATA;
	private String DURATION;
	private String DISPLAY_NAME;
	private String RESOLUTION;
	private String SIZE;
	private String TITLE;
	private String DATE_ADDED;
	
	public Video(){}

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public String getDATA() {
		return DATA;
	}

	public void setDATA(String dATA) {
		DATA = dATA;
	}

	public String getDURATION() {
		return DURATION;
	}

	public void setDURATION(String dURATION) {
		DURATION = dURATION;
	}

	public String getDISPLAY_NAME() {
		return DISPLAY_NAME;
	}

	public void setDISPLAY_NAME(String dISPLAY_NAME) {
		DISPLAY_NAME = dISPLAY_NAME;
	}

	public String getRESOLUTION() {
		return RESOLUTION;
	}

	public void setRESOLUTION(String rESOLUTION) {
		RESOLUTION = rESOLUTION;
	}

	public String getSIZE() {
		return SIZE;
	}

	public void setSIZE(String sIZE) {
		SIZE = sIZE;
	}

	public String getTITLE() {
		return TITLE;
	}

	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}

	public String getDATE_ADDED() {
		return DATE_ADDED;
	}

	public void setDATE_ADDED(String dATE_ADDED) {
		DATE_ADDED = dATE_ADDED;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(DATA);
        dest.writeString(DURATION);
        dest.writeString(DISPLAY_NAME);
        dest.writeString(RESOLUTION);
        dest.writeString(SIZE);
        dest.writeString(TITLE);
        dest.writeString(DATE_ADDED);
    }

    private Video(Parcel in){
        this._ID = in.readString();
        this.DATA = in.readString();
        this.DURATION = in.readString();
        this.DISPLAY_NAME = in.readString();
        this.RESOLUTION = in.readString();
        this.SIZE = in.readString();
        this.TITLE = in.readString();
        this.DATE_ADDED = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR
            = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
