package ar.edu.itba.paw.model;

public class Image {

    private final long imageId;
    private final byte[] bytes;

    public Image(long imageId, byte[] bytes) {
        this.imageId = imageId;
        this.bytes = bytes;
    }

    public long getImageId() {
        return imageId;
    }

    public byte[] getBytes() {
        return bytes;
    }
}