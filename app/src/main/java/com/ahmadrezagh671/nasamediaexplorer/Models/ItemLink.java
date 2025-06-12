package com.ahmadrezagh671.nasamediaexplorer.Models;

public class ItemLink {

    String href,rel,render;
    int width,height,size;

    public ItemLink(String href, String rel, String render, int width, int height, int size) {
        this.href = href;
        this.rel = rel;
        this.render = render;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    @Override
    public String toString() {
        return "ItemLink{" +
                "href='" + href + '\'' +
                ", rel='" + rel + '\'' +
                ", render='" + render + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                '}';
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getRender() {
        return render;
    }

    public void setRender(String render) {
        this.render = render;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
