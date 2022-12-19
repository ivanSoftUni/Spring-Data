package exam.model.dtos;

import exam.constants.WarrantyType;
import exam.model.entities.Shop;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.*;
import java.math.BigDecimal;

public class LaptopImportDto {

    @Size(min = 8)
    private String macAddress;

    @Positive
    private Double cpuSpeed;

    @Min(8)
    @Max(128)
    private Integer ram;

    @Min(128)
    @Max(1024)
    private Integer storage;

    @Size(min = 10)
    private String description;

    @Positive
    private BigDecimal price;

    @Enumerated(EnumType.ORDINAL)
    private WarrantyType warrantyType;

    @NotNull
    private NameDto shop;

    public LaptopImportDto() {
    }

    public LaptopImportDto(String macAddress,
                           Double cpuSpeed,
                           Integer ram,
                           Integer storage,
                           String description,
                           BigDecimal price,
                           WarrantyType warrantyType,
                           NameDto shop) {
        this.macAddress = macAddress;
        this.cpuSpeed = cpuSpeed;
        this.ram = ram;
        this.storage = storage;
        this.description = description;
        this.price = price;
        this.warrantyType = warrantyType;
        this.shop = shop;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(Double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public WarrantyType getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(WarrantyType warrantyType) {
        this.warrantyType = warrantyType;
    }

    public NameDto getShop() {
        return shop;
    }

    public void setShop(NameDto shop) {
        this.shop = shop;
    }
}
