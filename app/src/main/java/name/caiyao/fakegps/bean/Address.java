package name.caiyao.fakegps.bean;

/**
 * Created by sky on 2017/3/11.
 */

public class Address {

    private double latitude;
    private double longitude;
    private int lac;
    private int cid;
    private String addname;


    public Address(double latitude, double longitude, int lac, int cid, String addname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lac = lac;
        this.cid = cid;
        this.addname = addname;
    }

    public Address() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getAddname() {
        return addname;
    }

    public void setAddname(String addname) {
        this.addname = addname;
    }

    @Override
    public String toString() {
        return "Address{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", lac=" + lac +
                ", cid=" + cid +
                ", addname='" + addname + '\'' +
                '}';
    }
}
