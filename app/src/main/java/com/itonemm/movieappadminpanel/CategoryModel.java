package com.itonemm.movieappadminpanel;

public class CategoryModel {
    public String categoryname;

    public CategoryModel(String categoryname) {
        this.categoryname = categoryname;
    }

    public CategoryModel() {
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }
}
