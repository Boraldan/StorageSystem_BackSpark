package boraldan.backspark.storagesystem.tool.builder;

import boraldan.backspark.storagesystem.domen.Sock;


public class SockBuilder {

    private final Sock sock;

    private SockBuilder() {
        sock = new Sock();
    }

    public static SockBuilder getBuilder() {
        return new SockBuilder();
    }

    public SockBuilder color(String color) {
        this.sock.setColor(color);
        return this;
    }

    public SockBuilder model(String model) {
        this.sock.setModel(model != null ? model : "-");
        return this;
    }

    public SockBuilder cottonPercentage(Integer cottonPercentage) {
        this.sock.setCottonPercentage(cottonPercentage != null ? cottonPercentage : 0);
        return this;
    }

    public SockBuilder quantity(Integer quantity) {
        this.sock.setQuantity(quantity != null ? quantity : 0) ;
        return this;
    }

    public SockBuilder isActive(Boolean isActive) {
        this.sock.setIsActive(isActive);
        return this;
    }

    public Sock build() {
        return this.sock;
    }
}

