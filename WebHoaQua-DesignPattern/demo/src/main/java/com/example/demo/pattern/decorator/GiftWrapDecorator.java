package com.example.demo.pattern.decorator;

public class GiftWrapDecorator extends FruitDecorator {
    public GiftWrapDecorator(Fruit fruit) {
        super(fruit);
    }

    @Override
    public String getDescription() {
        return fruit.getDescription() + " + goi qua";
    }

    @Override
    public double getPrice() {
        return fruit.getPrice() + 10000;
    }
}
