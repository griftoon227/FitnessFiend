package com.example.grift.fitnessfiend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RecipeListItem implements Parcelable {
    private String name;
    private String picture;
    private String source;
    private ArrayList<String> ingredientsList;
    private ArrayList<Nutrient> nutrientList;

    RecipeListItem(String n, String p, String s, ArrayList<Nutrient> nut, ArrayList<String> ing){
        name = n;
        picture = p;
        source = s;
        nutrientList = nut;
        ingredientsList = ing;
    }

    private RecipeListItem(Parcel in) {
        name = in.readString();
        picture = in.readString();
        source = in.readString();
        ingredientsList = in.createStringArrayList();
        nutrientList = in.createTypedArrayList(Nutrient.CREATOR);
    }

    public static final Creator<RecipeListItem> CREATOR = new Creator<RecipeListItem>() {
        @Override
        public RecipeListItem createFromParcel(Parcel in) {
            return new RecipeListItem(in);
        }

        @Override
        public RecipeListItem[] newArray(int size) {
            return new RecipeListItem[size];
        }
    };

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    String getPicture() { return picture; }

    public String getSource() {return source;}

    public void setSource(String s) {source = s;}

    ArrayList<String> getIngredientsList(){return ingredientsList;}

    ArrayList<Nutrient> getNutrientsList(){return nutrientList;}

    String getIngredient(int index) {return ingredientsList.get(index); }

    Nutrient getNutrient(int index) {return nutrientList.get(index);}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(source);
        dest.writeStringList(ingredientsList);
        dest.writeTypedList(nutrientList);
    }

    public static class Nutrient implements Parcelable {
        private String name;
        private double quantity;
        private String unit;

        Nutrient(String n, double q, String u){
            name = n;
            quantity = q;
            unit = u;
        }

        Nutrient(Parcel in) {
            name = in.readString();
            quantity = in.readDouble();
            unit = in.readString();
        }

        public static final Creator<Nutrient> CREATOR = new Creator<Nutrient>() {
            @Override
            public Nutrient createFromParcel(Parcel in) {
                return new Nutrient(in);
            }

            @Override
            public Nutrient[] newArray(int size) {
                return new Nutrient[size];
            }
        };

        public String getName() {return name;}

        public void setName(String n) {name = n;}

        double getQuantity() {return quantity;}

        String getUnit() {return unit;}

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeDouble(quantity);
            dest.writeString(unit);
        }
    }
}
