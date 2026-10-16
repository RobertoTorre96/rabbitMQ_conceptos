package org.example.springboot_rabbitmq.json;

public class Pedido {

    private int id;
    private String producto;
    private double precio;

    // 1. Constructor vacío (Obligatorio para Jackson)
    public Pedido() {
    }

    // 2. Constructor para nosotros
    public Pedido(int id, String producto, double precio) {
        this.id = id;
        this.producto = producto;
        this.precio = precio;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return "Pedido{id=" + id + ", producto='" + producto + "', precio=$" + precio + "}";
    }
}