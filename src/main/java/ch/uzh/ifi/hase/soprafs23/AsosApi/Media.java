package ch.uzh.ifi.hase.soprafs23.AsosApi;

import java.util.List;

public class Media {

    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Media{" +
                "images=" + images +
                '}';
    }
}
