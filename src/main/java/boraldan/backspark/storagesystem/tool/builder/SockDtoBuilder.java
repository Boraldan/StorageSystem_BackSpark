package boraldan.backspark.storagesystem.tool.builder;

import boraldan.backspark.storagesystem.tool.dto.SockDto;

public class SockDtoBuilder {

    private final SockDto sockDto;

    private SockDtoBuilder() {
        sockDto = new SockDto();
    }

    public static SockDtoBuilder getBuilder() {
        return new SockDtoBuilder();
    }

    public SockDtoBuilder model(String model) {
        this.sockDto.setModel(model != null ? model : "-");
        return this;
    }

    public SockDtoBuilder color(String color) {
        this.sockDto.setColor(color);
        return this;
    }

    public SockDtoBuilder cottonPercentage(Integer cottonPercentage) {
        this.sockDto.setCottonPercentage(cottonPercentage != null ? cottonPercentage : 0);
        return this;
    }

    public SockDtoBuilder quantity(Integer quantity) {
        this.sockDto.setQuantity(quantity != null ? quantity : 0);
        return this;
    }

    public SockDtoBuilder isActive(Boolean isActive) {
        this.sockDto.setIsActive(isActive != null ? isActive : false);
        return this;
    }

    public SockDto build() {
        return this.sockDto;
    }
}
