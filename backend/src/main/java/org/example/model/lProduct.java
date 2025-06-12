package org.example.model;

public class lProduct {
    private long id;
    private String image;
    private String title;
    private long price;
    private String category;
    private String description;

    public lProduct(long id, String image, String title, long price, String category, String description) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    //Standard Getter & Setter
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public long getPrice() {return price;}
    public void setPrice(long price) {this.price = price;}
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", image='" + image +'\'' +
                ", title='" + title + '\'' +
                ", price'" + price + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean isValid() {
        return (id > 0 && price > 0 && isCategoryValid());
    }

    //WIP
    public boolean isCategoryValid() {
        return true;
    }

    //返回this的拷贝
    public lProduct copy() {
        return new lProduct(id,image,title,price,category,description);
    }

    //从newlProduct拷贝除id的内容到this
    public void copyFrom(lProduct newlProduct) {
        image = newlProduct.getImage();
        title = newlProduct.getTitle();
        price = newlProduct.getPrice();
        category = newlProduct.getCategory();
        description = newlProduct.getDescription();
    }
}
