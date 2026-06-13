package com.example.demo.pattern.decorator;

// Decorator Pattern: bổ sung đóng gói/quà tặng cho sản phẩm mà không sửa lớp gốc
public abstract class FruitDecorator implements Fruit {
    protected Fruit fruit;

    public FruitDecorator(Fruit fruit) {
        this.fruit = fruit;
    }
}
