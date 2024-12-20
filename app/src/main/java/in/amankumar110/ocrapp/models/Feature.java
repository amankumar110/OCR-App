package in.amankumar110.ocrapp.models;

public class Feature {

    private String title,description;
    private int imageSource;

    public Feature(String title, String description, int imageSource) {
        this.title = title;
        this.description = description;
        this.imageSource = imageSource;
    }

    public Feature() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }
}
