package com.lubotin.serega.pictureapplication.model;

import androidx.annotation.NonNull;

public class Image {
    private UploadedImage data;
    private String imageUri;

    public Image(String imageUri) {
        this.imageUri = imageUri;
    }

    public UploadedImage getData() {
        return data;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setData(UploadedImage data) {
        this.data = data;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "imageUri=" + imageUri +
                "\ndata=" + data.toString() +
                '}';
    }

    public static class UploadedImage {
        private String link;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return "UploadedImage{" +
                    "link='" + link + '\'' +
                    '}';
        }
    }
}
