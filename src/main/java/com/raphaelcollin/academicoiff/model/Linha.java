package com.raphaelcollin.academicoiff.model;

import javafx.beans.property.SimpleStringProperty;

public class Linha {
    private SimpleStringProperty c1;
    private SimpleStringProperty c2;
    private SimpleStringProperty c3;
    private SimpleStringProperty c4;
    private SimpleStringProperty c5;
    private SimpleStringProperty c6;
    private SimpleStringProperty c7;
    private SimpleStringProperty c8;
    private SimpleStringProperty c9;
    private SimpleStringProperty c10;
    private SimpleStringProperty c11;
    private SimpleStringProperty c12;
    private SimpleStringProperty c13;
    private SimpleStringProperty c14;
    private SimpleStringProperty c15;
    private SimpleStringProperty c16;
    private SimpleStringProperty c17;
    private SimpleStringProperty c18;
    private SimpleStringProperty c19;
    private SimpleStringProperty c20;
    private SimpleStringProperty c21;
    private SimpleStringProperty c22;
    private SimpleStringProperty c23;

    public Linha(String c1, String c2, String c3, String c4, String c5, String c6, String c7, String c8,
                 String c9, String c10, String c11, String c12, String c13, String c14, String c15,
                 String c16, String c17, String c18, String c19, String c20, String c21, String c22,
                 String c23) {
        this.c1 = new SimpleStringProperty(c1);
        this.c2 = new SimpleStringProperty(c2);
        this.c3 = new SimpleStringProperty(c3);
        this.c4 = new SimpleStringProperty(c4);
        this.c5 = new SimpleStringProperty(c5);
        this.c6 = new SimpleStringProperty(c6);
        this.c7 = new SimpleStringProperty(c7);
        this.c8 = new SimpleStringProperty(c8);
        this.c9 = new SimpleStringProperty(c9);
        this.c10 = new SimpleStringProperty(c10);
        this.c11 = new SimpleStringProperty(c11);
        this.c12 = new SimpleStringProperty(c12);
        this.c13 = new SimpleStringProperty(c13);
        this.c14 = new SimpleStringProperty(c14);
        this.c15 = new SimpleStringProperty(c15);
        this.c16 = new SimpleStringProperty(c16);
        this.c17 = new SimpleStringProperty(c17);
        this.c18 = new SimpleStringProperty(c18);
        this.c19 = new SimpleStringProperty(c19);
        this.c20 = new SimpleStringProperty(c20);
        this.c21 = new SimpleStringProperty(c21);
        this.c22 = new SimpleStringProperty(c22);
        this.c23 = new SimpleStringProperty(c23);
    }

    // Esse método recebe o número da coluna e retorna o seu respectivo texto

    public String get(int c){
        switch (c){
            case 1: return c1Property().get();
            case 2: return c2Property().get();
            case 3: return c3Property().get();
            case 4: return c4Property().get();
            case 5: return c5Property().get();
            case 6: return c6Property().get();
            case 7: return c7Property().get();
            case 8: return c8Property().get();
            case 9: return c9Property().get();
            case 10: return c10Property().get();
            case 11: return c11Property().get();
            case 12: return c12Property().get();
            case 13: return c13Property().get();
            case 14: return c14Property().get();
            case 15: return c15Property().get();
            case 16: return c16Property().get();
            case 17: return c17Property().get();
            case 18: return c18Property().get();
            case 19: return c19Property().get();
            case 20: return c20Property().get();
            case 21: return c21Property().get();
            case 22: return c22Property().get();
            case 23: return c23Property().get();

            default: return "";

        }
    }

    // Getters

    public SimpleStringProperty c1Property() {
        return c1;
    }

    public SimpleStringProperty c2Property() {
        return c2;
    }

    public SimpleStringProperty c3Property() {
        return c3;
    }

    public SimpleStringProperty c4Property() {
        return c4;
    }

    public SimpleStringProperty c5Property() {
        return c5;
    }

    public SimpleStringProperty c6Property() {
        return c6;
    }

    public SimpleStringProperty c7Property() {
        return c7;
    }

    public SimpleStringProperty c8Property() {
        return c8;
    }

    public SimpleStringProperty c9Property() {
        return c9;
    }

    public SimpleStringProperty c10Property() {
        return c10;
    }

    public SimpleStringProperty c11Property() {
        return c11;
    }

    public SimpleStringProperty c12Property() {
        return c12;
    }

    public SimpleStringProperty c13Property() {
        return c13;
    }

    public SimpleStringProperty c14Property() {
        return c14;
    }

    public SimpleStringProperty c15Property() {
        return c15;
    }

    public SimpleStringProperty c16Property() {
        return c16;
    }

    public SimpleStringProperty c17Property() {
        return c17;
    }

    public SimpleStringProperty c18Property() {
        return c18;
    }

    public SimpleStringProperty c19Property() {
        return c19;
    }

    public SimpleStringProperty c20Property() {
        return c20;
    }

    public SimpleStringProperty c21Property() {
        return c21;
    }

    public SimpleStringProperty c22Property() {
        return c22;
    }

    public SimpleStringProperty c23Property() {
        return c23;
    }


}
