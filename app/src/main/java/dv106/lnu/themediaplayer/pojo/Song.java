package dv106.lnu.themediaplayer.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable{
	private String _ID;
	private String ARTIST;
	private String TITLE;
	private String DATA;
	private String DISPLAY_NAME;
	private String DURATION;
	
	public Song(String _ID, String aRTIST, String tITLE, String dATA,
			String dISPLAY_NAME, String dURATION) {
		super();
		this._ID = _ID;
		ARTIST = aRTIST;
		TITLE = tITLE;
		DATA = dATA;
		DISPLAY_NAME = dISPLAY_NAME;
		DURATION = dURATION;
	}
	
	public Song() {}

	public String get_ID() {
		return _ID;
	}
	public void set_ID(String _ID) {
		this._ID = _ID;
	}
	public String getARTIST() {
		return ARTIST;
	}
	public void setARTIST(String aRTIST) {
		ARTIST = aRTIST;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getDATA() {
		return DATA;
	}
	public void setDATA(String dATA) {
		DATA = dATA;
	}
	public String getDISPLAY_NAME() {
		return DISPLAY_NAME;
	}
	public void setDISPLAY_NAME(String dISPLAY_NAME) {
		DISPLAY_NAME = dISPLAY_NAME;
	}
	public String getDURATION() {
		return DURATION;
	}
	public void setDURATION(String dURATION) {
		DURATION = dURATION;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_ID);
        dest.writeString(ARTIST);
        dest.writeString(DATA);
        dest.writeString(DISPLAY_NAME);
        dest.writeString(DURATION);
    }

    private Song(Parcel in){
        this._ID = in.readString();
        this.ARTIST = in.readString();
        this.DATA = in.readString();
        this.DISPLAY_NAME = in.readString();
        this.DURATION = in.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR
            = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
